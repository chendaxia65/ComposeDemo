package com.servicebio.compose.application.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.servicebio.compose.application.component.PagerScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {

    PagerScaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = "主页")
            })
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Text("Home", modifier = Modifier.align(Alignment.TopCenter).clickable{})


            SelectionContainer(modifier = Modifier.align(Alignment.Center).padding(horizontal = 20.dp)) {
                Text(text = "该项目专为国内开发环境设计，旨在为社区提供实用的 Compose 代码参考，帮助开发者快速掌握现代 Android 开发技术，同时也能对我所学的知识进行巩固和分享。")
            }
        }
    }
}