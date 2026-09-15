package com.example.soul_android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soul_android.data.network.MatchUserResponse
import com.example.soul_android.data.network.SoulApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MatchUiState {
    object Loading : MatchUiState()
    data class Success(val users: List<MatchUserResponse>) : MatchUiState()
    data class Error(val message: String) : MatchUiState()
}

class MatchViewModel(
    private val apiService: SoulApiService = SoulApiService.create()
) : ViewModel() {

    private val _uiState = MutableStateFlow<MatchUiState>(MatchUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun findMatches(have: String? = null, want: String? = null) {
        viewModelScope.launch {
            _uiState.value = MatchUiState.Loading
            try {
                // 调用后端 /api/match
                val users = apiService.getMatches(haveSkill = have, wantSkill = want)
                _uiState.value = MatchUiState.Success(users)
            } catch (e: Exception) {
                _uiState.value = MatchUiState.Error(e.localizedMessage ?: "Match failed")
            }
        }
    }
}
