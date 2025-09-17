package com.servicebio.compose.application.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicebio.compose.application.component.SymbolAnnotationType
import com.servicebio.compose.application.model.Message
import com.servicebio.compose.application.model.Panel
import com.servicebio.compose.application.model.SheetEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _newMessageEvent = MutableSharedFlow<Unit>()
    val newMessageEvent = _newMessageEvent.asSharedFlow()

    private val _toggleKeyboardEvent = MutableSharedFlow<Boolean>()
    val toggleKeyboardEvent = _toggleKeyboardEvent.asSharedFlow()

    private val _showBottomSheet = MutableStateFlow<SheetEvent>(SheetEvent.DismissSheet)
    val showBottomSheet = _showBottomSheet.asStateFlow()

    init {
        viewModelScope.launch {
            _messages.value += listOf(
                Message("0", "Send", true, "啊付费OK门口马拉松大", 1757379600000L),
                Message("1", "Receive", false, "哈哈哈哈哈哈[得意]", 1757380200000),
                Message("2", "Send", true, "[得意] @小姐姐 www.google.com +1 5634471692 czhen711@163.com", 1757380200000),
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
            val id = (newList.size).toString()
            newList.add(0, Message(id, "Send", true, text, System.currentTimeMillis()))
            if (text.lowercase() == "ok") {
                newList.add(0, Message(id, "Receive", false, text, System.currentTimeMillis()))
            } else if (text == "背景") {
                _showCustomBackground.emit(true)
            } else if (text == "取消背景") {
                _showCustomBackground.emit(false)
            }
            _messages.emit(newList)

            if (text == "自动发送") {
                autoSendMessage()
            }
        }
    }

    fun deleteMessage(message: Message) {
        viewModelScope.launch {
            val newList = _messages.value.toMutableList()
            val success = newList.removeIf { it.id == message.id }
            if (success) {
                _messages.emit(newList)
            }
        }
    }

    fun toggleKeyboard(shown: Boolean = false) {
        viewModelScope.launch { _toggleKeyboardEvent.emit(shown) }
    }

    fun showBottomSheet(type: SymbolAnnotationType, item: String) {
        viewModelScope.launch {
            _showBottomSheet.emit(SheetEvent.ShowSheet(type, item))
        }
    }

    fun dismissBottomSheet() {
        viewModelScope.launch { _showBottomSheet.emit(SheetEvent.DismissSheet) }
    }

    private val sentences = arrayListOf(
        "有些离别悄然无声，却让人痛彻心扉。",
        "破晓降至，抬头便是璀璨朝阳温暖心田。",
        "采采流水，蓬蓬远春。窈窕深谷，时见美人。碧桃满春树，风日水滨。柳阴路曲，流莺比邻。乘之愈往，识之愈真。如将不尽，与古为新。",
        "心若澄明，世界亦无尘埃，一切如此美好。",
        "力所能及之事尽力而为，无力回天之时则顺其自然。",
        "时间是最公正的雕刻家，它从不言语，却改变了所有。",
        "孤独是精神的旷野，唯有独处时，人才能听见自己内心的声音。",
        "生命的厚重，不在于避开了多少暗礁，而在于在风浪中学会了航行。",
        "成长是一场无声的蜕变，痛是必然的，但美是必然的结局。",
        "过去是镜中的影，未来是雾中的光，唯有当下是可触摸的真实。",
        "星空之所以美丽，是因为黑暗从不能吞噬光芒，只能衬托它。",
        "[微笑][撇嘴][色][发呆][得意][流泪][害羞][闭嘴][笑脸][大哭][尴尬][发怒][调皮][呲牙][惊讶][难过][酷][冷汗][奋斗]",
        "山不言语，却教会了世人何为沉稳；海不喧哗，却诠释了什么是深邃。",
        "一朵花的绽放无需谁的目光，它的美自在生命本身。",
        "人性既有光明的坦荡，也有幽暗的曲折，而智慧是学会与两者共存。",
        "真正的强大，不是征服世界，而是能温柔地对待自己的脆弱。",
        "理解不代表认同，宽容不意味妥协，这是一种深层的智慧。",
        "追求的意义不在于抵达，而在于一路上的蜕变与清醒。",
        "当我们凝视深渊时，深渊也在凝视我们。"
    )

    private fun autoSendMessage() {
        viewModelScope.launch {
            for (i in 0 until 200) {
                delay(500)
                val text = sentences.random()
                val newList = _messages.value.toMutableList()
                val nextIndex = newList.size
                val isOut = nextIndex % 2 == 0
                val id = nextIndex.toString()
                newList.add(
                    0,
                    Message(
                        id,
                        if (isOut) "Send" else "Receive",
                        isOut,
                        text,
                        System.currentTimeMillis()
                    )
                )
                _messages.emit(newList)
                _newMessageEvent.emit(Unit)
            }
        }
    }
}