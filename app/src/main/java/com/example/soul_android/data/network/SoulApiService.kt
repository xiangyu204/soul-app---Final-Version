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

// --- Match DTOs ---
data class MatchUserResponse(
    val id: Long,
    val username: String,
    val name: String,
    val age: Int?,
    val gender: String?,
    val nationality: String?,
    val avatar: String?,
    val skillOffer: List<String>,
    val skillWant: List<String>,
    val timeSlot: String?,
    val skillWantLevel: String?,
    val skillOfferLevel: String?,
    val averageRating: Double?,
    val ratingCount: Int?
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

interface SoulApiService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): retrofit2.Response<LoginResponse>

    // 获取特定用户详情 + 历史记录 (用于匹配详情页)
    @GET("api/match/profile/{userId}")
    suspend fun getUserMatchProfile(@Path("userId") userId: Long): retrofit2.Response<UserMatchProfileResponse>

    // 获取当前登录用户的资料 (用于个人资料页)
    @GET("api/user/profile")
    suspend fun getUserProfile(@Query("username") username: String): retrofit2.Response<ProfileResponse>

    // 智能匹配接口
    @GET("api/match")
    suspend fun getMatches(
        @Query("haveSkill") haveSkill: String? = null,
        @Query("wantSkill") wantSkill: String? = null,
        @Query("timeSlot") timeSlot: String? = null,
        @Query("skillWantLevel") wantLevel: String? = null,
        @Query("skillOfferLevel") offerLevel: String? = null,
        @Query("limit") limit: Int = 5
    ): List<MatchUserResponse>

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/"

        fun create(): SoulApiService {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            val client = OkHttpClient.Builder().addInterceptor(logger).build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SoulApiService::class.java)
        }
    }
}
