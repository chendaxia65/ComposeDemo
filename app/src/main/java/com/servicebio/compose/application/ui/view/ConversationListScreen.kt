package com.servicebio.compose.application.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.servicebio.compose.application.R
import com.servicebio.compose.application.component.PagerScaffold
import com.servicebio.compose.application.model.Conversation
import com.servicebio.compose.application.route.Route
import com.servicebio.compose.application.ui.theme.ComposeDemoTheme
import com.servicebio.compose.application.viewmodel.ConversationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    navController: NavHostController,
    viewModel: ConversationViewModel = viewModel<ConversationViewModel>()
) {

    val conversationList by viewModel.conversations.collectAsState()

    val appBarHeight = rememberSaveable { mutableFloatStateOf(0f) }
    val appBarOffsetPx = rememberSaveable { mutableFloatStateOf(0f) }


    val nestedScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delay = available.y
                val newOffset = appBarOffsetPx.floatValue + delay
                appBarOffsetPx.floatValue = newOffset.coerceIn(-appBarHeight.floatValue, 0f)
                return super.onPreScroll(available, source)
            }
        }
    }

    PagerScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "消息")
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier
//                    .offset {
//                        IntOffset(x = 0, y = appBarOffsetPx.floatValue.roundToInt())
//                    }
                    .graphicsLayer {
                        translationY = appBarOffsetPx.floatValue
                    }
                    .onGloballyPositioned {
                        appBarHeight.floatValue = it.size.height.toFloat()
                    })
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        val paddingTopOffset =
            with(LocalDensity.current) { (appBarHeight.floatValue + appBarOffsetPx.floatValue).toDp() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        top = paddingTopOffset,
                        bottom = innerPadding.calculateBottomPadding()
                    )
                )
        ) {
            if(conversationList.isNotEmpty()){
                LazyColumn(
                    modifier = Modifier
                        .background(Color.White)
                        .nestedScroll(nestedScroll)
                ) {
                    itemsIndexed(conversationList, key = { index, item -> item.id }) { index, item ->
                        ConversationItem(item) {
                            navController.navigate(Route.Chat.buildRoute(item)){
                                launchSingleTop = true//如果要启动的在栈顶就复用
                                restoreState = true//保存状态，使用rememberSaveable保证跳转页面后数据恢复
                            }

                        }
                        if (index != conversationList.lastIndex)
                            HorizontalDivider(
                                thickness = Dp.Hairline,
                                modifier = Modifier.padding(start = 86.dp, end = 8.dp)
                            )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationItem(item: Conversation, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .clickable { onClick() }
            .padding(16.dp, 12.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(item.avatarResId),
            contentDescription = null,
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Spacer(Modifier.width(12.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            Row {
                Text(
                    text = item.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = item.time,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.lastMessage,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .alignByBaseline(),
                    style = MaterialTheme.typography.bodyLarge,
                )

                if (item.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    ) {
                        Text(
                            text = if (item.unreadCount > 99) "99+" else item.unreadCount.toString(),
                            maxLines = 1,
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun ConversationItemPreview() {
    ComposeDemoTheme {
        ConversationItem(
            Conversation(
                "1",
                R.mipmap.icon_avatar_default,
                "陈陈",
                "Hello",
                "18:00",
                0
            )
        ) {}
    }
}