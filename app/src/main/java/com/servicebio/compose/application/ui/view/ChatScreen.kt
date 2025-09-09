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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.servicebio.compose.application.R
import com.servicebio.compose.application.component.KeyboardManager
import com.servicebio.compose.application.component.monitorKeyboardHeight
import com.servicebio.compose.application.component.rememberKeyboardManager
import com.servicebio.compose.application.component.rememberPanelPadding2
import com.servicebio.compose.application.emoji.EditTextEvent
import com.servicebio.compose.application.emoji.EditTextField
import com.servicebio.compose.application.emoji.EditTextFieldController
import com.servicebio.compose.application.emoji.Emoji
import com.servicebio.compose.application.ext.noRippleClickable
import com.servicebio.compose.application.model.Conversation
import com.servicebio.compose.application.model.Message
import com.servicebio.compose.application.model.Panel
import com.servicebio.compose.application.route.Route
import com.servicebio.compose.application.utils.TimestampUtils
import com.servicebio.compose.application.viewmodel.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    navController: NavHostController,
    conversation: Conversation?,
    viewModel: ChatViewModel
) {

    val scope = rememberCoroutineScope()

    val textContent by viewModel.messageTextContent.collectAsState()

    val bottomPanel by viewModel.bottomPanel.collectAsState()

    val panelHeight by monitorKeyboardHeight()

    val keyboardManager = rememberKeyboardManager()
    val keyboardController = remember { EditTextFieldController() }
    var isResumedFromBackground by rememberSaveable { mutableStateOf(false) }

    var isKeyboardLastShown by rememberSaveable { mutableStateOf(textContent.isNotEmpty()) }

    val emojis by rememberSaveable { mutableStateOf(Emoji.instance.emojiIcons) }
    val lazyGridState = rememberLazyGridState()
    val editEventFlow = remember { MutableSharedFlow<EditTextEvent>() }

    val hidePanel = { viewModel.updatePanel(Panel.NONE) }

    LaunchedEffect(Unit) {
        keyboardManager.addOnDirectionChangedListener {
            //记录最后一次键盘的显示状态
            isKeyboardLastShown = it == KeyboardManager.DIRECTION_UP

            //如果键盘正在升起时，Panel是开启状态，就将Panel隐藏
            if (isKeyboardLastShown && bottomPanel == Panel.EMOJI) {
                hidePanel()
            }
            Log.e("TAG", "rememberKeyboardManager,InsetsController: Direction = $it")
        }

        if (textContent.isNotEmpty()) {
            keyboardController.requestFocus()
            //跳转页面再返回当前页面时，UI被重构，所以需要限制软键盘的显示
            if (isKeyboardLastShown) keyboardController.showSoftKeyboard()
        }
    }

    val window = (LocalContext.current as? Activity)?.window
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, navController) {
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

        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            //跳转页面时隐藏软键盘
            if (destination.route != Route.Chat.route) {
                keyboardController.hideSoftKeyboard()
            }
        }
        navController.addOnDestinationChangedListener(listener)

        onDispose {
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            lifecycleOwner.lifecycle.removeObserver(observer)
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    BackHandler(bottomPanel == Panel.EMOJI) { hidePanel() }

    Scaffold(
        topBar = { ChatAppBar(conversation, keyboardController, navController) },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ChatContent(
                viewModel,
                keyboardManager,
                keyboardController,
                editEventFlow.asSharedFlow()
            )

            if (bottomPanel == Panel.EMOJI) {
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
                            modifier = Modifier.fillMaxSize(),
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
                                            scope.launch { editEventFlow.emit(EditTextEvent.Shock) }
                                            viewModel.appendTextContent(item.emoji)
                                        }),
                                    contentDescription = item.emoji,
                                )
                            }
                        }

                        DeleteButton(onEvent = {
                            editEventFlow.emit(EditTextEvent.Delete)
                        })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ChatAppBar(
    conversation: Conversation?,
    keyboardController: EditTextFieldController,
    navController: NavHostController
) {
    val isImeVisible = WindowInsets.isImeVisible

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
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChatContent(
    viewModel: ChatViewModel,
    keyboardManager: KeyboardManager,
    keyboardController: EditTextFieldController,
    editEventFlow: SharedFlow<EditTextEvent>
) {
    val textContent by viewModel.messageTextContent.collectAsState()
    val bottomPanel by viewModel.bottomPanel.collectAsState()

    val messages by viewModel.messages.collectAsState()

    val scope = rememberCoroutineScope()
    val isImeVisible = WindowInsets.isImeVisible
    val listState = rememberLazyListState()

    // 当键盘弹起或Panel显示时，需要增加底部的padding，避免内容被遮挡
    val resizeBottom by rememberPanelPadding2(keyboardManager, bottomPanel.isOpened())

    LaunchedEffect(keyboardManager) {
        keyboardManager.addOnDirectionChangedListener {
            if (it == KeyboardManager.DIRECTION_UP) {
                scope.launch { listState.animateScrollToItem(0) }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(resizeBottom)
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    if (bottomPanel.isOpened()) {
                        viewModel.updatePanel(Panel.NONE)
                    } else if (isImeVisible) {
                        keyboardController.hideSoftKeyboard()
                    }
                })
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                state = listState,
                reverseLayout = true
            ) {
                itemsIndexed(messages, key = { index, item -> index }) { index, item ->
                    val preMessage = messages.getOrNull(index + 1)
                    val nextMessage = messages.getOrNull(index - 1)
                    MessageContainer(preMessage, item, nextMessage)
                }
            }
        }
        ChatInputArea2(
            textContent,
            keyboardController,
            editEventFlow,
            bottomPanel,
            onInputTextChanged = {
                viewModel.updateTextContent(it)
            },
            onSendMessage = {
                Log.e("TAG", "onSendMessage: $textContent")
                viewModel.sendTextMessage(textContent)
                scope.launch { listState.animateScrollToItem(0) }

            },
            onPanelChanged = { viewModel.updatePanel(it) }
        )
    }
}

@Composable
private fun MessageContainer(preMessage: Message?, message: Message, nextMessage: Message?) {
    val timeInterval = 300 * 1000L
    val preTime = (preMessage?.timestamp ?: 0) / timeInterval
    val time = message.timestamp / timeInterval
    val displayTime = preTime < time

    val displayAvatar = message.id != nextMessage?.id

    Column(modifier = Modifier.fillMaxWidth()) {
        if (displayTime) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = TimestampUtils.formatMessageDate(message.timestamp),
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        if (message.isOut) MessageOutCell(message, displayAvatar) else MessageInCell(
            message,
            displayAvatar
        )
    }
}

@Composable
private fun MessageInCell(message: Message, displayAvatar: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        if (displayAvatar) {
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.Top),
                painter = painterResource(R.mipmap.icon_avatar_default),
                contentDescription = null
            )
        } else {
            Spacer(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .weight(1f, fill = false)
                .heightIn(min = 40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable(onClick = {}),
            contentAlignment = Alignment.Center
        ) {
            MessageText(message)
        }
    }
}

@Composable
private fun rememberAvatarHeight(): State<Dp> {
    val textMeasurer = rememberTextMeasurer()
    val textHeight = textMeasurer.measure("中", style = LocalTextStyle.current.copy(fontSize = 16.sp)).size.height
    val textHeightDp = with(LocalDensity.current) { textHeight.toDp() }

    return rememberUpdatedState(textHeightDp + 16.dp)
}

@Composable
private fun MessageOutCell(message: Message, displayAvatar: Boolean) {
//    val avatarHeight by rememberAvatarHeight()

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Box(
            modifier = Modifier
                .weight(1f, fill = false)
                .heightIn(min = 40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable(onClick = {}),
            contentAlignment = Alignment.Center,
        ) {
            MessageText(message)
        }
        Spacer(modifier = Modifier.width(6.dp))
        if (displayAvatar) {
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.Top),
                painter = painterResource(R.mipmap.icon_avatar_default),
                contentDescription = null
            )
        } else {
            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
private fun MessageText(message: Message) {
    val emojiEngine = remember { Emoji.instance }
    val density = LocalDensity.current

    Text(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp) .onGloballyPositioned{
            val height = with(density){it.size.height.toDp()}
            Log.e("TAG", "onGloballyPositioned: viewHeight = $height", )
        },
        fontSize = 16.sp,
        text = emojiEngine.toAnnotatedString(message.message),
        inlineContent = emojiEngine.inlineContentMap()
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
private fun ChatInputArea2(
    textContent: String,
    keyboardController: EditTextFieldController,
    editEventFlow: SharedFlow<EditTextEvent>,
    bottomPanel: Panel = Panel.NONE,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    onPanelChanged: (Panel) -> Unit
) {

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
            initialText = textContent
        ) {
            onInputTextChanged(it)
        }

        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(32.dp)
                .noRippleClickable {
                    val emojiOpened = bottomPanel == Panel.EMOJI
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
                tint = if (bottomPanel == Panel.EMOJI) MaterialTheme.colorScheme.primary else LocalContentColor.current
            )
        }

        AnimatedContent(
            targetState = textContent.isNotEmpty(),
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

@Composable
private fun BoxScope.DeleteButton(onEvent: suspend () -> Unit) {
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    var isLongPressing by remember { mutableStateOf(false) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect {
            if (it is PressInteraction.Press) {
                isLongPressing = true
                scope.launch {
                    delay(300)
                    while (isLongPressing) {
                        onEvent()
                        delay(50)
                    }
                }
            } else if (it is PressInteraction.Release || it is PressInteraction.Cancel) {
                isLongPressing = false
            }
        }
    }

    IconButton(
        onClick = { scope.launch { onEvent() } },
        interactionSource = interactionSource,
        modifier = Modifier.align(Alignment.BottomEnd)
    ) {
        Image(
            painterResource(R.drawable.ic_emoji_delete),
            contentDescription = "删除"
        )
    }

}








