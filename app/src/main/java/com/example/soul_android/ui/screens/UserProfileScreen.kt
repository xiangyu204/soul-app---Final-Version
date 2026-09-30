package com.example.soul_android.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.data.DummyData
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.components.BackgroundGalaxy
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

    val user = DummyData.users.find { it.id == userId } ?: com.example.soul_android.models.User(
        id = userId,
        name = if (userId.length <= 2) userId else userId.replaceFirstChar { it.uppercase() },
        bio = "SOUL Planet Active Souler",
        languages = listOf("한국어", "English"),
        teachSkills = listOf("Language", "Tech"),
        learnSkills = listOf("Culture", "Design"),
        matchRate = 88,
        isOnline = true
    )

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundGalaxy()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = { Text(strings.title, fontWeight = FontWeight.ExtraBold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                // High-End Avatar
                Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2)))), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.White)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = user.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                
                Surface(color = Color(0xFF00D0D9).copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                    Text(text = "${strings.matchRate} ${user.matchRate}%", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 14.sp, color = Color(0xFF00D0D9), fontWeight = FontWeight.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                SkillRadarChart(matchRate = user.matchRate, currentLanguage = language)

                Spacer(modifier = Modifier.height(16.dp))
                
                // Content Cards using Glassmorphism panel styling
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.White.copy(alpha = 0.05f)).border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp)).padding(20.dp)) {
                    Column {
                        ProfileSectionComponent(strings.about, user.bio)
                        ProfileSkillSectionComponent(strings.teach, user.teachSkills, Color(0xFF00D0D9))
                        ProfileSkillSectionComponent(strings.learn, user.learnSkills, Color(0xFF7E57C2))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                SoulButton(text = strings.requestBtn, onClick = { onMatchRequestClick(user.id) })
                Spacer(modifier = Modifier.height(14.dp))
                
                Button(
                    onClick = { onChatClick(user.id) },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.06f), contentColor = Color(0xFF00D0D9)),
                    border = BorderStroke(1.2.dp, Color(0xFF00D0D9).copy(alpha = 0.4f))
                ) {
                    Text(text = strings.messageBtn, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                }
            }
        }
    }
}

@Composable
private fun ProfileSectionComponent(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00D0D9))
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = content, fontSize = 15.sp, color = Color.White.copy(alpha = 0.7f))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileSkillSectionComponent(title: String, skills: List<String>, themeColor: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = themeColor)
        Spacer(modifier = Modifier.height(10.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            skills.forEach { skill ->
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(themeColor.copy(alpha = 0.08f)).border(1.dp, themeColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text(text = skill, color = if (themeColor == Color(0xFF7E57C2)) Color(0xFFB39DDB) else themeColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
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

@Composable
fun SkillRadarChart(matchRate: Int, currentLanguage: com.example.soul_android.models.AppLanguage) {
    val stats = remember(matchRate) {
        listOf(
            (matchRate * 0.9f).coerceIn(40f, 100f),
            (matchRate * 1.0f).coerceIn(40f, 100f),
            (matchRate * 0.85f).coerceIn(40f, 100f),
            (matchRate * 0.95f).coerceIn(40f, 100f),
            (matchRate * 1.05f).coerceIn(40f, 100f)
        )
    }
    
    val labels = listOf("지식 출력", "흡수 속도", "활발한 공명", "안정성", "적합도")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val maxRadius = size.minDimension / 2.3f

            // 绘制网格圈 (3层圈)
            for (j in 1..3) {
                val radius = maxRadius * (j / 3f)
                val path = androidx.compose.ui.graphics.Path()
                for (i in 0..4) {
                    val angle = Math.toRadians((i * 72 - 90).toDouble())
                    val x = centerX + Math.cos(angle).toFloat() * radius
                    val y = centerY + Math.sin(angle).toFloat() * radius
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(path, Color.White.copy(alpha = 0.08f), style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))
            }

            // 绘制数据区域
            val statPath = androidx.compose.ui.graphics.Path()
            for (i in 0..4) {
                val angle = Math.toRadians((i * 72 - 90).toDouble())
                val radius = maxRadius * (stats[i] / 100f)
                val x = centerX + Math.cos(angle).toFloat() * radius
                val y = centerY + Math.sin(angle).toFloat() * radius
                if (i == 0) statPath.moveTo(x, y) else statPath.lineTo(x, y)
            }
            statPath.close()
            drawPath(statPath, Brush.radialGradient(listOf(Color(0xFF00D0D9).copy(alpha = 0.4f), Color(0xFF7E57C2).copy(alpha = 0.4f))), style = androidx.compose.ui.graphics.drawscope.Fill)
            drawPath(statPath, Color(0xFF00D0D9), style = androidx.compose.ui.graphics.drawscope.Stroke(1.5.dp.toPx()))
        }

        // 放置文本标签
        labels.forEachIndexed { i, label ->
            val angle = Math.toRadians((i * 72 - 90).toDouble())
            val offsetRadius = 65.dp
            val xOffset = (Math.cos(angle) * 85).toInt().dp
            val yOffset = (Math.sin(angle) * 65).toInt().dp
            
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(x = xOffset, y = yOffset)
            )
        }
    }
}

