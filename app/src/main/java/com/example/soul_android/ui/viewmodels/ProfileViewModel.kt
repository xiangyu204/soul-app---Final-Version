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
    val nationality: String,
    val age: String,
    val gender: String,
    val phone: String,
    val address: String,
    val skillOfferLevel: String,
    val skillWantLevel: String,
    val timeSlot: String,
    val projects: String
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
                        nationality = body.nationality ?: "-",
                        age = body.age?.toString() ?: "-",
                        gender = body.gender ?: "-",
                        phone = body.phone ?: "-",
                        address = body.address ?: "-",
                        skillOfferLevel = body.skillOfferLevel ?: "입문",
                        skillWantLevel = body.skillWantLevel ?: "입문",
                        timeSlot = "평일 오전", // 后端暂无此字段，先硬编码对应 UI
                        projects = "-"       // 后端暂无此字段，先硬编码对应 UI
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

    fun updateProfile(updatedData: ProfileUiData) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is ProfileUiState.Success) {
                    val original = currentState.data
                    // 构造后端 ProfileResponse DTO
                    val dto = ProfileResponse(
                        id = 0, // 后端通常从 Session 识别，ID 可传 0
                        username = updatedData.username,
                        name = updatedData.name,
                        email = updatedData.email,
                        avatar = original.avatar,
                        gender = updatedData.gender,
                        age = updatedData.age.toIntOrNull() ?: 0,
                        skillOffer = updatedData.teachSkills.joinToString(","),
                        skillWant = updatedData.learnSkills.joinToString(","),
                        skillOfferLevel = updatedData.skillOfferLevel,
                        skillWantLevel = updatedData.skillWantLevel,
                        nationality = updatedData.nationality,
                        phone = updatedData.phone,
                        address = updatedData.address,
                        averageRating = original.rating.replace("★ ", "").toDoubleOrNull() ?: 0.0,
                        role = "USER"
                    )

                    val response = apiService.updateProfile(dto)
                    if (response.isSuccessful) {
                        // 更新成功后，同步本地 UI 状态
                        _uiState.value = ProfileUiState.Success(updatedData)
                    } else {
                        _uiState.value = ProfileUiState.Error("Update failed: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }
}
