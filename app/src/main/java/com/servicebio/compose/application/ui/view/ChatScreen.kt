package com.servicebio.compose.application.ui.view

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.servicebio.compose.application.R
import com.servicebio.compose.application.emoji.EditTextField
import com.servicebio.compose.application.emoji.EditTextFieldController
import com.servicebio.compose.application.component.KeyboardManager
import com.servicebio.compose.application.component.KeyboardSate
import com.servicebio.compose.application.component.monitorKeyboardHeight
import com.servicebio.compose.application.component.rememberKeyboardManager
import com.servicebio.compose.application.ext.noRippleClickable
import com.servicebio.compose.application.model.Conversation
import com.servicebio.compose.application.ui.theme.ComposeDemoTheme
import com.servicebio.compose.application.utils.EmojiEngine

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    conversation: Conversation?
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val currentPanel = remember { mutableStateOf(Panel.NONE) }
    val panelHeight by monitorKeyboardHeight()

    val keyboardManager = rememberKeyboardManager()
    val keyboardController = remember { EditTextFieldController() }
    val isImeVisible = WindowInsets.isImeVisible

    var textField by remember { mutableStateOf("中国[微笑]国庆") }


    val hidePanel = {
        Log.e("TAG", "ChatScreen: hidePanel")
        currentPanel.value = Panel.NONE
    }

    LaunchedEffect(Unit) {
        keyboardManager.addOnDirectionChangedListener {
            //如果键盘正在升起时，Panel是开启状态，就将Panel隐藏
            if (it == KeyboardManager.DIRECTION_UP && currentPanel.value == Panel.EMOJI) {
                hidePanel()
            }
            Log.e("TAG", "rememberKeyboardManager: Direction = $it")
        }
    }



    BackHandler(currentPanel.value == Panel.EMOJI) {
        Log.e("TAG", "ChatScreen: BackHandler")
        hidePanel()
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = conversation?.name ?: "Chat") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isImeVisible) {
                            keyboardController.hideSoftKeyboard(true)
                        }
                        navController.popBackStack()
                    }) {
                        Icon(
                            painter = rememberVectorPainter(Icons.AutoMirrored.Outlined.ArrowBack),
                            contentDescription = "",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                })
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Log.e("TAG", "ChatScreen: refresh ${currentPanel.value.name}")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        rememberPanelPadding2(
                            keyboardManager,
                            currentPanel.value == Panel.EMOJI
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0f)
                        .background(Color.Gray)
                )
                ChatInputArea2(
                    textField,
                    keyboardController,
                    currentPanel.value,
                    onInputTextChanged = {
                        textField = it
                    },
                    onSendMessage = {
                        Log.e("TAG", "onSendMessage: $textField")
                        textField = ""
                    },
                    onPanelChanged = { currentPanel.value = it })
            }
            if (currentPanel.value == Panel.EMOJI) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .height(panelHeight)
                        .background(MaterialTheme.colorScheme.surface)
                        .navigationBarsPadding()
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
private fun ChatInputArea2(
    textField: String,
    keyboardController: EditTextFieldController,
    currentPanel: Panel = Panel.NONE,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    onPanelChanged: (Panel) -> Unit
) {

    Log.e("TAG", "ChatScreen: ChatInputArea textField = $textField")
    var hasFocus by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    Log.d("interactionSource", "用户按下输入框")
                }

                is PressInteraction.Release -> {
                    Log.d("interactionSource", "用户点击抬起输入框（算是一次点击）hasFocus=$hasFocus")
                }

                is PressInteraction.Cancel -> {
                    Log.d("interactionSource", "用户按下后取消（比如滑走了）")
                }

                is FocusInteraction.Focus -> {
                    hasFocus = true
                    Log.d("interactionSource", "输入框获取了焦点")
                }

                is FocusInteraction.Unfocus -> {
                    hasFocus = false
                    Log.d("interactionSource", "输入框失去了焦点")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (textField.isNotEmpty()) {
            keyboardController.showSoftKeyboard()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        EditTextField(
            keyboardController,
            Modifier
                .weight(1.0f)
                .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                .heightIn(max = 150.dp), interactionSource = interactionSource,
            initialText = textField
        ) {
            onInputTextChanged(it)
        }

        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(32.dp)
                .noRippleClickable {
                    val emojiOpened = currentPanel == Panel.EMOJI
                    if (emojiOpened) {
                        if (hasFocus) {
                            keyboardController.showSoftKeyboard()
                        } else {
                            keyboardController.requestFocus()
                        }
                    } else {
                        if (!hasFocus) keyboardController.requestFocus()
                        keyboardController.hideSoftKeyboard()
                        onPanelChanged(Panel.EMOJI)
                    }
                }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_emoji_good),
                contentDescription = null,
                tint = if (currentPanel == Panel.EMOJI) MaterialTheme.colorScheme.primary else LocalContentColor.current
            )
        }

        AnimatedContent(
            targetState = textField.isNotEmpty(),
            contentAlignment = Alignment.Center
        ) { state ->
            if (state) {
                Row {
                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(
                        onClick = {
                            onSendMessage()
                        },
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(text = "发送", color = Color.White)
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
private fun ChatInputArea(
    currentPanel: Panel = Panel.NONE,
    onSendMessage: (AnnotatedString) -> Unit,
    onPanelChanged: (Panel) -> Unit
) {

    Log.e("TAG", "ChatScreen: ChatInputArea")
    // 键盘控制器
    val keyboardController = LocalSoftwareKeyboardController.current

    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }


    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    Log.d("interactionSource", "用户按下输入框")
                }

                is PressInteraction.Release -> {
                    Log.d("interactionSource", "用户点击抬起输入框（算是一次点击）hasFocus=$hasFocus")
//                    if (hasFocus) {
//                        onPanelChanged(Panel.KEYBOARD)
//                    }
                }

                is PressInteraction.Cancel -> {
                    Log.d("interactionSource", "用户按下后取消（比如滑走了）")
                }

                is FocusInteraction.Focus -> {
//                    onPanelChanged(false, Panel.KEYBOARD)
                    hasFocus = true
                    Log.d("interactionSource", "输入框获取了焦点")
                }

                is FocusInteraction.Unfocus -> {
                    hasFocus = false
                    Log.d("interactionSource", "输入框失去了焦点")
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val emojiEngine = remember { EmojiEngine() }

        var textField by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue())
        }

        val annotatedText = remember(textField.text) {
            emojiEngine.toAnnotatedString(textField.text)
        }

        BasicTextField(
            textField,
            modifier = Modifier
                .weight(1.0f)
                .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                .heightIn(max = 150.dp)
//                .focusable(enabled = false)//focusable() 的调用必须在focusRequester(..)之前
                .focusRequester(focusRequester)
                .padding(horizontal = 10.dp, vertical = 12.dp),
            onValueChange = { textField = it },
            interactionSource = interactionSource,
            visualTransformation = emojiEngine.visualTransformation,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                lineHeight = 20.sp
            ),
            decorationBox = { innerTextFiled ->
                Box {
                    Text(
                        text = annotatedText,
                        inlineContent = emojiEngine.inlineContentMap,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            lineHeight = 20.sp
                        ),
                        softWrap = true
                    )
                    innerTextFiled()
                }
            }
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(32.dp)
                .noRippleClickable {
                    val emojiOpened = currentPanel == Panel.EMOJI
                    if (emojiOpened) {
                        if (hasFocus) {
                            keyboardController?.show()
//                            onPanelChanged(Panel.KEYBOARD)
                        } else {
                            focusRequester.requestFocus()
                        }
                    } else {
                        if (!hasFocus) focusRequester.requestFocus()
                        keyboardController?.hide()
                        onPanelChanged(Panel.EMOJI)
                    }
                }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_emoji_good),
                contentDescription = null,
                tint = if (currentPanel == Panel.EMOJI) MaterialTheme.colorScheme.primary else LocalContentColor.current
            )
        }

        AnimatedContent(
            targetState = textField.text.isNotEmpty(),
            contentAlignment = Alignment.Center
        ) { state ->
            if (state) {
                Row {
                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(
                        onClick = {
                            onSendMessage(textField.annotatedString)
                            textField = TextFieldValue(text = "")
                        },
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(text = "发送", color = Color.White)
                    }
                }
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ComposeDemoTheme {
        val navController = rememberNavController()
        ChatScreen(navController, null)
    }
}

@Preview(showBackground = true)
@Composable
fun InputAreaPreview() {
    ComposeDemoTheme {
        ChatInputArea(Panel.EMOJI, onSendMessage = {}) { panel ->

        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun rememberPanelPadding2(
    keyboardManager: KeyboardManager,
    isPanelOpened: Boolean
): PaddingValues {
    val density = LocalDensity.current
    val navigationBars = WindowInsets.navigationBars

    val panelRef = remember { Ref<Boolean>().apply { value = false } }

    val keyboardStateRef = remember {
        Ref<KeyboardSate>().apply {
            value = KeyboardSate.of(density, navigationBars, 336.dp)
        }
    }


    val keyboardState = keyboardStateRef.value!!

    val imeBottom by keyboardManager.height

    LaunchedEffect(keyboardManager) {
        keyboardManager.addOnAnimationEndListener {
            if (it > 0.dp) {
                if (keyboardState.imeHeight != it) {
                    keyboardStateRef.value =
                        KeyboardSate.of(density, navigationBars, it)
                }
                //键盘完全升起，说明Panel已经不显示了
                panelRef.value = false
            }
        }
    }

    if (isPanelOpened) panelRef.value = true

    val oldPanelState = panelRef.value ?: false

    val navBottom = WindowInsets.navigationBars.asPaddingValues()
    val imeHeight = (imeBottom - navBottom.calculateBottomPadding()).coerceAtLeast(0.dp)

    Log.e("TAG", "ChatScreen: imeHeight $imeHeight")


    //如果是打开Panel 或者 在键盘显示之前Panel已经是开启状态，就使用固定Padding
    val height = if (isPanelOpened || (WindowInsets.isImeVisible && oldPanelState)) {//
        if (imeHeight > keyboardState.imePadding) imeHeight else keyboardState.imePadding
    } else {
        imeHeight
    }

    //如果padding 为0时 说明Panel或键盘都已经收起了 恢复到默认状态
    if (height == 0.dp) panelRef.value = false

    return PaddingValues(bottom = height)
}


private enum class Panel {
    EMOJI,
    NONE
}







