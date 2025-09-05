package com.servicebio.compose.application.ui.view

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.servicebio.compose.application.R
import com.servicebio.compose.application.component.KeyboardManager
import com.servicebio.compose.application.component.KeyboardSate
import com.servicebio.compose.application.component.monitorKeyboardHeight
import com.servicebio.compose.application.component.rememberKeyboardManager
import com.servicebio.compose.application.emoji.EditTextEvent
import com.servicebio.compose.application.emoji.EditTextField
import com.servicebio.compose.application.emoji.EditTextFieldController
import com.servicebio.compose.application.emoji.Emoji
import com.servicebio.compose.application.ext.noRippleClickable
import com.servicebio.compose.application.model.Conversation
import com.servicebio.compose.application.route.Route
import com.servicebio.compose.application.ui.theme.ComposeDemoTheme
import com.servicebio.compose.application.utils.EmojiEngine
import com.servicebio.compose.application.viewmodel.ChatViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    conversation: Conversation?,
    viewModel: ChatViewModel = viewModel<ChatViewModel>()
) {

    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val currentPanel = rememberSaveable { mutableStateOf(Panel.NONE) }
    val panelHeight by monitorKeyboardHeight()

    val keyboardManager = rememberKeyboardManager()
    val keyboardController = remember { EditTextFieldController() }
    val isImeVisible = WindowInsets.isImeVisible
    var isResumedFromBackground by rememberSaveable { mutableStateOf(false) }

    var textField by rememberSaveable { mutableStateOf("中国[发财啦]国庆") }
    var isKeyboardLastShown by rememberSaveable { mutableStateOf(textField.isNotEmpty()) }

    val emojis by rememberSaveable { mutableStateOf(Emoji.instance.emojiIcons) }
    val lazyGridState = rememberLazyGridState()
    val editEvenFlow = remember { MutableSharedFlow<EditTextEvent>() }
    val scope = rememberCoroutineScope()

    val hidePanel = { currentPanel.value = Panel.NONE }

    val window = (LocalContext.current as? Activity)?.window

    LaunchedEffect(Unit) {
        keyboardManager.addOnDirectionChangedListener {
            //记录最后一次键盘的显示状态
            isKeyboardLastShown = it == KeyboardManager.DIRECTION_UP

            //如果键盘正在升起时，Panel是开启状态，就将Panel隐藏
            if (isKeyboardLastShown && currentPanel.value == Panel.EMOJI) {
                hidePanel()
            }
            Log.e("TAG", "rememberKeyboardManager,InsetsController: Direction = $it")
        }

        if (textField.isNotEmpty()) {
            keyboardController.requestFocus()
            //跳转页面再返回当前页面时，UI被重构，所以需要限制软键盘的显示
            if (isKeyboardLastShown) keyboardController.showSoftKeyboard()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (isResumedFromBackground) {
                    Log.e(
                        "TAG",
                        "lifecycleOwner,InsetsController: isKeyboardLastShown = $isKeyboardLastShown"
                    )
                    //SOFT_INPUT_ADJUST_RESIZE模式且EditText有焦点的情况下，从后台到前台时会默认弹起键盘。
                    //所以需要根据上次的键盘状态来判断是否需要添加 SOFT_INPUT_STATE_HIDDEN 来强制回到前台后不弹起键盘
                    val flag = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or
                            if (!isKeyboardLastShown) WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN else 0
                    window?.setSoftInputMode(flag)
                }
                //记录是否是从后台的ON_RESUME，除了第一次ON_RESUME，其它均视为后台
                isResumedFromBackground = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            //跳转页面时隐藏软键盘
            if (destination.route != Route.Chat.route){
                keyboardController.hideSoftKeyboard()
            }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }

    BackHandler(currentPanel.value == Panel.EMOJI) { hidePanel() }

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
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Route.Other.route) }) {
                        Icon(
                            rememberVectorPainter(Icons.Filled.MoreVert),
                            contentDescription = null
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
                    editEvenFlow,
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
                        .navigationBarsPadding(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                    ) {
                        LazyVerticalGrid(
                            state = lazyGridState,
                            modifier = Modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(top = 12.dp, bottom = 42.dp),
                            columns = GridCells.Fixed(8),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(
                                emojis,
                                key = { index, item -> item.emoji }) { index, item ->
                                Image(
                                    painterResource(item.icon),
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .align(Alignment.Center)
                                        .clickable(onClick = {
                                            textField += item.emoji
                                        }),
                                    contentDescription = item.emoji,
                                )
                            }
                        }



                        IconButton(onClick = {
                            scope.launch { editEvenFlow.emit(EditTextEvent.Delete) }
                        }, modifier = Modifier.align(Alignment.BottomEnd)) {
                            Image(
                                painterResource(R.drawable.ic_emoji_delete),
                                contentDescription = "删除"
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
private fun ChatInputArea2(
    textField: String,
    keyboardController: EditTextFieldController,
    editEventFlow: SharedFlow<EditTextEvent>,
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
            editEventFlow,
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







