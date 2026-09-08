package com.example.soul_android.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
fun MatchesScreen(
    onUserClick: (String) -> Unit = {},
    onChatClick: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToExplore: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val strings = when (language) {
        AppLanguage.KOREAN -> MatchesStrings("매칭", "받은 요청", "내 매칭", "수락", "거절", "채팅하기", "가르칠 수 있는 스킬: ", "배우고 싶은 스킬: ", "매칭률: ", "홈", "탐색", "매칭", "채팅", "프로필", "수락됨", "거절됨")
        AppLanguage.ENGLISH -> MatchesStrings("Matches", "Received", "My Matches", "Accept", "Reject", "Chat", "Can teach: ", "Wants to learn: ", "Match: ", "Home", "Explore", "Matches", "Chat", "Profile", "Accepted", "Rejected")
        AppLanguage.CHINESE -> MatchesStrings("匹配", "收到的请求", "我的匹配", "接受", "拒绝", "聊天", "可以教授的技能: ", "想学习的技能: ", "匹配率: ", "首页", "探索", "匹配", "聊天", "个人资料", "已接受", "已拒绝")
    }

    var receivedRequests by remember { mutableStateOf(DummyData.users.take(2)) }
    var myMatches by remember { mutableStateOf(DummyData.users.drop(2).take(2)) }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundGalaxy()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = { BrandingSection(title = strings.title, subtitle = "Your Connections", titleSize = 20) },
                    actions = {
                        LanguageSelector(currentLanguage = language, expanded = languageMenuExpanded, onExpandedChange = { languageMenuExpanded = it }, onLanguageSelected = { language = it })
                    }
                )
            },
            bottomBar = {
                SoulNavigationBar(strings, onNavigateToHome, onNavigateToExplore, onNavigateToChat, onNavigateToProfile)
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {},
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]), color = MaterialTheme.colorScheme.primary)
                    }
                ) {
                    Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }, text = { Text(strings.receivedTab, color = if(selectedTabIndex == 0) Color.White else Color.White.copy(alpha = 0.5f)) })
                    Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }, text = { Text(strings.myMatchesTab, color = if(selectedTabIndex == 1) Color.White else Color.White.copy(alpha = 0.5f)) })
                }

                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (selectedTabIndex == 0) {
                        items(receivedRequests) { user ->
                            ReceivedRequestCard(user, strings, onUserClick, onAccept = {
                                receivedRequests = receivedRequests.filter { it.id != user.id }
                                myMatches = myMatches + user
                            }, onReject = { receivedRequests = receivedRequests.filter { it.id != user.id } })
                        }
                    } else {
                        items(myMatches) { user ->
                            MyMatchCard(user, strings, onUserClick, onChatClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SoulNavigationBar(strings: MatchesStrings, onHome: () -> Unit, onExplore: () -> Unit, onChat: () -> Unit, onProfile: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp, modifier = Modifier.height(80.dp)) {
            val navItems = listOf(Triple(Icons.Default.Home, strings.home, false), Triple(Icons.Default.Search, strings.explore, false), Triple(Icons.Default.Favorite, strings.matches, true), Triple(Icons.AutoMirrored.Filled.Chat, strings.chatLabel, false), Triple(Icons.Default.Person, strings.profile, false))
            navItems.forEach { (icon, label, selected) ->
                NavigationBarItem(selected = selected, onClick = { if(!selected) when(label) { strings.home -> onHome(); strings.explore -> onExplore(); strings.chatLabel -> onChat(); strings.profile -> onProfile() } }, icon = { Icon(icon, null, modifier = Modifier.size(26.dp)) }, label = { Text(label, fontSize = 11.sp, fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary, selectedTextColor = MaterialTheme.colorScheme.primary, unselectedIconColor = Color.White.copy(alpha = 0.5f), unselectedTextColor = Color.White.copy(alpha = 0.5f), indicatorColor = Color.Transparent))
            }
        }
    }
}

@Composable
private fun ReceivedRequestCard(user: User, strings: MatchesStrings, onUserClick: (String) -> Unit, onAccept: () -> Unit, onReject: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Color.White.copy(alpha = 0.06f)).border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(28.dp)).clickable { onUserClick(user.id) }.padding(16.dp)) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2)))), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(28.dp), tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Text("${user.matchRate}% Match", color = Color(0xFF00D0D9), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onReject, modifier = Modifier.weight(1f).height(44.dp), shape = RoundedCornerShape(22.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f), contentColor = Color.White.copy(alpha = 0.6f))) {
                    Text(strings.reject, fontSize = 13.sp)
                }
                Button(onClick = onAccept, modifier = Modifier.weight(1.2f).height(44.dp), shape = RoundedCornerShape(22.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                    Text(strings.accept, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MyMatchCard(user: User, strings: MatchesStrings, onUserClick: (String) -> Unit, onChatClick: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.White.copy(alpha = 0.06f)).border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)).clickable { onUserClick(user.id) }.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2)))), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(32.dp), tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
                Text(user.teachSkills.firstOrNull() ?: "", color = Color(0xFF00D0D9), fontSize = 13.sp)
            }
            IconButton(onClick = { onChatClick(user.id) }, modifier = Modifier.clip(CircleShape).background(Color(0xFF00D0D9).copy(alpha = 0.15f))) {
                Icon(Icons.AutoMirrored.Filled.Chat, null, tint = Color(0xFF00D0D9))
            }
        }
    }
}

private data class MatchesStrings(
    val title: String, val receivedTab: String, val myMatchesTab: String, val accept: String, val reject: String,
    val chat: String, val teachPrefix: String, val learnPrefix: String, val matchRate: String, val home: String,
    val explore: String, val matches: String, val chatLabel: String, val profile: String, val acceptedMsg: String, val rejectedMsg: String
)
