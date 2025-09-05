package com.servicebio.compose.application.emoji

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan
import androidx.core.content.ContextCompat
import androidx.core.graphics.withSave

class EmojiImageSpan (
    private val drawable: Drawable,
    private val scaleFactor: Float = 1.15f
) : ImageSpan(drawable) {

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        // 计算缩放后的尺寸
        val textSize = paint.textSize
        val targetHeight = (textSize * scaleFactor).toInt()
        val scale = targetHeight.toFloat() / drawable.intrinsicHeight
        val targetWidth = (drawable.intrinsicWidth * scale).toInt()

        // 更新drawable边界
        drawable.setBounds(0, 0, targetWidth, targetHeight)

        // 对齐文本基线
        fm?.let {
            val pfm = paint.fontMetricsInt
            it.ascent = pfm.ascent
            it.descent = pfm.descent
            it.top = pfm.top
            it.bottom = pfm.bottom
        }

        return targetWidth
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        canvas.withSave {
            // 计算垂直偏移（使表情居中对齐文本）
            val transY = (bottom - top - drawable.bounds.height()) / 2 + top
            translate(x, transY.toFloat())
            drawable.draw(this)
        }
    }

    companion object {
        /**
         * 创建与文本大小匹配的ImageSpan
         * 直接使用Drawable，避免转换为Bitmap
         */
        fun create(
            context: Context,
            drawableRes: Int,
            textSize: Float
        ): EmojiImageSpan? {
           val drawable = ContextCompat.getDrawable(context,drawableRes) ?: return null
            drawable.apply {
                // 确保获取正确的 intrinsic 尺寸
                if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
                    // 对于无固定尺寸的Drawable，设置默认大小
                    setBounds(0, 0, textSize.toInt(), textSize.toInt())
                }
            }
            return EmojiImageSpan(drawable)
        }
    }
}