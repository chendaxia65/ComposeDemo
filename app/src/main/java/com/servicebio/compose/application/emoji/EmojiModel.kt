package com.servicebio.compose.application.emoji

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class EmojiModel(val icon: Int, val emoji: String) : Parcelable
