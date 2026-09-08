package com.example.soul_android.ui.screens

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
import com.example.soul_android.data.DummyData
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.models.User
import com.example.soul_android.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onUserClick: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToMatches: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableIntStateOf(0) }

    val strings = when (language) {
        AppLanguage.KOREAN -> ExploreStrings("탐색", "스킬 또는 사용자 검색", listOf("전체", "언어", "프로그래밍", "음악", "디자인", "스포츠", "기타"), "프로필 보기", "배우고 싶은 스킬: ", "매칭률 ", "홈", "탐색", "매칭", "채팅", "프로필")
        AppLanguage.ENGLISH -> ExploreStrings("Explore", "Search skills or users", listOf("All", "Languages", "Programming", "Music", "Design", "Sports", "Other"), "View Profile", "Wants to learn: ", "Match ", "Home", "Explore", "Matches", "Chat", "Profile")
        AppLanguage.CHINESE -> ExploreStrings("探索", "搜索技能或用户", listOf("全部", "语言", "编程", "音乐", "设计", "运动", "其他"), "查看资料", "想学：", "匹配率 ", "首页", "探索", "匹配", "聊天", "个人资料")
    }

    val dummyUsers = DummyData.users.filter { 
        it.name.contains(searchQuery, ignoreCase = true) || it.teachSkills.any { s -> s.contains(searchQuery, ignoreCase = true) }
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
                // Search Bar
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    SoulTextField(value = searchQuery, onValueChange = { searchQuery = it }, placeholder = strings.searchPlaceholder)
                }

                // Categories
                LazyRow(modifier = Modifier.padding(vertical = 12.dp), contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(strings.categories.size) { index ->
                        FilterChip(
                            selected = selectedCategory == index,
                            onClick = { selectedCategory = index },
                            label = { Text(strings.categories[index]) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White.copy(alpha = 0.05f),
                                labelColor = Color.White.copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, if(selectedCategory == index) Color.Transparent else Color.White.copy(alpha = 0.1f))
                        )
                    }
                }

                // User List
                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                    items(dummyUsers) { user ->
                        UserExploreCard(user, strings, onUserClick)
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
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.06f)).border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            .clickable { onUserClick(user.id) }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2)))), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(36.dp), tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
                    Surface(color = Color(0xFF00D0D9).copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                        Text("${user.matchRate}%", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 11.sp, color = Color(0xFF00D0D9), fontWeight = FontWeight.Bold)
                    }
                }
                Text(user.bio, fontSize = 13.sp, color = Color.White.copy(alpha = 0.5f), maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text(user.teachSkills.joinToString(", "), fontSize = 13.sp, color = Color(0xFF00D0D9), fontWeight = FontWeight.Medium)
                
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = strings.viewProfile, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

private data class ExploreStrings(
    val title: String, val searchPlaceholder: String, val categories: List<String>, val viewProfile: String,
    val learnPrefix: String, val matchRate: String, val home: String, val explore: String, val matches: String,
    val chat: String, val profile: String
)
