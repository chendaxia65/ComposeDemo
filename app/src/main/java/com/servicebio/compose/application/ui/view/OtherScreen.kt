package com.servicebio.compose.application.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.servicebio.compose.application.ext.popBackStackWithResult
import com.servicebio.compose.application.ui.theme.ComposeDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherScreen(navController: NavHostController) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text(text = "Other") },
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
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            GoBack(Modifier.align(Alignment.Center),navController)

        }
    }
}

@Composable
private fun GoBack(modifier: Modifier = Modifier, navController: NavHostController) {
    TextButton(
        onClick = {
            navController.popBackStackWithResult("result","你猜我猜不猜...")
        },
        modifier = modifier
            .height(40.dp)
            .clip(CircleShape),
        colors = ButtonDefaults.buttonColors()
    ) {
        Text(text = "GoBack")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGoBack() {
    ComposeDemoTheme {
        GoBack(navController = rememberNavController())
    }
}