package com.example.soul_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import com.example.soul_android.ui.components.LanguageSelector
import com.example.soul_android.ui.components.SoulButton
import com.example.soul_android.ui.components.SoulGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UserProfileScreen(
    userId: String,
    onBackClick: () -> Unit = {},
    onMatchRequestClick: (String) -> Unit = {},
    onChatClick: (String) -> Unit = {}
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    val strings = when (language) {
        AppLanguage.KOREAN -> UserProfileStrings("프로필", "자기소개", "가르칠 수 있는 스킬", "배우고 싶은 스킬", "매칭률", "매칭 요청", "메시지 보내기", "구사 언어")
        AppLanguage.ENGLISH -> UserProfileStrings("Profile", "About Me", "Skills I Can Teach", "Skills I Want to Learn", "Match Rate", "Match Request", "Send Message", "Languages")
        AppLanguage.CHINESE -> UserProfileStrings("个人资料", "自我介绍", "我可以教授的技能", "我想学习的技能", "匹配率", "发送匹配请求", "发送消息", "语言")
    }

    val user = DummyData.users.find { it.id == userId } ?: DummyData.currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
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
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(Color(0xFFF0F0F0)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = user.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            
            Surface(color = SoulGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                Text(text = "${strings.matchRate} ${user.matchRate}%", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 14.sp, color = SoulGreen, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
            ProfileSectionComponent(strings.about, user.bio)
            ProfileSkillSectionComponent(strings.teach, user.teachSkills)
            ProfileSkillSectionComponent(strings.learn, user.learnSkills)

            Spacer(modifier = Modifier.height(48.dp))

            SoulButton(text = strings.requestBtn, onClick = { onMatchRequestClick(user.id) })
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { onChatClick(user.id) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoulGreen)
            ) {
                Text(text = strings.messageBtn, color = SoulGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ProfileSectionComponent(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SoulGreen)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = content, style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileSkillSectionComponent(title: String, skills: List<String>) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SoulGreen)
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            skills.forEach { skill ->
                Surface(color = Color(0xFFF0F0F0), shape = RoundedCornerShape(20.dp)) {
                    Text(text = skill, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 14.sp)
                }
            }
        }
    }
}

private data class UserProfileStrings(
    val title: String,
    val about: String,
    val teach: String,
    val learn: String,
    val matchRate: String,
    val requestBtn: String,
    val messageBtn: String,
    val languages: String
)
