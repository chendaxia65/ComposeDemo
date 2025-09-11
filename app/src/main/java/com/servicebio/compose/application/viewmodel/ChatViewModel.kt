package com.servicebio.compose.application.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicebio.compose.application.model.Message
import com.servicebio.compose.application.model.Panel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _messageTextContent = MutableStateFlow("中国[发财啦]国庆")
    val messageTextContent = _messageTextContent.asStateFlow()

    private val _bottomPanel = MutableStateFlow(Panel.NONE)
    val bottomPanel = _bottomPanel.asStateFlow()

    init {
        viewModelScope.launch {
            _messages.value += listOf(
                Message("Send", true, "啊付费OK门口马拉松大", 1757379600000L),
                Message("Receive", false, "哈哈哈哈哈哈[得意]", 1757380200000),
            ).reversed()
        }
    }


    fun updateTextContent(text: String) {
        viewModelScope.launch { _messageTextContent.emit(text) }
    }

    fun appendTextContent(text: String) {
        viewModelScope.launch { _messageTextContent.value += text }
    }

    fun updatePanel(panel: Panel) {
        viewModelScope.launch { _bottomPanel.emit(panel) }
    }

    fun sendTextMessage(text: String) {
        if (text.isEmpty()) return
        viewModelScope.launch {
            updateTextContent("")
            val newList = _messages.value.toMutableList()
            newList.add(0, Message("Send", true, text, System.currentTimeMillis()))
            if (text.lowercase() == "ok") {
                newList.add(0, Message("Receive", false, text, System.currentTimeMillis()))
            }
            _messages.emit(newList)
        }
    }
}