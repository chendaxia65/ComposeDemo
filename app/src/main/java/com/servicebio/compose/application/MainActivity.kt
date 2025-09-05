package com.servicebio.compose.application

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.servicebio.compose.application.ext.decodeUri
import com.servicebio.compose.application.model.Conversation
import com.servicebio.compose.application.route.Route
import com.servicebio.compose.application.ui.theme.ComposeDemoTheme
import com.servicebio.compose.application.ui.view.ChatScreen
import com.servicebio.compose.application.ui.view.MainScreen
import com.servicebio.compose.application.ui.view.OtherScreen
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //SystemBarStyle.light(Color.TRANSPARENT,Color.TRANSPARENT)  确保底部导航栏完全透明
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )

        setContent {
            ComposeDemoTheme {
                val navController = rememberNavController()
                NavHost(
                    navController, Route.Main.route,
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(300)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(300)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(300)
                        )
                    }) {
                    composable(Route.Main.route) {
                        MainScreen(navController, viewModel)
                    }
                    composable(Route.Chat.route) {
                        val conversationJson =
                            it.arguments?.getString(Route.Chat.PARAMETER_NAME_CONVERSATION)

                        val conversation =
                            conversationJson?.let { json -> Json.decodeFromString<Conversation>(json.decodeUri()) }
                        ChatScreen(navController, conversation)
                    }

                    composable(Route.Other.route) {
                        OtherScreen(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeDemoTheme {
        Greeting("Android")
    }
}