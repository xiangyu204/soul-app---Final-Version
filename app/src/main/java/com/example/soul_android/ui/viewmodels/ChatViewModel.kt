package com.example.soul_android.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soul_android.data.network.ChatSendRequest
import com.example.soul_android.data.network.SoulApiService
import com.example.soul_android.models.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ChatUiState {

    object Loading : ChatUiState()

    data class Success(
        val messages: List<Message>,
        val partnerAvatar: String? = null
    ) : ChatUiState()

    data class Error(
        val message: String
    ) : ChatUiState()
}

class ChatViewModel(
    private val apiService: SoulApiService = SoulApiService.create()
) : ViewModel() {

    companion object {
        private const val TAG = "CHAT_DEBUG"
    }

    private val _uiState =
        MutableStateFlow<ChatUiState>(ChatUiState.Loading)

    val uiState = _uiState.asStateFlow()

    private val _messages =
        MutableStateFlow<List<Message>>(emptyList())

    /**
     * 获取两个人之间的聊天记录
     */
    fun fetchMessages(
        otherUserId: String,
        currentUsername: String
    ) {

        if (otherUserId.isBlank()) {
            Log.e(TAG, "fetchMessages: otherUserId 为空")
            _uiState.value =
                ChatUiState.Error("聊天对象不能为空")
            return
        }

        if (currentUsername.isBlank()) {
            Log.e(TAG, "fetchMessages: currentUsername 为空")
            _uiState.value =
                ChatUiState.Error("当前登录用户为空")
            return
        }

        viewModelScope.launch {

            _uiState.value = ChatUiState.Loading

            Log.d(
                TAG,
                "fetchMessages: me=$currentUsername partner=$otherUserId"
            )

            /*
             * 1. 创建 / 获取直聊聊天室
             *
             * 注意：
             * 这个接口返回 Response，所以 400 / 500 不一定抛 Exception。
             * 必须手动检查 isSuccessful。
             */
            try {

                val roomResponse =
                    apiService.createDirectChatRoom(
                        ChatSendRequest(
                            senderUsername = currentUsername,
                            receiverUsername = otherUserId,
                            content = ""
                        )
                    )

                if (roomResponse.isSuccessful) {

                    Log.d(
                        TAG,
                        "createDirectChatRoom success " +
                                "code=${roomResponse.code()} " +
                                "body=${roomResponse.body()}"
                    )

                } else {

                    val errorBody =
                        roomResponse.errorBody()?.string()

                    Log.e(
                        TAG,
                        "createDirectChatRoom failed " +
                                "code=${roomResponse.code()} " +
                                "error=$errorBody"
                    )
                }

            } catch (e: Exception) {

                /*
                 * 创建聊天室失败不直接终止。
                 *
                 * 因为聊天室可能本来就存在，
                 * 仍然尝试获取聊天记录。
                 */
                Log.e(
                    TAG,
                    "createDirectChatRoom exception",
                    e
                )
            }

            /*
             * 2. 获取聊天记录及对方头像
             */
            var partnerAvatar: String? = null
            try {
                val profileResp = apiService.getUserProfile(otherUserId)
                if (profileResp.isSuccessful) {
                    partnerAvatar = profileResp.body()?.avatar
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch partner profile for avatar", e)
            }

            try {

                val responses =
                    apiService.getChatMessages(
                        me = currentUsername,
                        partner = otherUserId
                    )

                Log.d(
                    TAG,
                    "getChatMessages success size=${responses.size}"
                )

                val mappedMessages =
                    responses.map { resp ->

                        Message(
                            id = resp.id?.toString()
                                ?: java.util.UUID
                                    .randomUUID()
                                    .toString(),

                            senderId = resp.senderUsername,

                            text = resp.content,

                            /*
                             * 你的 Message.timestamp 目前是 Long，
                             * 暂时使用当前时间。
                             *
                             * 后面如果需要按照服务器 createdAt 显示时间，
                             * 再单独转换。
                             */
                            timestamp =
                                System.currentTimeMillis(),

                            isMe =
                                resp.senderUsername ==
                                        currentUsername
                        )
                    }

                _messages.value = mappedMessages

                _uiState.value =
                    ChatUiState.Success(
                        mappedMessages,
                        partnerAvatar
                    )

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "getChatMessages failed",
                    e
                )

                _uiState.value =
                    ChatUiState.Error(
                        "获取聊天记录失败：${e.message ?: "未知错误"}"
                    )
            }
        }
    }

    /**
     * 发送消息
     */
    fun sendMessage(
        receiverId: String,
        content: String,
        currentUsername: String
    ) {

        /*
         * 空消息不发送
         */
        if (content.isBlank()) {
            return
        }

        if (receiverId.isBlank()) {

            Log.e(
                TAG,
                "sendMessage: receiverId 为空"
            )

            return
        }

        if (currentUsername.isBlank()) {

            Log.e(
                TAG,
                "sendMessage: currentUsername 为空"
            )

            return
        }

        viewModelScope.launch {

            Log.d(
                TAG,
                "准备发送消息"
            )

            Log.d(
                TAG,
                "sender=$currentUsername"
            )

            Log.d(
                TAG,
                "receiver=$receiverId"
            )

            Log.d(
                TAG,
                "content=$content"
            )

            try {

                /*
                 * 发送消息时不要再 createDirectChatRoom。
                 *
                 * 因为进入 ChatScreen 时 fetchMessages()
                 * 已经创建/获取聊天室。
                 */
                val response =
                    apiService.sendMessage(
                        ChatSendRequest(
                            senderUsername =
                                currentUsername,

                            receiverUsername =
                                receiverId,

                            content =
                                content.trim()
                        )
                    )

                Log.d(
                    TAG,
                    "sendMessage HTTP CODE=${response.code()}"
                )

                /*
                 * Retrofit Response<T>：
                 *
                 * 400 / 404 / 500 并不一定进入 catch。
                 * 所以这里必须判断 isSuccessful。
                 */
                if (response.isSuccessful) {

                    Log.d(
                        TAG,
                        "sendMessage SUCCESS"
                    )

                    Log.d(
                        TAG,
                        "response body=${response.body()}"
                    )

                    /*
                     * 确认服务器返回成功以后，
                     * 才把消息添加到 App 页面。
                     *
                     * 这样不会再发生：
                     *
                     * App 看起来发送成功，
                     * 实际服务器根本没收到。
                     */
                    val newMessage =
                        Message(
                            id = java.util.UUID
                                .randomUUID()
                                .toString(),

                            senderId =
                                currentUsername,

                            text =
                                content.trim(),

                            timestamp =
                                System.currentTimeMillis(),

                            isMe =
                                true
                        )

                    val updatedMessages =
                        _messages.value +
                                newMessage

                    _messages.value =
                        updatedMessages

                    val currentAvatar =
                        (_uiState.value as? ChatUiState.Success)?.partnerAvatar

                    _uiState.value =
                        ChatUiState.Success(
                            updatedMessages,
                            currentAvatar
                        )

                } else {

                    val errorBody =
                        response
                            .errorBody()
                            ?.string()

                    Log.e(
                        TAG,
                        "sendMessage FAILED " +
                                "code=${response.code()} " +
                                "error=$errorBody"
                    )

                    /*
                     * 不添加假消息。
                     *
                     * 保留当前聊天记录，
                     * 同时 Logcat 可以看到真正错误。
                     */
                }

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "sendMessage EXCEPTION",
                    e
                )
            }
        }
    }
}