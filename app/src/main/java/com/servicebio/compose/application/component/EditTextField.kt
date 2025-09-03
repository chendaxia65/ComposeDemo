package com.servicebio.compose.application.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ImageSpan
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.servicebio.compose.application.R

@SuppressLint("ClickableViewAccessibility")
@Composable
fun EditTextField(
    controller: EditTextFieldController,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource,
    placeholder: String = "请输入内容…",
    initialText: String = "Hello[笑]World",
    onTextChange: (String) -> Unit = {}
) {
    var composeText by remember { mutableStateOf(initialText) }

    AndroidView(
        factory = { ctx ->
            val editText = EditText(ctx)
            controller.bindEditText(editText)
            editText.isFocusable = true
            editText.isFocusableInTouchMode = true
            editText.setBackgroundColor(Color.TRANSPARENT)
            editText.setText(initialText)
            editText.setSelection(editText.text.length)
            editText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                interactionSource.tryEmit(
                    if (hasFocus) FocusInteraction.Focus() else FocusInteraction.Unfocus(
                        FocusInteraction.Focus()
                    )
                )
            }

            editText.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 通知 Compose: 按下
                        interactionSource.tryEmit(PressInteraction.Press(Offset.Zero))
                    }

                    MotionEvent.ACTION_UP -> {
                        // 通知 Compose: 松开
                        interactionSource.tryEmit(
                            PressInteraction.Release(
                                PressInteraction.Press(
                                    Offset.Zero
                                )
                            )
                        )
                    }

                    MotionEvent.ACTION_CANCEL -> {
                        // 通知 Compose: 松开
                        interactionSource.tryEmit(
                            PressInteraction.Cancel(
                                PressInteraction.Press(
                                    Offset.Zero
                                )
                            )
                        )
                    }
                }


                return@setOnTouchListener false
            }

            // 监听文本变化
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    s ?: return

                    // 遍历文本，将 [icon] 替换成图片
                    val spannable = SpannableString(s.toString())
                    var index = spannable.indexOf("[笑]")
                    while (index >= 0) {
                        val end = index + "[笑]".length
                        val drawable: Drawable? = ContextCompat.getDrawable(ctx, R.drawable.img001)
                        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                        val imageSpan = ImageSpan(drawable!!, ImageSpan.ALIGN_BASELINE)
                        spannable.setSpan(imageSpan, index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        index = spannable.indexOf("[笑]", end)
                    }

                    // 避免重复触发，先移除 TextWatcher
                    editText.removeTextChangedListener(this)
                    val selection = editText.selectionStart
                    editText.setText(spannable)
                    editText.setSelection(selection.coerceAtMost(spannable.length))
                    editText.addTextChangedListener(this)

                    // 同步 Compose 状态
                    composeText = spannable.toString()
                    onTextChange(composeText)
                }
            })
            editText
        },
        modifier = modifier.indication(interactionSource, indication = null),
        update = { editText ->
            controller.bindEditText(editText)

            // 可以在这里同步 Compose 状态到 EditText
            if (editText.text.toString() != composeText) {
                editText.setText(composeText)
                editText.setSelection(editText.text.length)
            }
        }
    )
}

class EditTextFieldController() {
    private var editText: EditText? = null

    fun bindEditText(editText: EditText) {
        this.editText = editText
    }

    fun requestFocus() {
        editText?.requestFocus()
    }

    fun showSoftKeyboard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            editText?.windowInsetsController?.show(WindowInsets.Type.ime())
        } else {
            val context = editText?.context ?: return
            val manager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.showSoftInput(editText, 0)
        }
    }

    fun hideSoftKeyboard(clearFocus: Boolean = false) {
        if (clearFocus) editText?.clearFocus()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            editText?.windowInsetsController?.hide(WindowInsets.Type.ime())
        } else {
            val context = editText?.context ?: return
            val manager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(editText?.windowToken, 0)
        }
    }
}