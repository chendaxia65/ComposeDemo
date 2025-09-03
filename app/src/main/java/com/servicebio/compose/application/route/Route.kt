package com.servicebio.compose.application.route

import com.servicebio.compose.application.ext.encodeUri
import com.servicebio.compose.application.model.Conversation
import kotlinx.serialization.json.Json

sealed class Route(val route: String) {

    object Main : Route("/main")
    object Chat : Route("/chat/{conversation}") {
        const val PARAMETER_NAME_CONVERSATION = "conversation"
        fun buildRoute(conversation: Conversation): String {
            val json = Json.encodeToString(conversation).encodeUri()
            return "/chat/$json"
        }
    }
}