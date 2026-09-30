package com.example.soul_android.data.remote


// =========================
// Quiz Start
// =========================

// 对应 Spring Boot:
// QuizStartRequest.java
data class QuizStartRequest(
    val skill: String,
    val quizType: String,
    val targetLang: String,
    val username: String
)


// 对应 Spring Boot:
// QuizStartResponse.java
data class QuizStartResponse(
    val quizId: Long,
    val skill: String,
    val quizType: String,
    val questions: List<QuizQuestionResponse>
)


// 对应 Spring Boot:
// QuizQuestionResponse.java
data class QuizQuestionResponse(
    val questionId: Long,
    val question: String,
    val options: List<String>
)


// =========================
// Quiz Submit
// =========================

// 对应 Spring Boot:
// QuizAnswerRequest.java
data class QuizAnswerRequest(
    val questionId: Long,
    val answer: String
)


// 对应 Spring Boot:
// QuizSubmitRequest.java
data class QuizSubmitRequest(
    val quizId: Long,
    val username: String,
    val skill: String,
    val quizType: String,
    val answers: List<QuizAnswerRequest>
)


// 对应 Spring Boot:
// QuizSubmitResponse.java
data class QuizSubmitResponse(
    val score: Int,
    val total: Int,
    val level: String,
    val levelText: String
)