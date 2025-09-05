package com.servicebio.compose.application.emoji

sealed class EditTextEvent {

    object Delete : EditTextEvent()
}