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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
        AppLanguage.KOREAN -> MatchesStrings("매칭", "받은 요청", "내 매칭", "수락", "거절", "채팅하기", "가르칠 수 있는 스킬: ", "배우고 싶은 스킬: ", "매칭률: ", "홈", "탐색", "매칭", "채팅", "프로필", "요청을 수락했습니다.", "요청을 거절했습니다.")
        AppLanguage.ENGLISH -> MatchesStrings("Matches", "Received", "My Matches", "Accept", "Reject", "Chat", "Can teach: ", "Wants to learn: ", "Match: ", "Home", "Explore", "Matches", "Chat", "Profile", "Request accepted.", "Request rejected.")
        AppLanguage.CHINESE -> MatchesStrings("匹配", "收到的请求", "我的匹配", "接受", "拒绝", "聊天", "可以教授的技能: ", "想学习的技能: ", "匹配率: ", "首页", "探索", "匹配", "聊天", "个人资料", "已接受请求。", "已拒绝请求。")
    }

    var receivedRequests by remember { mutableStateOf(DummyData.users.take(2)) }
    var myMatches by remember { mutableStateOf(DummyData.users.drop(2).take(1)) }

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
                NavigationBarItem(selected = false, onClick = onNavigateToHome, icon = { Icon(Icons.Default.Home, contentDescription = null) }, label = { Text(strings.home) })
                NavigationBarItem(selected = false, onClick = onNavigateToExplore, icon = { Icon(Icons.Default.Search, contentDescription = null) }, label = { Text(strings.explore) })
                NavigationBarItem(selected = true, onClick = { }, icon = { Icon(Icons.Default.Favorite, contentDescription = null) }, label = { Text(strings.matches) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = SoulGreen, selectedTextColor = SoulGreen, indicatorColor = Color.Transparent))
                NavigationBarItem(selected = false, onClick = onNavigateToChat, icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null) }, label = { Text(strings.chatLabel) })
                NavigationBarItem(selected = false, onClick = onNavigateToProfile, icon = { Icon(Icons.Default.Person, contentDescription = null) }, label = { Text(strings.profile) })
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.White, contentColor = SoulGreen, indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]), color = SoulGreen)
            }) {
                Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }, text = { Text(strings.receivedTab) })
                Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }, text = { Text(strings.myMatchesTab) })
            }

            if (selectedTabIndex == 0) {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(receivedRequests) { user ->
                        ReceivedRequestCardComponent(user, strings, onUserClick, onAccept = {
                            receivedRequests = receivedRequests.filter { it.id != user.id }
                            myMatches = myMatches + user
                        }, onReject = {
                            receivedRequests = receivedRequests.filter { it.id != user.id }
                        })
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(myMatches) { user ->
                        MyMatchCardComponent(user, strings, onUserClick, onChatClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceivedRequestCardComponent(user: User, strings: MatchesStrings, onUserClick: (String) -> Unit, onAccept: () -> Unit, onReject: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onUserClick(user.id) }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(30.dp), tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "${strings.matchRate}${user.matchRate}%", color = SoulGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "${strings.teachPrefix}${user.teachSkills.joinToString(", ")}", fontSize = 13.sp, color = Color.DarkGray)
            Text(text = "${strings.learnPrefix}${user.learnSkills.joinToString(", ")}", fontSize = 13.sp, color = Color.DarkGray)
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onReject, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)) {
                    Text(text = strings.reject, color = Color.Gray)
                }
                Button(onClick = onAccept, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = SoulGreen)) {
                    Text(text = strings.accept)
                }
            }
        }
    }
}

@Composable
private fun MyMatchCardComponent(user: User, strings: MatchesStrings, onUserClick: (String) -> Unit, onChatClick: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onUserClick(user.id) }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(36.dp), tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(text = user.teachSkills.joinToString(", "), color = SoulGreen, fontSize = 13.sp)
                Text(text = "${strings.matchRate}${user.matchRate}%", color = Color.Gray, fontSize = 12.sp)
            }
            IconButton(onClick = { onChatClick(user.id) }, colors = IconButtonDefaults.iconButtonColors(containerColor = SoulGreen.copy(alpha = 0.1f))) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = SoulGreen)
            }
        }
    }
}

private data class MatchesStrings(
    val title: String,
    val receivedTab: String,
    val myMatchesTab: String,
    val accept: String,
    val reject: String,
    val chat: String,
    val teachPrefix: String,
    val learnPrefix: String,
    val matchRate: String,
    val home: String,
    val explore: String,
    val matches: String,
    val chatLabel: String,
    val profile: String,
    val acceptedMsg: String,
    val rejectedMsg: String
)
