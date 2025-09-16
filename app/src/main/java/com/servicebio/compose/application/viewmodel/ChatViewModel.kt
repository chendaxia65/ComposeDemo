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

    private val _showCustomBackground = MutableStateFlow(false)
    val showCustomBackground = _showCustomBackground.asStateFlow()

    init {
        viewModelScope.launch {
            _messages.value += listOf(
                Message("0","Send", true, "啊付费OK门口马拉松大", 1757379600000L),
                Message("1","Receive", false, "哈哈哈哈哈哈[得意]", 1757380200000),
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
            val id = (newList.size + 1).toString()
            newList.add(0, Message(id,"Send", true, text, System.currentTimeMillis()))
            if (text.lowercase() == "ok") {
                newList.add(0, Message(id,"Receive", false, text, System.currentTimeMillis()))
            } else if (text == "背景") {
                _showCustomBackground.emit(true)
            } else if (text == "取消背景") {
                _showCustomBackground.emit(false)
            }
            _messages.emit(newList)
        }
    }

    fun deleteMessage(message: Message){
        viewModelScope.launch {
            val newList = _messages.value.toMutableList()
           val success = newList.removeIf { it.id == message.id }
            if(success){
                _messages.emit(newList)
            }
        }
    }
}