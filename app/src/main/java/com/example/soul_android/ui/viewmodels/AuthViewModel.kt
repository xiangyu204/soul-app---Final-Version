package com.example.soul_android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soul_android.data.network.LoginRequest
import com.example.soul_android.data.network.SoulApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginResult {

    object Idle : LoginResult()

    object Loading : LoginResult()

    data class Success(
        val username: String
    ) : LoginResult()

    data class Error(
        val message: String
    ) : LoginResult()
}


class AuthViewModel(
    private val apiService: SoulApiService = SoulApiService.create()
) : ViewModel() {

    private val _loginResult =
        MutableStateFlow<LoginResult>(
            LoginResult.Idle
        )

    val loginResult =
        _loginResult.asStateFlow()


    fun login(
        username: String,
        pass: String
    ) {

        viewModelScope.launch {

            _loginResult.value =
                LoginResult.Loading

            try {

                val response =
                    apiService.login(
                        LoginRequest(
                            username,
                            pass
                        )
                    )

                if (response.isSuccessful) {

                    val body =
                        response.body()

                    if (body?.success == true) {

                        // 登录成功
                        // 把 username 一起传回 LoginScreen
                        _loginResult.value =
                            LoginResult.Success(
                                username = body.username
                                    ?: username
                            )

                    } else {

                        _loginResult.value =
                            LoginResult.Error(
                                body?.message
                                    ?: "Login failed"
                            )
                    }

                } else {

                    _loginResult.value =
                        LoginResult.Error(
                            "Server error: ${response.code()}"
                        )
                }

            } catch (e: Exception) {

                e.printStackTrace()

                _loginResult.value =
                    LoginResult.Error(
                        e.message
                            ?: "서버에 연결할 수 없습니다."
                    )
            }
        }
    }


    fun resetResult() {

        _loginResult.value =
            LoginResult.Idle
    }
}