package com.example.soul_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.components.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupScreen(
    onNextClick: () -> Unit = {}
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    
    val availableSkills = listOf("영어", "한국어", "중국어", "프로그래밍", "기타")
    val availableSkillsEn = listOf("English", "Korean", "Chinese", "Programming", "Etc")
    val availableSkillsCn = listOf("英语", "韩语", "中文", "编程", "其他")

    var selectedTeachSkills by remember { mutableStateOf(setOf<Int>()) }
    var selectedLearnSkills by remember { mutableStateOf(setOf<Int>()) }

    val strings = when (language) {
        AppLanguage.KOREAN -> ProfileSetupStrings("프로필 설정", "프로필 사진", "이름", "자기소개", "자신을 소개해 주세요", "가르칠 수 있는 스킬", "배우고 싶은 스킬", "다음", availableSkills)
        AppLanguage.ENGLISH -> ProfileSetupStrings("Profile Setup", "Profile Photo", "Name", "Bio", "Tell us about yourself", "Skills I can teach", "Skills I want to learn", "Next", availableSkillsEn)
        AppLanguage.CHINESE -> ProfileSetupStrings("个人资料设置", "个人头像", "姓名", "自我介绍", "请介绍一下你自己", "我可以教的技能", "我想学的技能", "下一步", availableSkillsCn)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundGalaxy()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                LanguageSelector(
                    currentLanguage = language,
                    expanded = languageMenuExpanded,
                    onExpandedChange = { languageMenuExpanded = it },
                    onLanguageSelected = { language = it }
                )
            }

            BrandingSection(title = strings.title, subtitle = "Tell souls about you", titleSize = 22)
            
            Spacer(modifier = Modifier.height(32.dp))

            // Profile Photo with Glow
            Box(contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(110.dp).blur(20.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape))
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(50.dp), tint = Color.White.copy(alpha = 0.5f))
                    Box(
                        modifier = Modifier.align(Alignment.BottomEnd).size(30.dp).clip(CircleShape)
                            .background(Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp), tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            GlassPanel {
                SoulTextField(value = name, onValueChange = { name = it }, placeholder = strings.name)
                Spacer(modifier = Modifier.height(16.dp))
                SoulTextField(value = bio, onValueChange = { bio = it }, placeholder = strings.bioHint, modifier = Modifier.height(100.dp))
                
                Spacer(modifier = Modifier.height(24.dp))

                SectionHeaderSmall(strings.teachSkills)
                FlowRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    strings.skills.forEachIndexed { index, skill ->
                        SkillChip(text = skill, selected = selectedTeachSkills.contains(index), onSelectedChange = {
                            selectedTeachSkills = if (it) selectedTeachSkills + index else selectedTeachSkills - index
                        })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SectionHeaderSmall(strings.learnSkills)
                FlowRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    strings.skills.forEachIndexed { index, skill ->
                        SkillChip(text = skill, selected = selectedLearnSkills.contains(index), onSelectedChange = {
                            selectedLearnSkills = if (it) selectedLearnSkills + index else selectedLearnSkills - index
                        })
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                SoulButton(text = strings.next, onClick = onNextClick)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionHeaderSmall(title: String) {
    Text(text = title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
}

private data class ProfileSetupStrings(
    val title: String, val photo: String, val name: String, val bio: String, val bioHint: String,
    val teachSkills: String, val learnSkills: String, val next: String, val skills: List<String>
)
