package com.servicebio.compose.application.emoji

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("ClickableViewAccessibility")
@Composable
fun AndroidText(textContent: String, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val emojiSize by remember { mutableIntStateOf(with(density) { 16.sp.toPx() }.toInt()) }

    AndroidView(modifier = modifier, factory = { context ->
        val textView = object : AppCompatTextView(context) {
            override fun setText(text: CharSequence?, type: BufferType?) {
                var spannableText = text
                if (!spannableText.isNullOrEmpty()) {
                    spannableText = SpannableStringBuilder(text)
                    EmojiHandler.instance.handler(
                        context,
                        spannableText,
                        emojiSize = emojiSize,
                        0,
                        -1
                    )
                }

                super.setText(spannableText, type)

            }
        }
        textView.textSize = 16f
        textView.text = textContent
        textView.setTextIsSelectable(false)
        textView.isClickable = false
        textView.isContextClickable = false
        textView.isLongClickable = false
        textView.isFocusable = false
        textView.isFocusableInTouchMode = false
        textView
    }, update = { textView ->

        if (textView.text != textContent) {
            textView.text = textContent
        }
    })
}

