package com.example.soul_android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soul_android.data.network.ProfileResponse
import com.example.soul_android.data.network.SoulApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 为 UI 准备的包装模型
data class ProfileUiData(
    val name: String,
    val username: String,
    val email: String,
    val avatar: String?,
    val teachSkills: List<String>,
    val learnSkills: List<String>,
    val rating: String,
    val nationality: String
)

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val data: ProfileUiData) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    private val apiService: SoulApiService = SoulApiService.create()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun fetchProfile(username: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val response = apiService.getUserProfile(username)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    
                    // 将后端的 String 转换为 UI 需要的 List
                    val uiData = ProfileUiData(
                        name = body.name ?: body.username,
                        username = body.username,
                        email = body.email ?: "",
                        avatar = body.avatar,
                        teachSkills = body.skillOffer?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
                        learnSkills = body.skillWant?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
                        rating = "★ ${body.averageRating ?: 0.0}",
                        nationality = body.nationality ?: ""
                    )
                    
                    _uiState.value = ProfileUiState.Success(uiData)
                } else {
                    _uiState.value = ProfileUiState.Error("Failed to load profile: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
