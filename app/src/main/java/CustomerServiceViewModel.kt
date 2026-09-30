package com.example.soul_android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean
)

class CustomerServiceViewModel : ViewModel() {

    private val _messages = MutableStateFlow(
        listOf(
            ChatMessage(
                text = "안녕하세요! SOUL AI 고객센터입니다. 무엇을 도와드릴까요? 😊",
                isFromUser = false
            )
        )
    )

    val messages: StateFlow<List<ChatMessage>> =
        _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    // ==============================
    // DeepSeek API
    // ==============================

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val apiUrl =
        "https://api.deepseek.com/chat/completions"

    // 여기에 본인의 DeepSeek API Key 입력
    private val apiKey =
        "sk-097fcb205e444556a6a62f22afee4fb3"


    // ==============================
    // SOUL AI System Prompt
    // ==============================

    private val systemPrompt = """
        당신은 SOUL 스킬 교환 플랫폼의 친절하고 전문적인 AI 고객센터 상담원입니다.

        SOUL은 사용자가 서로의 스킬을 무료로 교환하면서
        배우고 가르칠 수 있는 스킬 교환 플랫폼입니다.

        주요 기능:
        1. 회원가입
        2. 로그인
        3. 프로필 관리
        4. 스킬 등록
        5. Teach / Learn 설정
        6. 스킬 매칭
        7. AI 추천
        8. 매칭 요청
        9. 채팅
        10. 스킬 교환

        답변 규칙:
        - 모든 답변은 자연스럽고 친절한 한국어로 작성하세요.
        - 사용자가 중국어로 질문해도 한국어로 답변하세요.
        - 핵심 내용을 이해하기 쉽게 설명하세요.
        - 앱 사용 방법을 질문하면 단계별로 설명하세요.
        - 존재하지 않는 SOUL 기능을 임의로 만들어내지 마세요.
        - 고객센터 상담원처럼 친절하게 답변하세요.
        - 필요한 경우 이모지를 적절하게 사용하세요.
    """.trimIndent()

    // ==============================
    // Send Message
    // ==============================

    fun sendMessage(userText: String) {

        if (userText.isBlank()) return

        if (_isLoading.value) return

        val cleanText = userText.trim()

        // 화면에 사용자 메시지 표시
        _messages.value = _messages.value + ChatMessage(
            text = cleanText,
            isFromUser = true
        )

        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {

            try {

                val reply = callDeepSeek()

                _messages.value = _messages.value + ChatMessage(
                    text = reply,
                    isFromUser = false
                )

            } catch (e: Exception) {

                e.printStackTrace()

                _messages.value = _messages.value + ChatMessage(
                    text = """
                        ⚠️ AI 고객센터 연결에 실패했습니다.

                        ${e.message ?: "알 수 없는 오류"}

                        잠시 후 다시 시도해 주세요.
                    """.trimIndent(),
                    isFromUser = false
                )

            } finally {

                _isLoading.value = false
            }
        }
    }

    // ==============================
    // DeepSeek Request
    // ==============================

    private fun callDeepSeek(): String {

        if (
            apiKey.isBlank() ||
            apiKey == "YOUR_DEEPSEEK_API_KEY"
        ) {
            throw Exception(
                "DeepSeek API Key가 설정되지 않았습니다."
            )
        }

        val messages = JSONArray()

        // System
        messages.put(
            JSONObject().apply {
                put("role", "system")
                put("content", systemPrompt)
            }
        )

        // 기존 대화
        _messages.value
            .takeLast(10)
            .forEach { message ->

                messages.put(
                    JSONObject().apply {

                        put(
                            "role",
                            if (message.isFromUser) {
                                "user"
                            } else {
                                "assistant"
                            }
                        )

                        put("content", message.text)
                    }
                )
            }

        // 요청 JSON
        val jsonBody = JSONObject().apply {

            put(
                "model",
                "deepseek-flash"
            )

            put(
                "messages",
                messages
            )

            put(
                "stream",
                false
            )

            put(
                "max_tokens",
                1024
            )
        }

        val requestBody =
            jsonBody
                .toString()
                .toRequestBody(
                    "application/json; charset=utf-8".toMediaType()
                )

        val request =
            Request.Builder()
                .url(apiUrl)
                .addHeader(
                    "Authorization",
                    "Bearer $apiKey"
                )
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .post(requestBody)
                .build()

        client.newCall(request).execute().use { response ->

            val responseBody =
                response.body?.string()
                    ?: throw Exception(
                        "DeepSeek 서버 응답이 없습니다."
                    )

            // 중요：HTTP 에러의 실제 내용을 보여줌
            if (!response.isSuccessful) {

                throw Exception(
                    "HTTP ${response.code}\n$responseBody"
                )
            }

            val jsonResponse =
                JSONObject(responseBody)

            val choices =
                jsonResponse.optJSONArray("choices")
                    ?: throw Exception(
                        "choices가 없습니다.\n$responseBody"
                    )

            if (choices.length() == 0) {
                throw Exception(
                    "DeepSeek가 답변을 반환하지 않았습니다."
                )
            }

            val choice =
                choices.getJSONObject(0)

            val message =
                choice.optJSONObject("message")
                    ?: throw Exception(
                        "message가 없습니다.\n$responseBody"
                    )

            val content =
                message.optString(
                    "content",
                    ""
                )

            if (content.isBlank()) {
                throw Exception(
                    "AI 답변이 비어 있습니다.\n$responseBody"
                )
            }

            return content.trim()
        }
    }

    // ==============================
    // Clear Chat
    // ==============================

    fun clearChat() {

        _messages.value = listOf(
            ChatMessage(
                text = "대화 내용이 초기화되었습니다. 다시 궁금한 점을 물어보세요! 😊",
                isFromUser = false
            )
        )
    }

    override fun onCleared() {

        super.onCleared()

        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }
}