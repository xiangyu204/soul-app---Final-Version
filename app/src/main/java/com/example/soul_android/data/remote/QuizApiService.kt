package com.example.soul_android.data.remote

import retrofit2.http.Body
import retrofit2.http.POST


interface QuizApiService {

    // Spring Boot:
    // POST /api/quiz/start
    @POST("api/quiz/start")
    suspend fun startQuiz(
        @Body request: QuizStartRequest
    ): QuizStartResponse


    // Spring Boot:
    // POST /api/quiz/submit
    @POST("api/quiz/submit")
    suspend fun submitQuiz(
        @Body request: QuizSubmitRequest
    ): QuizSubmitResponse
}