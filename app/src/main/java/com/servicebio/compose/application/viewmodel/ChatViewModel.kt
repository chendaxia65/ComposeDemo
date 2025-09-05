package com.servicebio.compose.application.viewmodel

import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel

class ChatViewModel: ViewModel() {


    val iconPainterMap = mutableMapOf<Int, Painter>()
}