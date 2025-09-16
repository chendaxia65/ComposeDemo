package com.servicebio.compose.application.emoji

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@Composable
fun AndroidEditText(
    controller: AndroidEditTextController,
    editEventFlow: SharedFlow<EditTextEvent>,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource,
    placeholder: String = "请输入内容…",
    initialText: String,
    onTextChange: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val scope = rememberCoroutineScope()

    val preFocus = remember { Ref<FocusInteraction.Focus>() }
    val emojiSize by remember { mutableIntStateOf(with(density) { 16.sp.toPx() }.toInt()) }
    val minHeight by remember { mutableIntStateOf(with(density) { 44.dp.toPx() }.toInt()) }
    val padding by remember { mutableIntStateOf(with(density) { 10.dp.toPx() }.toInt()) }
    var editAndroidView: AppCompatEditText? by remember { mutableStateOf(null) }

    val textWatcher = remember {
        EditTextWatcher(context, emojiSize) {
            onTextChange(it)
        }
    }

    LaunchedEffect(editEventFlow) {
        //接收外部的关于需要使用EditText的事件
        editEventFlow.collect { editTextEvent ->
            //外部需要触发键盘的删除事件
            if (editTextEvent is EditTextEvent.Delete) {
                val length = editAndroidView?.text?.length ?: 0
                if (length > 0) {
                    editAndroidView?.dispatchKeyEvent(
                        KeyEvent(
                            KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_DEL
                        )
                    )
                    editAndroidView?.dispatchKeyEvent(
                        KeyEvent(
                            KeyEvent.ACTION_UP,
                            KeyEvent.KEYCODE_DEL
                        )
                    )
                    //震动触感
                    editAndroidView?.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                }
            } else if (editTextEvent is EditTextEvent.Shock) {
                editAndroidView?.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            }
        }
    }

    AndroidView(
        factory = { ctx ->
            val editText = AppCompatEditText(ctx).also { editAndroidView = it }
            controller.bindEditText(editText)
            editText.textSize = 16f
//            editText.isFocusable = true
//            editText.isFocusableInTouchMode = true
            editText.minHeight = minHeight
            editText.setBackgroundColor(Color.TRANSPARENT)
            editText.setPadding(padding, padding, padding, padding)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                editText.isLocalePreferredLineHeightForMinimumUsed = false
            }
            editText.includeFontPadding = false
            editText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                Log.d("interactionSource", "hasFocus = $hasFocus")
                val interaction = if (hasFocus) {
                    FocusInteraction.Focus().also { preFocus.value = it }
                } else {
                    preFocus.value?.let { FocusInteraction.Unfocus(it) }
                }
                interaction?.let { scope.launch { interactionSource.emit(it) } }
            }

//            editText.setOnTouchListener { v, event ->
//                when (event.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        // 通知 Compose: 按下
//                        interactionSource.tryEmit(PressInteraction.Press(Offset.Zero))
//                    }
//
//                    MotionEvent.ACTION_UP -> {
//                        // 通知 Compose: 松开
//                        interactionSource.tryEmit(
//                            PressInteraction.Release(
//                                PressInteraction.Press(
//                                    Offset.Zero
//                                )
//                            )
//                        )
//                    }
//
//                    MotionEvent.ACTION_CANCEL -> {
//                        // 通知 Compose: 松开
//                        interactionSource.tryEmit(
//                            PressInteraction.Cancel(
//                                PressInteraction.Press(
//                                    Offset.Zero
//                                )
//                            )
//                        )
//                    }
//                }
//
//
//                return@setOnTouchListener false
//            }

            // 监听文本变化
            editText.addTextChangedListener(textWatcher)


            editText.setText(initialText)
            editText.setSelection(editText.text?.length ?: 0)

            editText
        },
        modifier = modifier.indication(interactionSource, indication = null),
        update = { editText ->
            controller.bindEditText(editText)
            editAndroidView = editText

            // 同步 Compose 状态到 EditText
            if (editText.text.toString() != initialText) {
                editText.setText(initialText)
                editText.setSelection(editText.text?.length ?: 0)
            }
        }
    )
}

class AndroidEditTextController() {
    private var editText: EditText? = null

    fun bindEditText(editText: EditText) {
        this.editText = editText
    }

    fun requestFocus() {
        editText?.requestFocus()
    }

    fun clearFocus() {
        editText?.clearFocus()
    }

    fun showSoftKeyboard() {
        requestFocus()
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

private class EditTextWatcher(
    private val context: Context,
    private val emojiSize: Int,
    private val onTextChanged: (String) -> Unit
) : TextWatcher {
    private var start = 0
    private var count = 0
    override fun beforeTextChanged(
        s: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        this.start = start
        this.count = count
    }

    override fun afterTextChanged(s: Editable?) {
        s ?: return

        EmojiHandler.instance.handler(context, s, emojiSize, start, count)
        // 同步 Compose 状态
        onTextChanged(s.toString())
    }
}