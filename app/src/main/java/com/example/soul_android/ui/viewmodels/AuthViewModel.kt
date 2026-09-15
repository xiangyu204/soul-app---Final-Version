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
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
}

class AuthViewModel(
    private val apiService: SoulApiService = SoulApiService.create()
) : ViewModel() {

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult = _loginResult.asStateFlow()

    fun login(username: String, pass: String) {
        viewModelScope.launch {
            _loginResult.value = LoginResult.Loading
            try {
                // 调用后端 api/login
                val response = apiService.login(LoginRequest(username, pass))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        _loginResult.value = LoginResult.Success
                    } else {
                        // 显示后端返回的错误消息，例如 "Invalid username or password"
                        _loginResult.value = LoginResult.Error(body?.message ?: "Login failed")
                    }
                } else {
                    _loginResult.value = LoginResult.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error(e.localizedMessage ?: "Network error")
            }
        }
    }
    
    fun resetResult() {
        _loginResult.value = LoginResult.Idle
    }
}
