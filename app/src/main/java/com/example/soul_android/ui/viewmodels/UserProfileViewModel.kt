package com.example.soul_android.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soul_android.data.network.ProfileResponse
import com.example.soul_android.data.network.SoulApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UserProfileUiState {

    object Loading : UserProfileUiState()

    data class Success(
        val profile: ProfileResponse
    ) : UserProfileUiState()

    data class Error(
        val message: String
    ) : UserProfileUiState()
}


class UserProfileViewModel(
    private val apiService: SoulApiService = SoulApiService.create()
) : ViewModel() {

    companion object {
        private const val TAG = "USER_PROFILE"
    }

    private val _uiState =
        MutableStateFlow<UserProfileUiState>(
            UserProfileUiState.Loading
        )

    val uiState = _uiState.asStateFlow()


    fun loadProfile(username: String) {

        if (username.isBlank()) {
            _uiState.value =
                UserProfileUiState.Error(
                    "用户信息不存在"
                )
            return
        }

        viewModelScope.launch {

            _uiState.value =
                UserProfileUiState.Loading

            try {

                Log.d(
                    TAG,
                    "loadProfile username=$username"
                )

                val response =
                    apiService.getUserProfile(
                        username = username
                    )

                if (!response.isSuccessful) {

                    val errorBody =
                        response.errorBody()
                            ?.string()

                    Log.e(
                        TAG,
                        "HTTP ERROR ${response.code()} $errorBody"
                    )

                    _uiState.value =
                        UserProfileUiState.Error(
                            errorBody
                                ?: "获取用户资料失败 HTTP ${response.code()}"
                        )

                    return@launch
                }

                val profile =
                    response.body()

                if (profile == null) {

                    _uiState.value =
                        UserProfileUiState.Error(
                            "用户资料为空"
                        )

                    return@launch
                }

                Log.d(
                    TAG,
                    "profile loaded: ${profile.username}"
                )

                _uiState.value =
                    UserProfileUiState.Success(
                        profile
                    )

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "loadProfile failed",
                    e
                )

                _uiState.value =
                    UserProfileUiState.Error(
                        e.localizedMessage
                            ?: "无法连接服务器"
                    )
            }
        }
    }
}