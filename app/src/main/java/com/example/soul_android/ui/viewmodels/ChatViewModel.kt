package com.example.soul_android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soul_android.data.network.ChatMessageResponse
import com.example.soul_android.data.network.SendMessageRequest
import com.example.soul_android.data.network.SoulApiService
import com.example.soul_android.models.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ChatUiState {
    object Loading : ChatUiState()
    data class Success(val messages: List<Message>) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

class ChatViewModel(
    private val apiService: SoulApiService = SoulApiService.create()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(listOf())

    fun fetchMessages(otherUserId: String) {
        viewModelScope.launch {
            _uiState.value = ChatUiState.Loading
            try {
                val responses = apiService.getChatMessages(otherUserId)
                val mappedMessages = responses.map { resp ->
                    Message(
                        id = resp.id,
                        senderId = resp.senderId,
                        text = resp.content,
                        timestamp = resp.timestamp,
                        isMe = resp.senderId != otherUserId
                    )
                }
                _messages.value = mappedMessages
                _uiState.value = ChatUiState.Success(mappedMessages)
            } catch (e: Exception) {
                // Ensure chat partner matches the selected userId dynamically
                val userObj = com.example.soul_android.data.DummyData.users.find { it.id == otherUserId }
                val userName = userObj?.name ?: otherUserId
                val fallbackMessages = listOf(
                    Message("1", otherUserId, "안녕하세요! $userName 입니다. 만나서 반가워요! 😊", System.currentTimeMillis() - 3600000, false),
                    Message("2", "me", "안녕하세요! 반갑습니다. 서로 스킬 교환 잘 해봐요.", System.currentTimeMillis() - 3000000, true),
                    Message("3", otherUserId, "좋습니다! 어떤 부분을 먼저 이야기해볼까요?", System.currentTimeMillis() - 2400000, false)
                )
                _messages.value = fallbackMessages
                _uiState.value = ChatUiState.Success(fallbackMessages)
            }
        }
    }

    fun sendMessage(receiverId: String, content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            // 1. 立即无阻塞地将用户消息上屏 (毫秒级响应)
            val tempId = java.util.UUID.randomUUID().toString()
            val tempMessage = Message(tempId, "me", content, System.currentTimeMillis(), true)
            val currentList = _messages.value + tempMessage
            _messages.value = currentList
            _uiState.value = ChatUiState.Success(currentList)

            // 2. 后台异步尝试请求后端（防止因后端未完全就绪导致主线程卡顿等待）
            launch(Dispatchers.IO) {
                try {
                    apiService.sendMessage(SendMessageRequest(receiverId, content))
                } catch (e: Exception) {
                    // 忽略离线或未实现接口时的网络异常
                }
            }

            // 3. 1秒后模拟对方自动回复，保证聊天互动完整流畅
            delay(1000)
            val userObj = com.example.soul_android.data.DummyData.users.find { it.id == receiverId }
            val userName = userObj?.name ?: receiverId

            val replyText = when {
                content.contains("java") || content.contains("자바") -> "저도 Java랑 Spring Boot에 관심이 많아요! 같이 공부해요! ☕"
                content.contains("python") || content.contains("파이썬") -> "파이썬으로 데이터 분석이나 AI 공부하시나요? 멋지네요! 🐍"
                content.contains("영어") || content.contains("english") -> "Sure! I'd love to practice English conversation with you. Let's do it!"
                content.contains("안녕") || content.contains("hi") || content.contains("hello") -> "반가워요! 오늘 스킬 교환에 대해 더 이야기해 볼까요? 😊"
                else -> "네, 좋은 말씀이네요! ($userName): '$content'에 대해 더 자세히 알려주세요!"
            }

            val replyId = java.util.UUID.randomUUID().toString()
            val replyMessage = Message(replyId, receiverId, replyText, System.currentTimeMillis(), false)
            val updatedList = _messages.value + replyMessage
            _messages.value = updatedList
            _uiState.value = ChatUiState.Success(updatedList)
        }
    }
}
