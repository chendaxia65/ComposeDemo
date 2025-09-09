package com.servicebio.compose.application.model

enum class Panel {

    EMOJI,
    NONE;

    fun isOpened(): Boolean {
        return this != NONE
    }
}