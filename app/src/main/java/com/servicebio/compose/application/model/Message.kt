package com.servicebio.compose.application.model

data class Message(val id: String, val userId: String, val isOut: Boolean, val message: String, val timestamp: Long)
