package com.servicebio.compose.application.emoji

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.style.ImageSpan
import android.util.Log
import androidx.core.content.ContextCompat
import com.servicebio.compose.application.R

class EmojiHandler {

    companion object {
        val instance: EmojiHandler by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            EmojiHandler()
        }
    }


    fun handler(
        context: Context, text: Spannable?, emojiSize: Int, index: Int, length: Int
    ) {
        if (text == null) {
            return
        }
        val textLength = text.length
        val textLengthToProcessMax = textLength - index
        val textLengthToProcess =
            if (length < 0 || length > textLengthToProcessMax) textLength else length
        if (length > 1) {
            val oldSpans: Array<ImageSpan> = text.getSpans(
                index, index + length,
                ImageSpan::class.java
            )
            for (i in oldSpans.indices) {
                text.removeSpan(oldSpans[i])
            }
        }

        var skip: Int
        var i = index
        while (i < index + textLengthToProcess) {
            skip = 0
            var icon = 0

            val emojiModel = Emoji.instance.find(text, i)
            if (emojiModel != null) {
                icon = emojiModel.icon
                skip = emojiModel.emoji.length
            }

            Log.e("EmojiHandler", "handler: $emojiModel", )

            if (icon == 0) {
                val unicode = Character.codePointAt(text, i)
                skip = Character.charCount(unicode)
            }

            if (icon > 0) {
                val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.img001)
                drawable?.setBounds(0, 0, emojiSize, emojiSize)
                text.setSpan(
                    ImageSpan(drawable!!, ImageSpan.ALIGN_CENTER),
                    i,
                    i + skip,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            i += skip
        }
    }

}