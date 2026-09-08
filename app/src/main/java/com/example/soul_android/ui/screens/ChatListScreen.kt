package com.example.soul_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.soul_android.ui.components.SoulGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onChatClick: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToExplore: () -> Unit = {},
    onNavigateToMatches: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    val strings = when (language) {
        AppLanguage.KOREAN -> ChatListStrings(
            title = "채팅",
            home = "홈",
            explore = "탐색",
            matches = "매칭",
            chat = "채팅",
            profile = "프로필",
            lastMsgPrefix = "마지막 메시지: "
        )
        AppLanguage.ENGLISH -> ChatListStrings(
            title = "Chat",
            home = "Home",
            explore = "Explore",
            matches = "Matches",
            chat = "Chat",
            profile = "Profile",
            lastMsgPrefix = "Last message: "
        )
        AppLanguage.CHINESE -> ChatListStrings(
            title = "聊天",
            home = "首页",
            explore = "探索",
            matches = "匹配",
            chat = "聊天",
            profile = "个人资料",
            lastMsgPrefix = "最后一条消息: "
        )
    }

    val activeChats = DummyData.users.take(3) // Mock active chats

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.title, fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = { languageMenuExpanded = true }) {
                        val langText = when (language) {
                            AppLanguage.KOREAN -> "한국어"
                            AppLanguage.ENGLISH -> "English"
                            AppLanguage.CHINESE -> "中文"
                        }
                        Text("🌐 $langText", color = SoulGreen)
                    }
                    DropdownMenu(
                        expanded = languageMenuExpanded,
                        onDismissRequest = { languageMenuExpanded = false }
                    ) {
                        DropdownMenuItem(text = { Text("한국어") }, onClick = { language = AppLanguage.KOREAN; languageMenuExpanded = false })
                        DropdownMenuItem(text = { Text("English") }, onClick = { language = AppLanguage.ENGLISH; languageMenuExpanded = false })
                        DropdownMenuItem(text = { Text("中文") }, onClick = { language = AppLanguage.CHINESE; languageMenuExpanded = false })
                    }
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
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null) },
                    label = { Text(strings.chat) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = SoulGreen, selectedTextColor = SoulGreen, indicatorColor = Color.Transparent)
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(activeChats) { user ->
                ChatListItem(user, strings.lastMsgPrefix, onChatClick)
            }
        }
    }
}

@Composable
private fun ChatListItem(user: User, prefix: String, onChatClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChatClick(user.id) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.White)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "12:30 PM", fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                text = "${prefix}안녕하세요! Python...",
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}

private data class ChatListStrings(
    val title: String,
    val home: String,
    val explore: String,
    val matches: String,
    val chat: String,
    val profile: String,
    val lastMsgPrefix: String
)
