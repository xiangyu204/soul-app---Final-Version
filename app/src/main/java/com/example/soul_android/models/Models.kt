package com.example.soul_android.models

data class User(
    val id: String,
    val name: String,
    val bio: String = "",
    val profileImage: Int? = null,
    val languages: List<String> = emptyList(),
    val teachSkills: List<String> = emptyList(),
    val learnSkills: List<String> = emptyList(),
    val matchRate: Int = 0,
    val isOnline: Boolean = false
)

data class Skill(
    val name: String,
    val category: String
)

data class MatchRequest(
    val id: String,
    val sender: User,
    val receiver: User,
    val status: MatchStatus = MatchStatus.PENDING,
    val message: String = ""
)

enum class MatchStatus {
    PENDING, ACCEPTED, REJECTED
}

data class Message(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: Long,
    val isMe: Boolean
)

data class Session(
    val id: String,
    val user: User,
    val skill: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val notes: String = ""
)

data class Review(
    val id: String,
    val reviewerId: String,
    val revieweeId: String,
    val rating: Int,
    val comment: String,
    val skill: String
)
