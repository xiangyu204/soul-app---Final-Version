package com.example.soul

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CustomerServiceUiState {
    object Idle : CustomerServiceUiState
    object Loading : CustomerServiceUiState
    data class Success(val reply: String) : CustomerServiceUiState
    data class Error(val message: String) : CustomerServiceUiState
}

class CustomerServiceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<CustomerServiceUiState>(CustomerServiceUiState.Idle)
    val uiState: StateFlow<CustomerServiceUiState> = _uiState.asStateFlow()

    private val chatHistory = mutableListOf<com.google.firebase.vertexai.type.Content>()

    private val generativeModel: GenerativeModel by lazy {
        Firebase.vertexAI.generativeModel(
            modelName = "gemini-1.5-flash",
            systemInstruction = content {
                text("당신은 SOUL 소셜 및 스킬 교환 앱의 친절하고 전문적인 AI 고객센터 상담원입니다. 사용자가 계정 로그인, 프로필 설정, 스킬 교환(Teach/Learn), 다른 사용자들과의 소통 방법에 대해 묻는 질문에 답변하는 역할을 합니다. 답변은 간결하고 친절하게 한국어로 작성해 주세요.")
            }
        )
    }

    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return
        _uiState.value = CustomerServiceUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val chat = generativeModel.startChat(history = chatHistory)
                val response = chat.sendMessage(userMessage)

                response.text?.let { replyText ->
                    chatHistory.add(content(role = "user") { text(userMessage) })
                    chatHistory.add(content(role = "model") { text(replyText) })

                    _uiState.value = CustomerServiceUiState.Success(replyText)
                } ?: run {
                    _uiState.value = CustomerServiceUiState.Error("유효한 답변을 받지 못했습니다.")
                }
            } catch (e: Exception) {
                _uiState.value = CustomerServiceUiState.Error(e.localizedMessage ?: "요청 실패")
            }
        }
    }
}
