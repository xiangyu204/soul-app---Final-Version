package com.example.soul_android.data

import com.example.soul_android.models.User
import com.example.soul_android.models.MatchRequest
import com.example.soul_android.models.Message

object DummyData {
    val currentUser = User(
        id = "me",
        name = "김철수",
        bio = "안녕하세요! 새로운 기술을 배우고 싶습니다.",
        languages = listOf("한국어", "English"),
        teachSkills = listOf("Java", "Spring Boot"),
        learnSkills = listOf("Python", "Design")
    )

    val users = listOf(
        User(
            id = "sarah",
            name = "Sarah",
            bio = "English native speaker, loves hiking.",
            languages = listOf("English", "Korean"),
            teachSkills = listOf("영어", "사진"),
            learnSkills = listOf("한국어"),
            matchRate = 95,
            isOnline = true
        ),
        User(
            id = "chen",
            name = "Chen",
            bio = "Software engineer, tea lover.",
            languages = listOf("Chinese", "English"),
            teachSkills = listOf("중국어", "요리"),
            learnSkills = listOf("영어"),
            matchRate = 88,
            isOnline = false
        ),
        User(
            id = "alex",
            name = "Alex",
            bio = "Professional coder and part-time DJ.",
            languages = listOf("English", "Spanish"),
            teachSkills = listOf("Python", "프로그래밍"),
            learnSkills = listOf("디자인"),
            matchRate = 82,
            isOnline = true
        ),
        User(
            id = "victoria",
            name = "Victoria",
            bio = "UX designer and yoga enthusiast.",
            languages = listOf("English", "French"),
            teachSkills = listOf("디자인", "요가"),
            learnSkills = listOf("Python"),
            matchRate = 78,
            isOnline = true
        )
    )

    val matchRequests = listOf(
        MatchRequest(
            id = "req1",
            sender = users[0],
            receiver = currentUser,
            message = "안녕하세요! 영어 회화 배우고 싶어요."
        ),
        MatchRequest(
            id = "req2",
            sender = users[1],
            receiver = currentUser,
            message = "Hello! I can help you with Chinese."
        )
    )

    val chatMessages = listOf(
        Message("1", "alex", "안녕하세요! Python을 배우고 싶다고 해서 연락드렸어요.", System.currentTimeMillis() - 3600000, false),
        Message("2", "me", "안녕하세요! 저는 Python을 가르쳐드릴 수 있어요.", System.currentTimeMillis() - 3000000, true),
        Message("3", "alex", "좋아요! 이번 주에 시간 괜찮으세요?", System.currentTimeMillis() - 2400000, false)
    )
}
