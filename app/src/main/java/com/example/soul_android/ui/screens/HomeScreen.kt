package com.example.soul_android.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.R
import com.example.soul_android.data.DummyData
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.models.User
import com.example.soul_android.ui.components.LanguageSelector
import com.example.soul_android.ui.components.SoulGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToExplore: () -> Unit = {},
    onNavigateToMatches: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onUserClick: (String) -> Unit = {}
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    val user = DummyData.currentUser

    val strings = when (language) {
        AppLanguage.KOREAN -> HomeStrings("안녕하세요, ${user.name}님!", "오늘의 추천 매칭입니다.", "추천 매칭", "매칭률", "홈", "탐색", "매칭", "채팅", "프로필")
        AppLanguage.ENGLISH -> HomeStrings("Hello, ${user.name}!", "Today's recommended matches.", "Recommended Matches", "Match Rate", "Home", "Explore", "Matches", "Chat", "Profile")
        AppLanguage.CHINESE -> HomeStrings("你好，${user.name}！", "今日推荐匹配。", "推荐匹配", "匹配率", "首页", "发现", "匹配", "聊天", "个人资料")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.soul_logo),
                        contentDescription = "SOUL",
                        modifier = Modifier.height(30.dp)
                    )
                },
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
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(strings.home) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = SoulGreen, selectedTextColor = SoulGreen, indicatorColor = Color.Transparent)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToExplore,
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text(strings.explore) }
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = strings.welcome, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = strings.subtitle, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            }

            item {
                Text(text = strings.matchTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                RecommendedUsersRow(strings.matchRate, onUserClick)
            }

            item {
                Text(text = if (language == AppLanguage.KOREAN) "나의 학습 현황" else "My Learning Status", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                LearningStatusCard()
            }
            
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun RecommendedUsersRow(matchRateLabel: String, onUserClick: (String) -> Unit) {
    val dummyUsers = DummyData.users

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(end = 20.dp)
    ) {
        items(dummyUsers) { user ->
            UserMatchCard(user, matchRateLabel, onUserClick)
        }
    }
}

@Composable
fun UserMatchCard(user: User, matchRateLabel: String, onUserClick: (String) -> Unit) {
    Card(
        modifier = Modifier.width(160.dp).clickable { onUserClick(user.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = user.teachSkills.joinToString(", "), fontSize = 12.sp, color = SoulGreen, maxLines = 1)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(color = SoulGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                Text(text = "$matchRateLabel ${user.matchRate}%", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, color = SoulGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LearningStatusCard() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SoulGreen)) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Python Programming", color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = "Sarah님과 매칭 중", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
            CircularProgressIndicator(progress = { 0.7f }, color = Color.White, trackColor = Color.White.copy(alpha = 0.3f))
        }
    }
}

private data class HomeStrings(
    val welcome: String,
    val subtitle: String,
    val matchTitle: String,
    val matchRate: String,
    val home: String,
    val explore: String,
    val matches: String,
    val chat: String,
    val profile: String
)
