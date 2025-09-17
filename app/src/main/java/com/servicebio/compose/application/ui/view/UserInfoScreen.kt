package com.servicebio.compose.application.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.servicebio.compose.application.R
import com.servicebio.compose.application.model.Conversation
import com.servicebio.compose.application.route.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(navController: NavHostController, title: String) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text(text = title) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = rememberVectorPainter(Icons.AutoMirrored.Outlined.ArrowBack),
                        contentDescription = "",
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        )
    }, floatingActionButton = {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = {
                    val item = Conversation("19", R.mipmap.icon_avatar_default, title, "OKK!", "18:00", 0)
                    navController.navigate(Route.Chat.buildRoute(item)) {
                        launchSingleTop = true//如果要启动的在栈顶就复用(singleTop)
                        restoreState = true//保存状态，使用rememberSaveable保证跳转页面后数据恢复
                        //出栈操作，一直出栈到指定路由(类似于singleTask)
                        popUpTo(Route.Chat.route) {
                            //包含当前页面也出栈
                            inclusive = true
                        }
                    }
                })
        ) {

            Icon(
                modifier = Modifier.align(Alignment.Center),
                painter = rememberVectorPainter(Icons.AutoMirrored.Default.Send),
                contentDescription = null,
                tint = Color.White
            )
        }
    }) { innerPadding ->

        Box(modifier = Modifier.padding(innerPadding)) {

        }
    }
}