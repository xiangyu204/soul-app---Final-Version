package com.example.soul_android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soul_android.data.DummyData
import com.example.soul_android.data.network.SoulApiService
import com.example.soul_android.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ChatListUiState {
    object Loading : ChatListUiState()
    data class Success(val rooms: List<User>) : ChatListUiState()
    data class Error(val message: String) : ChatListUiState()
}

class ChatListViewModel(
    private val apiService: SoulApiService = SoulApiService.create()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatListUiState>(ChatListUiState.Loading)
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    fun fetchRooms(username: String = "xiangyu") {
        viewModelScope.launch {
            _uiState.value = ChatListUiState.Loading
            try {
                val roomMaps = apiService.getChatRooms(username)
                val backendUsers = roomMaps.map { map ->
                    val partnerUsername = map["partnerUsername"] as? String ?: "user"
                    val partnerName = map["partnerName"] as? String ?: partnerUsername
                    val lastMsg = map["lastMessage"] as? String ?: ""
                    
                    User(
                        id = partnerUsername,
                        name = partnerName,
                        bio = lastMsg,
                        languages = listOf(),
                        teachSkills = listOf(),
                        learnSkills = listOf(),
                        matchRate = 90,
                        isOnline = true
                    )
                }
                
                // 严格按 ID 去重，避免重复显示
                val combined = (backendUsers + DummyData.activeChatUsers).distinctBy { it.id }
                _uiState.value = ChatListUiState.Success(combined.ifEmpty { DummyData.users.take(6).distinctBy { it.id } })
            } catch (e: Exception) {
                val fallback = (DummyData.activeChatUsers + DummyData.users.take(6)).distinctBy { it.id }
                _uiState.value = ChatListUiState.Success(fallback)
            }
        }
    }
}
