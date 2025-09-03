package com.servicebio.compose.application.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicebio.compose.application.R
import com.servicebio.compose.application.model.Conversation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConversationViewModel : ViewModel() {

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations = _conversations.asStateFlow()


    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            val avatarResId = R.mipmap.icon_avatar_default
            _conversations.value = listOf(
                Conversation("1", avatarResId, "陈陈", "Hello,What is your name?", "18:00", 1),
                Conversation("2", avatarResId, "张三", "OKK!", "18:00", 0),
                Conversation("3", avatarResId, "里斯", "ByeBye!", "18:00", 1),
                Conversation("4", avatarResId, "Bio", "OKK!", "18:00", 0),
                Conversation("5", avatarResId, "宝儿", "ByeBye!", "18:00", 1),
                Conversation("6", avatarResId, "Int", "OKK!", "18:00", 0),
                Conversation("7", avatarResId, "Scaffold", "ByeBye!", "18:00", 1),
                Conversation("8", avatarResId, "张三", "OKK!", "18:00", 0),
                Conversation("9", avatarResId, "里斯", "ByeBye!", "18:00", 1),
                Conversation("10", avatarResId, "张三", "OKK!", "18:00", 0),
                Conversation("11", avatarResId, "里斯", "ByeBye!", "18:00", 1),
                Conversation("12", avatarResId, "张三", "OKK!", "18:00", 0),
                Conversation("13", avatarResId, "里斯", "ByeBye!", "18:00", 1),
                Conversation("14", avatarResId, "张三", "OKK!", "18:00", 0),
                Conversation("15", avatarResId, "里斯", "ByeBye!", "18:00", 1),
                Conversation("16", avatarResId, "张三", "OKK!", "18:00", 0),
                Conversation("17", avatarResId, "里斯", "ByeBye!", "18:00", 1),
                Conversation("18", avatarResId, "张三", "OKK!", "18:00", 0)
            )
        }
    }

    fun getData(): List<Conversation> {
        val avatarResId = R.mipmap.icon_avatar_default

        return listOf(
            Conversation("1", avatarResId, "陈陈", "Hello,What is your name?", "18:00", 1),
            Conversation("2", avatarResId, "张三", "OKK!", "18:00", 0),
            Conversation("3", avatarResId, "里斯", "ByeBye!", "18:00", 1),
            Conversation("4", avatarResId, "Bio", "OKK!", "18:00", 0),
            Conversation("5", avatarResId, "宝儿", "ByeBye!", "18:00", 1),
            Conversation("6", avatarResId, "Int", "OKK!", "18:00", 0),
            Conversation("7", avatarResId, "Scaffold", "ByeBye!", "18:00", 1),
            Conversation("8", avatarResId, "张三", "OKK!", "18:00", 0),
            Conversation("9", avatarResId, "里斯", "ByeBye!", "18:00", 1),
            Conversation("10", avatarResId, "张三", "OKK!", "18:00", 0),
            Conversation("11", avatarResId, "里斯", "ByeBye!", "18:00", 1),
            Conversation("12", avatarResId, "张三", "OKK!", "18:00", 0),
            Conversation("13", avatarResId, "里斯", "ByeBye!", "18:00", 1),
            Conversation("14", avatarResId, "张三", "OKK!", "18:00", 0),
            Conversation("15", avatarResId, "里斯", "ByeBye!", "18:00", 1),
            Conversation("16", avatarResId, "张三", "OKK!", "18:00", 0),
            Conversation("17", avatarResId, "里斯", "ByeBye!", "18:00", 1),
            Conversation("18", avatarResId, "张三", "OKK!", "18:00", 0)
        )
    }
}