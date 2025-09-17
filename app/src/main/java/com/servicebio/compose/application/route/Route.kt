package com.servicebio.compose.application.route

import com.servicebio.compose.application.ext.encodeUri
import com.servicebio.compose.application.model.Conversation
import kotlinx.serialization.json.Json
import java.net.URLEncoder

sealed class Route(val route: String) {

    object Main : Route("/main")
    object Chat : Route("/chat/{conversation}") {
        const val PARAMETER_NAME_CONVERSATION = "conversation"
        fun buildRoute(conversation: Conversation): String {
            val json = Json.encodeToString(conversation).encodeUri()
            return "/chat/$json"
        }
    }

    object Other : Route("/other")

    object UserInfo : Route("/userInfo/{name}"){
        const val PARAMETER_NAME = "name"

        fun buildRoute(name: String): String {
            return "/userInfo/$name"
        }
    }

    object WebContainer : Route("/web/{url}"){
        const val PARAMETER_NAME = "url"

        fun buildRoute(url: String): String {
            return "/web/${URLEncoder.encode(url,"UTF-8")}"
        }
    }


}