package com.servicebio.compose.application.utils

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.servicebio.compose.application.R

class EmojiEngine {

    private val emojiMap: Map<String, Int> = mapOf(
        "[笑]" to R.drawable.img001
    )

    private val tokenRegex = Regex(emojiMap.keys.joinToString("|") { Regex.escape(it) })
    private val em = "\uFFFD"//'\u2003' // EM SPACE，用作占位

    val inlineContentMap: Map<String, InlineTextContent>
        @Composable get() = emojiMap.mapValues { (key, resId) ->
            InlineTextContent(
                placeholder = Placeholder(
                    18.sp,
                    18.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                val dpValue: Dp = with(LocalDensity.current) { 18.sp.toDp() }
                Image(painter = painterResource(resId), contentDescription = key, modifier = Modifier.size(dpValue))
            }
        }

    fun toAnnotatedString(text: String): AnnotatedString{
        return buildAnnotatedString {
            var lastIndex = 0
            val regex = Regex(emojiMap.keys.joinToString("|") { Regex.escape(it) })
            regex.findAll(text).forEach { match ->
                append(text.substring(lastIndex, match.range.first))
                appendInlineContent(match.value, em)
                lastIndex = match.range.last + 1
            }

            if (lastIndex < text.length) {
                append(text.substring(lastIndex))
            }
        }
    }

    /** VisualTransformation 保证光标对齐 */
    val visualTransformation: VisualTransformation = VisualTransformation { text ->
        val s = text.text
        Log.e("TAG", "visualTransformation = $s ")
        val matches = tokenRegex.findAll(s).toList()
        val transformedText = buildString {
            var last = 0
            matches.forEach { m ->
                append(s.substring(last, m.range.first))
                append(em) // 替换为占位符
                last = m.range.last + 1
            }
            if (last < s.length) append(s.substring(last))
        }
        Log.e("TAG", "transformedText = $transformedText ")

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var transformed = offset
                var shift = 0
                tokenRegex.findAll(s).forEach { m ->
                    val start = m.range.first
                    val end = m.range.last + 1
                    val length = end - start
                    if (offset in start until end) {
                        // 光标在 token 内，映射到 EM SPACE 的位置
                        transformed = start - shift + 1
                        return transformed.coerceIn(0, transformedText.length)
                    } else if (offset >= end) {
                        shift += (length - 1)
                    }
                }
                transformed -= shift
                return transformed.coerceIn(0, transformedText.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                var original = offset
                var shift = 0
                tokenRegex.findAll(s).forEach { m ->
                    val start = m.range.first
                    val end = m.range.last + 1
                    val length = end - start
                    val transformedStart = start - shift
                    if (offset in transformedStart until transformedStart + 1) {
                        // 光标在 EM SPACE → 映射到 token 末尾
                        return end
                    } else if (offset >= transformedStart + 1) {
                        shift += (length - 1)
                    }
                }
                original += shift
                return original.coerceIn(0, s.length)
            }
        }

        TransformedText(AnnotatedString(transformedText), mapping)
    }
}