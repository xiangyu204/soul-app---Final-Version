package com.example.soul_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.soul_android.ui.components.SoulGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToExplore: () -> Unit = {},
    onNavigateToMatches: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    val user = DummyData.currentUser

    val strings = when (language) {
        AppLanguage.KOREAN -> ProfileStrings(
            title = "프로필",
            edit = "프로필 수정",
            settings = "설정",
            logout = "로그아웃",
            teach = "가르칠 수 있는 스킬",
            learn = "배우고 싶은 스킬",
            home = "홈",
            explore = "탐색",
            matches = "매칭",
            chat = "채팅",
            profile = "프로필"
        )
        AppLanguage.ENGLISH -> ProfileStrings(
            title = "Profile",
            edit = "Edit Profile",
            settings = "Settings",
            logout = "Logout",
            teach = "Skills I Can Teach",
            learn = "Skills I Want to Learn",
            home = "Home",
            explore = "Explore",
            matches = "Matches",
            chat = "Chat",
            profile = "Profile"
        )
        AppLanguage.CHINESE -> ProfileStrings(
            title = "个人资料",
            edit = "编辑资料",
            settings = "设置",
            logout = "登出",
            teach = "我可以教授的技能",
            learn = "我想学习的技能",
            home = "首页",
            explore = "探索",
            matches = "匹配",
            chat = "聊天",
            profile = "个人资料"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.title, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
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
                    selected = false,
                    onClick = onNavigateToChat,
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null) },
                    label = { Text(strings.chat) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(strings.profile) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = SoulGreen, selectedTextColor = SoulGreen, indicatorColor = Color.Transparent)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = user.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = user.bio, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onEditProfileClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(strings.edit, color = SoulGreen)
                }
                Button(
                    onClick = onLogoutClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE7E6), contentColor = Color.Red)
                ) {
                    Text(strings.logout)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Skills
            MyProfileSkillSection(strings.teach, user.teachSkills)
            MyProfileSkillSection(strings.learn, user.learnSkills)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MyProfileSkillSection(title: String, skills: List<String>) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SoulGreen)
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            skills.forEach { skill ->
                Surface(
                    color = Color(0xFFF0F0F0),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = skill,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

private data class ProfileStrings(
    val title: String,
    val edit: String,
    val settings: String,
    val logout: String,
    val teach: String,
    val learn: String,
    val home: String,
    val explore: String,
    val matches: String,
    val chat: String,
    val profile: String
)
