package com.example.soul_android.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soul_android.data.network.ChatSendRequest
import com.example.soul_android.data.network.MatchUserResponse
import com.example.soul_android.data.network.SoulApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class MatchUiState {

    object Loading : MatchUiState()


    data class Success(

        val recommendedUsers: List<MatchUserResponse>,

        val myMatches: List<MatchUserResponse>

    ) : MatchUiState()


    data class Error(
        val message: String
    ) : MatchUiState()
}


class MatchViewModel(

    private val apiService:
    SoulApiService =
        SoulApiService.create()

) : ViewModel() {


    companion object {

        private const val TAG =
            "MATCH_DEBUG"
    }


    private val _uiState =
        MutableStateFlow<MatchUiState>(
            MatchUiState.Loading
        )


    val uiState =
        _uiState.asStateFlow()


    // =====================================================
    // 获取推荐用户 + 我的匹配
    // =====================================================

    fun findMatches(

        username: String,

        have: String? = null,

        want: String? = null,

        timeSlot: String? = null,

        skillWantLevel: String? = null,

        skillOfferLevel: String? = null

    ) {


        if (username.isBlank()) {

            _uiState.value =
                MatchUiState.Error(
                    "当前登录用户为空"
                )

            return
        }


        viewModelScope.launch {


            _uiState.value =
                MatchUiState.Loading


            try {


                Log.d(
                    TAG,
                    "findMatches username=$username"
                )


                val recommended =
                    apiService.getMatches(

                        username =
                            username,

                        haveSkill =
                            have,

                        wantSkill =
                            want,

                        timeSlot =
                            timeSlot,

                        skillWantLevel =
                            skillWantLevel,

                        skillOfferLevel =
                            skillOfferLevel,

                        limit =
                            12
                    )


                val matchedUsers =
                    apiService.getMyMatches(

                        username =
                            username
                    )


                Log.d(
                    TAG,
                    "recommended=${recommended.size}, " +
                            "myMatches=${matchedUsers.size}"
                )


                _uiState.value =
                    MatchUiState.Success(

                        recommendedUsers =
                            recommended,

                        myMatches =
                            matchedUsers
                    )


            } catch (e: Exception) {


                Log.e(
                    TAG,
                    "findMatches FAILED",
                    e
                )


                _uiState.value =
                    MatchUiState.Error(

                        e.localizedMessage
                            ?: "匹配数据获取失败"
                    )
            }
        }
    }


    // =====================================================
    // 真正建立匹配
    // =====================================================

    fun acceptMatch(

        currentUsername: String,

        targetUsername: String,

        onSuccess: () -> Unit = {},

        onError: (String) -> Unit = {}

    ) {


        if (
            currentUsername.isBlank()
            || targetUsername.isBlank()
        ) {

            onError(
                "用户名不能为空"
            )

            return
        }


        if (
            currentUsername ==
            targetUsername
        ) {

            onError(
                "不能和自己匹配"
            )

            return
        }


        viewModelScope.launch {


            try {


                Log.d(
                    TAG,
                    "acceptMatch: " +
                            "$currentUsername -> $targetUsername"
                )


                /*
                 * 当前项目：
                 *
                 * ChatRoom 就是匹配关系。
                 *
                 * 创建 ChatRoom =
                 * 真正匹配成功。
                 */
                val response =
                    apiService
                        .createDirectChatRoom(

                            ChatSendRequest(

                                senderUsername =
                                    currentUsername,

                                receiverUsername =
                                    targetUsername,

                                content =
                                    ""
                            )
                        )


                /*
                 * HTTP 层失败
                 */
                if (!response.isSuccessful) {


                    val error =
                        response
                            .errorBody()
                            ?.string()
                            ?: "HTTP ${response.code()}"


                    Log.e(
                        TAG,
                        "acceptMatch HTTP FAILED: $error"
                    )


                    onError(
                        error
                    )

                    return@launch
                }


                /*
                 * Spring Boot 现在业务错误也可能返回 HTTP 200。
                 *
                 * 所以必须继续检查：
                 *
                 * success=true/false
                 */
                val body =
                    response.body()


                val success =
                    body
                        ?.get("success")
                            as? Boolean
                        ?: false


                if (!success) {


                    val message =
                        body
                            ?.get("message")
                            ?.toString()
                            ?: "匹配失败"


                    Log.e(
                        TAG,
                        "acceptMatch business FAILED: $message"
                    )


                    onError(
                        message
                    )

                    return@launch
                }


                Log.d(
                    TAG,
                    "MATCH SUCCESS: $body"
                )


                /*
                 * 匹配成功后：
                 *
                 * 重新获取我的匹配，
                 * 同时把这个用户从推荐列表删除。
                 */
                refreshAfterMatch(

                    username =
                        currentUsername,

                    matchedUsername =
                        targetUsername
                )


                onSuccess()


            } catch (e: Exception) {


                Log.e(
                    TAG,
                    "acceptMatch EXCEPTION",
                    e
                )


                onError(

                    e.localizedMessage
                        ?: "匹配失败"
                )
            }
        }
    }


    // =====================================================
    // 当前项目没有 Reject 数据表
    //
    // 所以“跳过”只在当前推荐列表隐藏
    // =====================================================

    fun rejectLocally(
        targetUsername: String
    ) {


        val current =
            _uiState.value


        if (
            current
                    !is MatchUiState.Success
        ) {

            return
        }


        _uiState.value =
            current.copy(

                recommendedUsers =
                    current
                        .recommendedUsers
                        .filter {

                            it.username !=
                                    targetUsername
                        }
            )
    }


    // =====================================================
    // 匹配成功后的刷新
    // =====================================================

    private suspend fun refreshAfterMatch(

        username: String,

        matchedUsername: String

    ) {


        val current =
            _uiState.value


        try {


            val matchedUsers =
                apiService.getMyMatches(

                    username =
                        username
                )


            if (
                current
                        is MatchUiState.Success
            ) {


                _uiState.value =
                    MatchUiState.Success(

                        recommendedUsers =
                            current
                                .recommendedUsers
                                .filter {

                                    it.username !=
                                            matchedUsername
                                },

                        myMatches =
                            matchedUsers
                    )


            } else {


                val recommended =
                    apiService.getMatches(

                        username =
                            username
                    )


                _uiState.value =
                    MatchUiState.Success(

                        recommendedUsers =
                            recommended,

                        myMatches =
                            matchedUsers
                    )
            }


        } catch (e: Exception) {


            Log.e(
                TAG,
                "refreshAfterMatch FAILED",
                e
            )
        }
    }
}