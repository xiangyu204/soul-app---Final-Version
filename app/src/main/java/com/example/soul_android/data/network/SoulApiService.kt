package com.example.soul_android.data.network

import com.example.soul_android.models.User
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// --- Auth DTOs ---
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(
    val success: Boolean,
    val name: String?,
    val username: String?,
    val email: String?,
    val avatar: String?,
    val role: String?,
    val message: String?
)

// --- Profile DTOs ---
data class ProfileResponse(
    val id: Long,
    val username: String,
    val name: String?,
    val email: String?,
    val avatar: String?,
    val gender: String?,
    val age: Int?,
    val skillOffer: String?,
    val skillWant: String?,
    val skillOfferLevel: String?,
    val skillWantLevel: String?,
    val nationality: String?,
    val averageRating: Double?,
    val role: String?,
    val phone: String?,
    val address: String?
)

data class AiGuideResponse(
    val advice: String
)

// --- Match DTOs ---
data class MatchUserResponse(
    val id: Long,
    val username: String,
    val name: String,
    val age: Int?,
    val gender: String?,
    val nationality: String?,
    val avatar: String?,
    val skillOffer: String?, // 改为 String? 适配后端数据库存储格式
    val skillWant: String?,  // 改为 String? 适配后端数据库存储格式
    val averageRating: Double?
)

data class MatchHistoryResponse(
    val id: Long,
    val name: String,
    val avatar: String?,
    val matchTime: String? // 后端 LocalDateTime 默认转 String
)

data class UserMatchProfileResponse(
    val id: Long,
    val username: String,
    val name: String?,
    val age: Int?,
    val nationality: String?,
    val avatar: String?,
    val skillOffer: String?,
    val skillWant: String?,
    val averageRating: Double?,
    val ratingCount: Int?,
    val histories: List<MatchHistoryResponse>
)

// --- Chat DTOs ---
data class ChatMessageResponse(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean
)

data class SendMessageRequest(
    val receiverId: String,
    val content: String
)

interface SoulApiService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): retrofit2.Response<LoginResponse>

    // 获取特定用户详情 + 历史记录 (用于匹配详情页)
    @GET("api/match/profile/{userId}")
    suspend fun getUserMatchProfile(@Path("userId") userId: Long): retrofit2.Response<UserMatchProfileResponse>

    // 获取当前登录用户的资料 (对应您的 UserController.java @GetMapping("/profile"))
    @GET("api/user/profile")
    suspend fun getUserProfile(@Query("username") username: String): retrofit2.Response<ProfileResponse>

    // 更新用户资料 (对应您的 UserController.java @PostMapping("/update"))
    @POST("api/user/update")
    suspend fun updateProfile(@Body user: ProfileResponse): retrofit2.Response<ProfileResponse>

    // 智能匹配接口 (对应您的 MatchQueryController.java)
    @GET("api/match")
    suspend fun getMatches(
        @Query("haveSkill") haveSkill: String? = null,
        @Query("wantSkill") wantSkill: String? = null,
        @Query("limit") limit: Int = 12
    ): List<MatchUserResponse>

    // --- Chat APIs (对应您的 ChatController.java) ---
    @GET("api/chat/messages/{userId}")
    suspend fun getChatMessages(@Path("userId") otherUserId: String): List<ChatMessageResponse>

    @POST("api/chat/send")
    suspend fun sendMessage(@Body request: SendMessageRequest): retrofit2.Response<ChatMessageResponse>

    // --- AI Guide API ---
    @GET("api/ai/guide")
    suspend fun getAiGuide(@Query("username") username: String, @Query("targetLang") targetLang: String): retrofit2.Response<AiGuideResponse>

    // --- AI Global Translation API ---
    @POST("api/ai/translate")
    suspend fun translateText(@Query("text") text: String, @Query("targetLang") targetLang: String): retrofit2.Response<AiGuideResponse>

    companion object {
        // 确保端口与您的 Spring Boot (默认 8080) 一致
        private const val BASE_URL = "http://10.0.2.2:8080/" 

        fun create(): SoulApiService {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SoulApiService::class.java)
        }
    }
}
