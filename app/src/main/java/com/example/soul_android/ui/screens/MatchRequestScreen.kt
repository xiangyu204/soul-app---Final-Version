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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchRequestScreen(
    userId: String,
    onBackClick: () -> Unit = {},
    onSendRequestClick: () -> Unit = {}
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val strings = when (language) {
        AppLanguage.KOREAN -> MatchRequestStrings("매칭 요청", "함께 교환할 스킬", "요청 메시지", "요청 메시지를 입력하세요", "매칭 요청 보내기", "요청 완료", "매칭 요청을 성공적으로 보냈습니다.")
        AppLanguage.ENGLISH -> MatchRequestStrings("Match Request", "Matching Skills", "Request Message", "Type a message to the user", "Send Match Request", "Sent", "Match request sent successfully.")
        AppLanguage.CHINESE -> MatchRequestStrings("匹配请求", "匹配技能", "请求消息", "输入给对方的消息", "发送匹配请求", "已发送", "匹配请求已成功发送。")
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
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(24.dp)) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(36.dp), tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = user.teachSkills.joinToString(", "), color = SoulGreen, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(text = strings.matchingSkills, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                SkillBadgeComponent(user.teachSkills.firstOrNull() ?: "")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Person, contentDescription = null, tint = SoulGreen, modifier = Modifier.size(20.dp).align(Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(8.dp))
                SkillBadgeComponent(user.learnSkills.firstOrNull() ?: "")
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(text = strings.messageLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                placeholder = { Text(strings.messageHint, fontSize = 14.sp) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoulGreen, unfocusedBorderColor = Color.LightGray)
            )

            Spacer(modifier = Modifier.height(48.dp))
            SoulButton(text = strings.sendBtn, onClick = onSendRequestClick)
        }
    }
}

@Composable
private fun SkillBadgeComponent(skill: String) {
    Surface(color = Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp)) {
        Text(text = skill, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

private data class MatchRequestStrings(
    val title: String,
    val matchingSkills: String,
    val messageLabel: String,
    val messageHint: String,
    val sendBtn: String,
    val successTitle: String,
    val successMsg: String
)
