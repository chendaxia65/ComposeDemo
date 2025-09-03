package com.servicebio.compose.application.model

import android.os.Parcelable
import kotlinx.serialization.Serializable

@kotlinx.parcelize.Parcelize
@Serializable
data class Conversation(
    val id: String,
    val avatarResId: Int,
    val name: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int = 0
) : Parcelable