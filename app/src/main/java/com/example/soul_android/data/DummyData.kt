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

    var users = listOf(
        User(id = "sarah", name = "Sarah", bio = "English native speaker", languages = listOf("English"), teachSkills = listOf("영어"), learnSkills = listOf("한국어"), matchRate = 95, isOnline = true),
        User(id = "chen", name = "Chen", bio = "Software engineer", languages = listOf("Chinese"), teachSkills = listOf("중국어"), learnSkills = listOf("영어"), matchRate = 88, isOnline = false),
        User(id = "alex", name = "Alex", bio = "Professional coder", languages = listOf("English"), teachSkills = listOf("Python"), learnSkills = listOf("디자인"), matchRate = 82, isOnline = true),
        User(id = "victoria", name = "Victoria", bio = "UX designer", languages = listOf("French"), teachSkills = listOf("디자인"), learnSkills = listOf("Python"), matchRate = 78, isOnline = true),
        User(id = "minji", name = "Minji", bio = "K-pop lover", languages = listOf("Korean"), teachSkills = listOf("Dance"), learnSkills = listOf("English"), matchRate = 92, isOnline = true),
        User(id = "john", name = "John", bio = "History buff", languages = listOf("English"), teachSkills = listOf("History"), learnSkills = listOf("Korean"), matchRate = 75, isOnline = true),
        User(id = "yuki", name = "Yuki", bio = "Anime fan", languages = listOf("Japanese"), teachSkills = listOf("Japanese"), learnSkills = listOf("Design"), matchRate = 85, isOnline = true),
        User(id = "leo", name = "Leo", bio = "Coffee enthusiast", languages = listOf("Italian"), teachSkills = listOf("Cooking"), learnSkills = listOf("Java"), matchRate = 80, isOnline = true),
        User(id = "sophia", name = "Sophia", bio = "Nature photographer", languages = listOf("German"), teachSkills = listOf("Photo"), learnSkills = listOf("Python"), matchRate = 89, isOnline = true),
        User(id = "ryan", name = "Ryan", bio = "Fitness coach", languages = listOf("English"), teachSkills = listOf("Gym"), learnSkills = listOf("Chinese"), matchRate = 83, isOnline = true),
        User(id = "jiwon", name = "Jiwon", bio = "Game developer", languages = listOf("Korean"), teachSkills = listOf("Unity"), learnSkills = listOf("Music"), matchRate = 94, isOnline = true),
        User(id = "emma", name = "Emma", bio = "Book worm", languages = listOf("English"), teachSkills = listOf("Literature"), learnSkills = listOf("German"), matchRate = 77, isOnline = true)
    )

    val activeChatUsers = mutableListOf<User>(
        User(id = "究极魔丸", name = "究极魔丸", bio = "안녕하세요!", languages = listOf("Korean"), teachSkills = listOf("Python"), learnSkills = listOf("Java"), matchRate = 95, isOnline = true),
        User(id = "오태양", name = "오태양", bio = "반갑습니다!", languages = listOf("Korean"), teachSkills = listOf("Java"), learnSkills = listOf("Python"), matchRate = 90, isOnline = true),
        User(id = "박하늘", name = "박하늘", bio = "열심히 공부해요!", languages = listOf("Korean"), teachSkills = listOf("Spring"), learnSkills = listOf("React"), matchRate = 88, isOnline = true),
        User(id = "huangjinghao", name = "huangjinghao", bio = "你好！", languages = listOf("Chinese"), teachSkills = listOf("Chinese"), learnSkills = listOf("English"), matchRate = 85, isOnline = true)
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
