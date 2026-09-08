package com.example.soul_android.ui.screens

import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.data.DummyData
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.components.*

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
        AppLanguage.KOREAN -> ProfileStrings("프로필", "프로필 수정", "설정", "로그아웃", "가르칠 수 있는 스킬", "배우고 싶은 스킬", "홈", "탐색", "매칭", "채팅", "프로필")
        AppLanguage.ENGLISH -> ProfileStrings("Profile", "Edit Profile", "Settings", "Logout", "Skills I Can Teach", "Skills I Want to Learn", "Home", "Explore", "Matches", "Chat", "Profile")
        AppLanguage.CHINESE -> ProfileStrings("个人资料", "编辑资料", "设置", "登出", "我可以教授的技能", "我想学习的技能", "首页", "探索", "匹配", "聊天", "个人资料")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundGalaxy()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = { BrandingSection(title = strings.title, subtitle = "My Universe", titleSize = 20) },
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, "Settings", tint = Color.White)
                        }
                        LanguageSelector(currentLanguage = language, expanded = languageMenuExpanded, onExpandedChange = { languageMenuExpanded = it }, onLanguageSelected = { language = it })
                    }
                )
            },
            bottomBar = {
                SoulNavigationBar(strings, onNavigateToHome, onNavigateToExplore, onNavigateToMatches, onNavigateToChat)
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
                        .background(Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2))))
                        .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = Color.White)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = user.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = user.bio, fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f), modifier = Modifier.padding(top = 4.dp))

                Spacer(modifier = Modifier.height(32.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = onEditProfileClick,
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Text(strings.edit, color = Color.White)
                    }
                    Button(
                        onClick = onLogoutClick,
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.2f)),
                        border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.3f))
                    ) {
                        Text(strings.logout, color = Color(0xFFFF5252))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Skills
                MyProfileSkillSection(strings.teach, user.teachSkills)
                MyProfileSkillSection(strings.learn, user.learnSkills)
                
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun SoulNavigationBar(strings: ProfileStrings, onHome: () -> Unit, onExplore: () -> Unit, onMatches: () -> Unit, onChat: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp, modifier = Modifier.height(80.dp)) {
            val navItems = listOf(Triple(Icons.Default.Home, strings.home, false), Triple(Icons.Default.Search, strings.explore, false), Triple(Icons.Default.Favorite, strings.matches, false), Triple(Icons.AutoMirrored.Filled.Chat, strings.chat, false), Triple(Icons.Default.Person, strings.profile, true))
            navItems.forEach { (icon, label, selected) ->
                NavigationBarItem(selected = selected, onClick = { if(!selected) when(label) { strings.home -> onHome(); strings.explore -> onExplore(); strings.matches -> onMatches(); strings.chat -> onChat() } }, icon = { Icon(icon, null, modifier = Modifier.size(26.dp)) }, label = { Text(label, fontSize = 11.sp, fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary, selectedTextColor = MaterialTheme.colorScheme.primary, unselectedIconColor = Color.White.copy(alpha = 0.5f), unselectedTextColor = Color.White.copy(alpha = 0.5f), indicatorColor = Color.Transparent))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MyProfileSkillSection(title: String, skills: List<String>) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF00D0D9))
        Spacer(modifier = Modifier.height(16.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            skills.forEach { skill ->
                Surface(
                    color = Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Text(text = skill, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 14.sp, color = Color.White)
                }
            }
        }
    }
}

private data class ProfileStrings(
    val title: String, val edit: String, val settings: String, val logout: String, val teach: String,
    val learn: String, val home: String, val explore: String, val matches: String, val chat: String, val profile: String
)
