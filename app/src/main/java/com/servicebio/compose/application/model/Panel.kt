package com.servicebio.compose.application.model

enum class Panel {

    EMOJI,
    MORE,
    NONE;

    fun isOpened(): Boolean {
        return this != NONE
    }
}