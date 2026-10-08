package com.example.soul_android.data.network

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


data class LoginRequest(
    val username: String,
    val password: String
)


data class LoginResponse(
    val success: Boolean,
    val name: String?,
    val username: String?,
    val email: String?,
    val avatar: String?,
    val role: String?,
    val message: String?
)


data class ProfileResponse(
    val id: Long?,
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
    val ratingCount: Int? = null,

    val role: String?,

    val phone: String?,
    val address: String?,

    val timeSlot: String? = null
)


data class AvatarUploadResponse(

    val success: Boolean,

    val avatar: String?,

    val message: String?
)


data class AiGuideResponse(
    val advice: String
)


data class MatchUserResponse(

    val id: Long,

    val username: String,

    val name: String,

    val age: Int?,

    val gender: String?,

    val nationality: String?,

    val avatar: String?,

    val skillOffer: List<String>? = null,

    val skillWant: List<String>? = null,

    val timeSlot: String?,

    val skillWantLevel: String?,

    val skillOfferLevel: String?,

    val averageRating: Double?,

    val ratingCount: Int?
)


data class MatchHistoryResponse(

    val partnerId: Long?,

    val partnerName: String?,

    val partnerAvatar: String?,

    val matchTime: String?
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

    val histories: List<MatchHistoryResponse>? =
        emptyList()
)


data class ChatSendRequest(

    val senderUsername: String,

    val receiverUsername: String,

    val content: String
)


data class BackendChatMessage(

    val id: Long?,

    val senderUsername: String,

    val receiverUsername: String,

    val content: String,

    val createdAt: String?
)


interface SoulApiService {


    // =====================================================
    // Login
    // =====================================================

    @POST("api/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>


    // =====================================================
    // Profile
    // =====================================================

    @GET("api/user/profile")
    suspend fun getUserProfile(

        @Query("username")
        username: String

    ): Response<ProfileResponse>


    @PUT("api/user/profile")
    suspend fun updateProfile(

        @Body
        profile: ProfileResponse

    ): Response<ProfileResponse>


    // =====================================================
    // Avatar
    // =====================================================

    @Multipart
    @POST("api/user/avatar")
    suspend fun uploadAvatar(

        @Part("username")
        username: RequestBody,

        @Part
        file: MultipartBody.Part

    ): Response<AvatarUploadResponse>


    // =====================================================
    // Match profile
    // =====================================================

    @GET("api/match/profile/{userId}")
    suspend fun getUserMatchProfile(

        @Path("userId")
        userId: Long

    ): UserMatchProfileResponse


    // =====================================================
    // Match
    // =====================================================

    @GET("api/match")
    suspend fun getMatches(

        @Query("username")
        username: String,

        @Query("haveSkill")
        haveSkill: String? = null,

        @Query("wantSkill")
        wantSkill: String? = null,

        @Query("timeSlot")
        timeSlot: String? = null,

        @Query("skillWantLevel")
        skillWantLevel: String? = null,

        @Query("skillOfferLevel")
        skillOfferLevel: String? = null,

        @Query("limit")
        limit: Int = 12

    ): List<MatchUserResponse>


    @GET("api/match/mine")
    suspend fun getMyMatches(

        @Query("username")
        username: String

    ): List<MatchUserResponse>


    // =====================================================
    // Chat
    // =====================================================

    @GET("api/chat/rooms")
    suspend fun getChatRooms(

        @Query("username")
        username: String

    ): List<Map<String, Any>>


    @POST("api/chat/direct")
    suspend fun createDirectChatRoom(

        @Body
        request: ChatSendRequest

    ): Response<Map<String, Any>>


    @GET("api/chat/messages")
    suspend fun getChatMessages(

        @Query("me")
        me: String,

        @Query("partner")
        partner: String

    ): List<BackendChatMessage>


    @POST("api/chat/send")
    suspend fun sendMessage(

        @Body
        request: ChatSendRequest

    ): Response<Map<String, Any>>


    // =====================================================
    // AI
    // =====================================================

    @GET("api/ai/guide")
    suspend fun getAiGuide(

        @Query("username")
        username: String,

        @Query("targetLang")
        targetLang: String

    ): Response<AiGuideResponse>


    @POST("api/ai/translate")
    suspend fun translateText(

        @Query("text")
        text: String,

        @Query("targetLang")
        targetLang: String

    ): Response<AiGuideResponse>


    companion object {

        /*
         * Android Emulator 访问电脑：
         *
         * 10.0.2.2
         */
        const val BASE_URL =
            "http://10.0.2.2:8080/"


        fun create(): SoulApiService {

            val logger =
                HttpLoggingInterceptor()
                    .apply {

                        level =
                            HttpLoggingInterceptor
                                .Level
                                .BODY
                    }


            val client =
                OkHttpClient
                    .Builder()

                    .addInterceptor(
                        logger
                    )

                    .connectTimeout(
                        15,
                        TimeUnit.SECONDS
                    )

                    .readTimeout(
                        15,
                        TimeUnit.SECONDS
                    )

                    .writeTimeout(
                        15,
                        TimeUnit.SECONDS
                    )

                    .build()


            return Retrofit
                .Builder()

                .baseUrl(
                    BASE_URL
                )

                .client(
                    client
                )

                .addConverterFactory(
                    GsonConverterFactory
                        .create()
                )

                .build()

                .create(
                    SoulApiService::class.java
                )
        }
    }
}