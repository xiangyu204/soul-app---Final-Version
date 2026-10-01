package com.example.soul_android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soul_android.data.DummyData
import com.example.soul_android.data.network.ChatSendRequest
import com.example.soul_android.data.network.SoulApiService
import com.example.soul_android.models.Message
import com.example.soul_android.models.User
import kotlinx.coroutines.Dispatchers
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

    fun fetchMessages(otherUserId: String, currentUsername: String = "xiangyu") {
        viewModelScope.launch {
            _uiState.value = ChatUiState.Loading
            
            // Track active chat user in DummyData so it shows in ChatListScreen
            val activeUser = DummyData.users.find { it.id == otherUserId } 
                ?: DummyData.activeChatUsers.find { it.id == otherUserId } 
                ?: User(id = otherUserId, name = otherUserId)
            if (DummyData.activeChatUsers.none { it.id == otherUserId }) {
                DummyData.activeChatUsers.add(0, activeUser)
            }

            try {
                // 1. 自动在后端创建/获取直聊聊天室
                try {
                    apiService.createDirectChatRoom(
                        ChatSendRequest(
                            senderUsername = currentUsername,
                            receiverUsername = otherUserId,
                            content = ""
                        )
                    )
                } catch (e: Exception) {
                    // 忽略创建失败
                }

                // 2. 获取聊天记录
                val responses = apiService.getChatMessages(me = currentUsername, partner = otherUserId)
                val mappedMessages = responses.map { resp ->
                    Message(
                        id = resp.id?.toString() ?: java.util.UUID.randomUUID().toString(),
                        senderId = resp.senderUsername,
                        text = resp.content,
                        timestamp = System.currentTimeMillis(),
                        isMe = resp.senderUsername == currentUsername
                    )
                }
                _messages.value = mappedMessages
                _uiState.value = ChatUiState.Success(mappedMessages)
            } catch (e: Exception) {
                // Fallback dummy messages if backend is offline or room doesn't exist yet
                val userName = activeUser.name
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

    fun sendMessage(receiverId: String, content: String, currentUsername: String = "xiangyu") {
        if (content.isBlank()) return

        viewModelScope.launch {
            // 1. 立即乐观更新 UI
            val tempId = java.util.UUID.randomUUID().toString()
            val tempMessage = Message(tempId, "me", content, System.currentTimeMillis(), true)
            val currentList = _messages.value + tempMessage
            _messages.value = currentList
            _uiState.value = ChatUiState.Success(currentList)

            // 2. 异步请求后端
            launch(Dispatchers.IO) {
                try {
                    apiService.createDirectChatRoom(
                        ChatSendRequest(
                            senderUsername = currentUsername,
                            receiverUsername = receiverId,
                            content = ""
                        )
                    )
                    apiService.sendMessage(
                        ChatSendRequest(
                            senderUsername = currentUsername,
                            receiverUsername = receiverId,
                            content = content
                        )
                    )
                } catch (e: Exception) {
                    // 忽略离线异常
                }
            }
        }
    }
}
