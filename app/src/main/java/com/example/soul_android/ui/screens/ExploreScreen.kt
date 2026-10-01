package com.example.soul_android.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soul_android.data.DummyData
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.models.User
import com.example.soul_android.ui.components.*
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

    // 监听搜索词和分类的变化，实时向后端发起请求
    LaunchedEffect(searchQuery, selectedCategory) {
        val categoryFilter = if (selectedCategory == 0) null else strings.categories[selectedCategory]
        val effectiveSearch = searchQuery.ifBlank { null }
        // 我们以“所搜即所求”为逻辑，搜索词作为 haveSkill 或 wantSkill 传入
        matchViewModel.findMatches(have = effectiveSearch ?: categoryFilter)
    }

    val displayUsers = remember(matchState, selectedCategory, searchQuery) {
        if (matchState is MatchUiState.Success && (matchState as MatchUiState.Success).users.isNotEmpty()) {
            (matchState as MatchUiState.Success).users.map { resp ->
                User(
                    id = resp.username,
                    name = resp.name,
                    bio = resp.nationality ?: "",
                    languages = listOf(),
                    teachSkills = resp.skillOffer?.split(",")?.map { it.trim() } ?: listOf(),
                    learnSkills = resp.skillWant?.split(",")?.map { it.trim() } ?: listOf(),
                    matchRate = (resp.averageRating?.times(20) ?: 80.0).toInt(),
                    isOnline = true
                )
            }
        } else {
            val category = strings.categories.getOrNull(selectedCategory) ?: "전체"
            DummyData.users.filter { user ->
                val matchesSearch = searchQuery.isBlank() || 
                    user.name.contains(searchQuery, ignoreCase = true) || 
                    user.teachSkills.any { s -> s.contains(searchQuery, ignoreCase = true) } ||
                    user.learnSkills.any { s -> s.contains(searchQuery, ignoreCase = true) }

                val matchesCategory = category == "전체" || when (category) {
                    "언어" -> user.teachSkills.any { s -> s.contains("영어", true) || s.contains("중국어", true) || s.contains("한국어", true) } || user.languages.isNotEmpty()
                    "프로그래밍" -> user.teachSkills.any { s -> s.contains("Java", true) || s.contains("Python", true) || s.contains("Spring", true) || s.contains("Unity", true) }
                    "음악" -> user.teachSkills.any { s -> s.contains("Dance", true) || s.contains("Music", true) }
                    "디자인" -> user.teachSkills.any { s -> s.contains("Design", true) || s.contains("디자인", true) || s.contains("Photo", true) }
                    "스포츠" -> user.teachSkills.any { s -> s.contains("Gym", true) || s.contains("체육", true) }
                    else -> true
                }

                matchesSearch && matchesCategory
            }.ifEmpty { DummyData.users }
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


