package com.example.soul_android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soul_android.data.remote.QuizAnswerRequest
import com.example.soul_android.data.remote.QuizQuestionResponse
import com.example.soul_android.data.remote.QuizStartRequest
import com.example.soul_android.data.remote.QuizSubmitRequest
import com.example.soul_android.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


// =====================================================
// UI State
// =====================================================

sealed class QuizUiState {

    data object Idle : QuizUiState()

    data object Loading : QuizUiState()

    data class Success(
        val questions: List<QuizQuestionResponse>
    ) : QuizUiState()

    data class Graded(
        val score: Int,
        val total: Int,
        val level: String,
        val levelText: String
    ) : QuizUiState()

    data class Error(
        val message: String
    ) : QuizUiState()
}


// =====================================================
// ViewModel
// =====================================================

class AiQuizViewModel : ViewModel() {

    private val api =
        RetrofitClient.quizApi


    private val _uiState =
        MutableStateFlow<QuizUiState>(
            QuizUiState.Idle
        )


    val uiState: StateFlow<QuizUiState> =
        _uiState.asStateFlow()


    // =================================================
    // Current Quiz Data
    // =================================================

    private var currentQuizId: Long? =
        null


    private var currentSkill: String =
        ""


    private var currentQuizType: String =
        ""


    private var currentQuestions:
            List<QuizQuestionResponse> =
        emptyList()


    // =================================================
    // Generate Quiz
    // =================================================

    fun generateQuiz(
        topic: String,
        username: String,
        targetLang: String = "ko",
        quizType: String = "learn"
    ) {

        // 检查主题
        if (topic.isBlank()) {

            _uiState.value =
                QuizUiState.Error(
                    "학습 주제를 입력해주세요."
                )

            return
        }


        // 检查登录
        if (username.isBlank()) {

            _uiState.value =
                QuizUiState.Error(
                    "로그인이 필요합니다."
                )

            return
        }


        // 清除上一次 Quiz
        currentQuizId = null
        currentQuestions = emptyList()

        currentSkill = topic
        currentQuizType = quizType


        viewModelScope.launch {

            _uiState.value =
                QuizUiState.Loading


            try {

                val request =
                    QuizStartRequest(
                        skill = topic,
                        quizType = quizType,
                        targetLang = targetLang,
                        username = username
                    )


                println(
                    "========== ANDROID QUIZ START =========="
                )

                println(
                    "username = $username"
                )

                println(
                    "skill = $topic"
                )

                println(
                    "quizType = $quizType"
                )

                println(
                    "targetLang = $targetLang"
                )


                val response =
                    api.startQuiz(
                        request
                    )


                // 后端返回检查
                if (response.questions.isEmpty()) {

                    _uiState.value =
                        QuizUiState.Error(
                            "퀴즈 문제가 생성되지 않았습니다."
                        )

                    return@launch
                }


                // 保存当前测试信息
                currentQuizId =
                    response.quizId

                currentSkill =
                    response.skill

                currentQuizType =
                    response.quizType

                currentQuestions =
                    response.questions


                println(
                    "Quiz ID = ${response.quizId}"
                )

                println(
                    "Questions = ${response.questions.size}"
                )


                _uiState.value =
                    QuizUiState.Success(
                        questions =
                            response.questions
                    )


            } catch (e: Exception) {

                e.printStackTrace()


                _uiState.value =
                    QuizUiState.Error(
                        message =
                            "퀴즈 생성 실패: ${
                                e.message ?: "서버 연결 오류"
                            }"
                    )
            }
        }
    }


    // =================================================
    // Submit Answers
    // =================================================

    fun submitAnswers(
        username: String,
        selections: Map<Long, Int>
    ) {

        // 检查登录
        if (username.isBlank()) {

            _uiState.value =
                QuizUiState.Error(
                    "로그인이 필요합니다."
                )

            return
        }


        // 必须有真实 quizId
        val quizId =
            currentQuizId


        if (quizId == null) {

            _uiState.value =
                QuizUiState.Error(
                    "Quiz ID가 없습니다. 퀴즈를 다시 생성해주세요."
                )

            return
        }


        // 必须有题
        if (currentQuestions.isEmpty()) {

            _uiState.value =
                QuizUiState.Error(
                    "퀴즈 문제가 없습니다."
                )

            return
        }


        // 必须全部回答
        if (
            selections.size
            !=
            currentQuestions.size
        ) {

            _uiState.value =
                QuizUiState.Error(
                    "모든 문제에 답해주세요."
                )

            return
        }


        viewModelScope.launch {

            _uiState.value =
                QuizUiState.Loading


            try {

                val answersList =
                    currentQuestions.map { question ->

                        val selectedIndex =
                            selections[
                                question.questionId
                            ]
                                ?: throw IllegalStateException(
                                    "선택하지 않은 문제가 있습니다."
                                )


                        if (
                            selectedIndex
                            !in
                            question.options.indices
                        ) {

                            throw IllegalStateException(
                                "잘못된 답안 선택입니다."
                            )
                        }


                        val answerText =
                            question.options[
                                selectedIndex
                            ]


                        QuizAnswerRequest(
                            questionId =
                                question.questionId,

                            answer =
                                answerText
                        )
                    }


                val submitRequest =
                    QuizSubmitRequest(
                        quizId = quizId,
                        username = username,
                        skill = currentSkill,
                        quizType = currentQuizType,
                        answers = answersList
                    )


                println(
                    "========== ANDROID QUIZ SUBMIT =========="
                )

                println(
                    "quizId = $quizId"
                )

                println(
                    "username = $username"
                )

                println(
                    "skill = $currentSkill"
                )

                println(
                    "quizType = $currentQuizType"
                )

                println(
                    "answers = $answersList"
                )


                val response =
                    api.submitQuiz(
                        submitRequest
                    )


                println(
                    "========== QUIZ RESULT =========="
                )

                println(
                    "score = ${response.score}/${response.total}"
                )

                println(
                    "level = ${response.level}"
                )


                _uiState.value =
                    QuizUiState.Graded(
                        score =
                            response.score,

                        total =
                            response.total,

                        level =
                            response.level,

                        levelText =
                            response.levelText
                    )


                // 已提交完成
                // 防止用户重复提交同一个 quizId
                currentQuizId = null


            } catch (e: Exception) {

                e.printStackTrace()


                _uiState.value =
                    QuizUiState.Error(
                        message =
                            "채점 실패: ${
                                e.message ?: "서버 연결 오류"
                            }"
                    )
            }
        }
    }


    // =================================================
    // Reset
    // =================================================

    fun resetQuiz() {

        currentQuizId =
            null

        currentSkill =
            ""

        currentQuizType =
            ""

        currentQuestions =
            emptyList()

        _uiState.value =
            QuizUiState.Idle
    }
}