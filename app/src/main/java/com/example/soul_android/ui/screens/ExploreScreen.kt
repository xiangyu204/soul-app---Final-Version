package com.example.soul_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.data.DummyData
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.models.User
import com.example.soul_android.ui.components.LanguageSelector
import com.example.soul_android.ui.components.SoulGreen

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
    var selectedCategory by remember { mutableStateOf(0) }

    val strings = when (language) {
        AppLanguage.KOREAN -> ExploreStrings("탐색", "스킬 또는 사용자 검색", listOf("전체", "언어", "프로그래밍", "음악", "디자인", "스포츠", "기타"), "프로필 보기", "배우고 싶은 스킬: ", "매칭률 ", "홈", "탐색", "매칭", "채팅", "프로필")
        AppLanguage.ENGLISH -> ExploreStrings("Explore", "Search skills or users", listOf("All", "Languages", "Programming", "Music", "Design", "Sports", "Other"), "View Profile", "Wants to learn: ", "Match ", "Home", "Explore", "Matches", "Chat", "Profile")
        AppLanguage.CHINESE -> ExploreStrings("探索", "搜索技能或用户", listOf("全部", "语言", "编程", "音乐", "设计", "运动", "其他"), "查看资料", "想学：", "匹配率 ", "首页", "探索", "匹配", "聊天", "个人资料")
    }

    val dummyUsers = DummyData.users.filter { 
        it.name.contains(searchQuery, ignoreCase = true) || it.teachSkills.any { s -> s.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.title, fontWeight = FontWeight.Bold) },
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
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHome,
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(strings.home) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text(strings.explore) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = SoulGreen, selectedTextColor = SoulGreen, indicatorColor = Color.Transparent)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToMatches,
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text(strings.matches) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToChat,
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null) },
                    label = { Text(strings.chat) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(strings.profile) }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                placeholder = { Text(strings.searchPlaceholder) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoulGreen, unfocusedBorderColor = Color.LightGray)
            )

            // Categories
            LazyRow(modifier = Modifier.padding(vertical = 12.dp), contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(strings.categories.size) { index ->
                    FilterChip(
                        selected = selectedCategory == index,
                        onClick = { selectedCategory = index },
                        label = { Text(strings.categories[index]) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SoulGreen, selectedLabelColor = Color.White),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            // User List
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
                items(dummyUsers) { user ->
                    UserExploreCard(user, strings, onUserClick)
                }
            }
        }
    }
}

@Composable
private fun UserExploreCard(user: User, strings: ExploreStrings, onUserClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onUserClick(user.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(70.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Surface(color = SoulGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                        Text(text = "${strings.matchRate}${user.matchRate}%", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, color = SoulGreen, fontWeight = FontWeight.Bold)
                    }
                }
                Text(text = user.bio, fontSize = 13.sp, color = Color.Gray, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = user.teachSkills.joinToString(", "), fontSize = 13.sp, color = SoulGreen, fontWeight = FontWeight.Medium)
                Text(text = "${strings.learnPrefix}${user.learnSkills.joinToString(", ")}", fontSize = 12.sp, color = Color.DarkGray)
                
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onUserClick(user.id) }, modifier = Modifier.align(Alignment.End), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = SoulGreen)) {
                    Text(text = strings.viewProfile, fontSize = 12.sp)
                }
            }
        }
    }
}

private data class ExploreStrings(
    val title: String,
    val searchPlaceholder: String,
    val categories: List<String>,
    val viewProfile: String,
    val learnPrefix: String,
    val matchRate: String,
    val home: String,
    val explore: String,
    val matches: String,
    val chat: String,
    val profile: String
)
