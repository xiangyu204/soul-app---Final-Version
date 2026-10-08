package com.example.soul_android.ui.screens

import android.content.Context

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.soul_android.models.AppLanguage
import com.example.soul_android.models.User

import com.example.soul_android.ui.components.BackgroundGalaxy
import com.example.soul_android.ui.components.BrandingSection
import com.example.soul_android.ui.components.LanguageSelector
import com.example.soul_android.ui.components.SoulTextField

import com.example.soul_android.ui.viewmodels.MatchUiState
import com.example.soul_android.ui.viewmodels.MatchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onUserClick: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToMatches: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    matchViewModel: MatchViewModel = viewModel()
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableIntStateOf(0) }

    val matchState by matchViewModel.uiState.collectAsState()

    val context = LocalContext.current
    val currentUsername = remember(context) {
        val soulPrefs = context.getSharedPreferences("soul_login_prefs", Context.MODE_PRIVATE)
        val userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        soulPrefs.getString("username", "")?.takeIf { it.isNotBlank() }
            ?: userPrefs.getString("username", "")?.takeIf { it.isNotBlank() }
            ?: ""
    }

    val strings = ExploreStrings(
        title = "탐색",
        searchPlaceholder = "스킬 또는 사용자 검색",
        categories = listOf("전체", "언어", "프로그래밍", "음악", "디자인", "스포츠", "기타"),
        viewProfile = "프로필 보기",
        learnPrefix = "학습 희망: ",
        matchRate = "매칭률 ",
        home = "홈",
        explore = "탐색",
        matches = "매칭",
        chat = "채팅",
        profile = "프로필"
    )

    // 监听搜索词和分类变化，使用真实登录用户请求后端
    LaunchedEffect(currentUsername) {

        if (currentUsername.isBlank()) {
            return@LaunchedEffect
        }

        // 只从后端获取当前用户可推荐的全部用户
        // 不把 “언어 / 프로그래밍” 这种大分类发给后端
        matchViewModel.findMatches(
            username = currentUsername
        )
    }

    val displayUsers =
        remember(
            matchState,
            searchQuery,
            selectedCategory
        ) {

            val state = matchState

            if (state !is MatchUiState.Success) {

                emptyList()

            } else {

                // =================================================
                // 后端数据 -> User
                // =================================================

                val allUsers =
                    state.recommendedUsers.map { resp ->

                        User(
                            id = resp.username,
                            name = resp.name,
                            bio = resp.nationality ?: "",
                            languages = emptyList(),

                            teachSkills =
                                resp.skillOffer ?: emptyList(),

                            learnSkills =
                                resp.skillWant ?: emptyList(),

                            matchRate =
                                ((resp.averageRating ?: 4.0) * 20)
                                    .toInt()
                                    .coerceIn(
                                        0,
                                        100
                                    ),

                            isOnline = true
                        )
                    }


                // =================================================
                // 搜索
                // =================================================

                val keyword =
                    searchQuery
                        .trim()
                        .lowercase()


                val searchedUsers =
                    if (keyword.isBlank()) {

                        allUsers

                    } else {

                        allUsers.filter { user ->

                            user.name
                                .lowercase()
                                .contains(keyword)

                                    ||

                                    user.id
                                        .lowercase()
                                        .contains(keyword)

                                    ||

                                    user.teachSkills.any { skill ->
                                        skill
                                            .lowercase()
                                            .contains(keyword)
                                    }

                                    ||

                                    user.learnSkills.any { skill ->
                                        skill
                                            .lowercase()
                                            .contains(keyword)
                                    }
                        }
                    }


                // =================================================
                // 分类
                // =================================================

                when (selectedCategory) {

                    // 전체
                    0 -> searchedUsers


                    // 언어
                    1 -> {

                        searchedUsers.filter { user ->

                            val skills =
                                (
                                        user.teachSkills +
                                                user.learnSkills
                                        )
                                    .joinToString(" ")
                                    .lowercase()


                            listOf(
                                "english",
                                "korean",
                                "chinese",
                                "japanese",
                                "french",
                                "spanish",
                                "영어",
                                "한국어",
                                "중국어",
                                "일본어",
                                "프랑스어",
                                "스페인어",
                                "英语",
                                "韩语",
                                "中文",
                                "日语",
                                "法语",
                                "西班牙语"
                            ).any { word ->

                                skills.contains(
                                    word.lowercase()
                                )
                            }
                        }
                    }


                    // 프로그래밍
                    2 -> {

                        searchedUsers.filter { user ->

                            val skills =
                                (
                                        user.teachSkills +
                                                user.learnSkills
                                        )
                                    .joinToString(" ")
                                    .lowercase()


                            listOf(
                                "java",
                                "kotlin",
                                "python",
                                "javascript",
                                "typescript",
                                "react",
                                "vue",
                                "spring",
                                "spring boot",
                                "android",
                                "sql",
                                "html",
                                "css",
                                "c++",
                                "c#",
                                "프로그래밍",
                                "개발",
                                "编程"
                            ).any { word ->

                                skills.contains(
                                    word.lowercase()
                                )
                            }
                        }
                    }


                    // 음악
                    3 -> {

                        searchedUsers.filter { user ->

                            val skills =
                                (
                                        user.teachSkills +
                                                user.learnSkills
                                        )
                                    .joinToString(" ")
                                    .lowercase()


                            listOf(
                                "music",
                                "guitar",
                                "piano",
                                "sing",
                                "singing",
                                "음악",
                                "기타",
                                "피아노",
                                "노래",
                                "音乐",
                                "吉他",
                                "钢琴",
                                "唱歌"
                            ).any { word ->

                                skills.contains(
                                    word.lowercase()
                                )
                            }
                        }
                    }


                    // 디자인
                    4 -> {

                        searchedUsers.filter { user ->

                            val skills =
                                (
                                        user.teachSkills +
                                                user.learnSkills
                                        )
                                    .joinToString(" ")
                                    .lowercase()


                            listOf(
                                "design",
                                "ui",
                                "ux",
                                "figma",
                                "photoshop",
                                "illustrator",
                                "디자인",
                                "设计"
                            ).any { word ->

                                skills.contains(
                                    word.lowercase()
                                )
                            }
                        }
                    }


                    // 스포츠
                    5 -> {

                        searchedUsers.filter { user ->

                            val skills =
                                (
                                        user.teachSkills +
                                                user.learnSkills
                                        )
                                    .joinToString(" ")
                                    .lowercase()


                            listOf(
                                "sport",
                                "fitness",
                                "football",
                                "soccer",
                                "basketball",
                                "tennis",
                                "baseball",
                                "스포츠",
                                "운동",
                                "축구",
                                "농구",
                                "테니스",
                                "야구",
                                "体育",
                                "运动",
                                "足球",
                                "篮球",
                                "网球"
                            ).any { word ->

                                skills.contains(
                                    word.lowercase()
                                )
                            }
                        }
                    }


                    // 기타
                    else -> searchedUsers
                }
            }
        }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundGalaxy()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = { BrandingSection(title = strings.title, subtitle = "Connect Souls", titleSize = 20) },
                    actions = {
                        LanguageSelector(
                            currentLanguage = language,
                            expanded = languageMenuExpanded,
                            onExpandedChange = { languageMenuExpanded = it },
                            onLanguageSelected = { language = it }
                        )
                    }
                )
            },
            bottomBar = {
                SoulNavigationBar(strings, onNavigateToHome, onNavigateToMatches, onNavigateToChat, onNavigateToProfile)
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                // Search Bar with improved design
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    SoulTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = strings.searchPlaceholder
                    )
                }

                // Categories Row
                LazyRow(
                    modifier = Modifier.padding(vertical = 12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(strings.categories.size) { index ->
                        FilterChip(
                            selected = selectedCategory == index,
                            onClick = { selectedCategory = index },
                            label = { Text(strings.categories[index], fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White.copy(alpha = 0.05f),
                                labelColor = Color.White.copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, if(selectedCategory == index) Color.Transparent else Color.White.copy(alpha = 0.12f))
                        )
                    }
                }

                CosmicWishRow(onWishClick = onUserClick)

                // High-End User List
                Box(modifier = Modifier.weight(1f)) {
                    if (matchState is MatchUiState.Loading && displayUsers.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF00D0D9))
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp)
                        ) {
                            items(displayUsers) { user ->
                                UserExploreCard(user, strings, onUserClick)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SoulNavigationBar(strings: ExploreStrings, onHome: () -> Unit, onMatches: () -> Unit, onChat: () -> Unit, onProfile: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(80.dp)
        ) {
            val navItems = listOf(Triple(Icons.Default.Home, strings.home, false), Triple(Icons.Default.Search, strings.explore, true), Triple(Icons.Default.Favorite, strings.matches, false), Triple(Icons.AutoMirrored.Filled.Chat, strings.chat, false), Triple(Icons.Default.Person, strings.profile, false))
            navItems.forEach { (icon, label, selected) ->
                NavigationBarItem(selected = selected, onClick = { if(!selected) when(label) { strings.home -> onHome(); strings.matches -> onMatches(); strings.chat -> onChat(); strings.profile -> onProfile() } }, icon = { Icon(icon, null, modifier = Modifier.size(26.dp)) }, label = { Text(label, fontSize = 11.sp, fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary, selectedTextColor = MaterialTheme.colorScheme.primary, unselectedIconColor = Color.White.copy(alpha = 0.5f), unselectedTextColor = Color.White.copy(alpha = 0.5f), indicatorColor = Color.Transparent))
            }
        }
    }
}

@Composable
private fun UserExploreCard(user: User, strings: ExploreStrings, onUserClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(30.dp))
            .clickable { onUserClick(user.id) }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar with border and online status
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(modifier = Modifier.size(68.dp).clip(CircleShape).background(Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2)))), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, modifier = Modifier.size(38.dp), tint = Color.White)
                    }
                    if (user.isOnline) {
                        Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(0xFF4CAF50)).border(2.dp, Color(0xFF1C1F26), CircleShape))
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Color.White)
                        Surface(color = Color(0xFF00D0D9).copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp)) {
                            Text("${user.matchRate}% Soul Match", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, color = Color(0xFF00D0D9), fontWeight = FontWeight.Black)
                        }
                    }
                    Text(user.bio, fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f), maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Professional Skills Presentation
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                user.teachSkills.take(3).forEach { skill ->
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF00D0D9).copy(alpha = 0.08f)).border(1.dp, Color(0xFF00D0D9).copy(alpha = 0.15f), RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text(skill, color = Color(0xFF00D0D9), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
                if (user.teachSkills.size > 3) {
                    Box(modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.06f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("+${user.teachSkills.size - 3}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // View Profile indicator
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                Text(text = strings.viewProfile, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ChevronRight, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
            }
        }
    }
}

private data class ExploreStrings(
    val title: String, val searchPlaceholder: String, val categories: List<String>, val viewProfile: String,
    val learnPrefix: String, val matchRate: String, val home: String, val explore: String, val matches: String,
    val chat: String, val profile: String
)

data class WishStar(val id: String, val author: String, val text: String, val color: Color)

@Composable
fun CosmicWishRow(onWishClick: (String) -> Unit) {
    val wishes = remember {
        listOf(
            WishStar("sarah", "Sarah", "🚀 오늘 저녁 Android Compose 레이아웃을 도와주실 전문가를 찾습니다!", Color(0xFF00D0D9)),
            WishStar("alex", "Alex", "🎸 기타 연주와 Python 크롤링 기술 교환하실 분 계신가요?", Color(0xFF7E57C2)),
            WishStar("victoria", "Victoria", "🎨 UX 디자인과 기초 한국어 회화 교류를 원하시는 분을 찾습니다.", Color(0xFFFF4081)),
            WishStar("chen", "Chen", "💻 알고리즘 문제 풀이 및 Spring Boot 아키텍처 학습 같이 해요!", Color(0xFF4CAF50))
        )
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(
            text = "🌌 우주 소망 광장 (Cosmic Wishes)",
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            items(wishes) { wish ->
                Box(
                    modifier = Modifier
                        .width(220.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, wish.color.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .clickable { onWishClick(wish.id) }
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(wish.color))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(wish.author, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = wish.text,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            maxLines = 2,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}


