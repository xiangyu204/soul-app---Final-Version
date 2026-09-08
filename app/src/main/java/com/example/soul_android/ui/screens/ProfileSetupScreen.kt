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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.components.LanguageSelector
import com.example.soul_android.ui.components.SkillChip
import com.example.soul_android.ui.components.SoulButton
import com.example.soul_android.ui.components.SoulGreen

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
        AppLanguage.KOREAN -> ProfileSetupStrings(
            title = "프로필 설정",
            photo = "프로필 사진",
            name = "이름",
            bio = "자기소개",
            bioHint = "자신을 소개해 주세요",
            teachSkills = "가르칠 수 있는 스킬",
            learnSkills = "배우고 싶은 스킬",
            next = "다음",
            skills = availableSkills
        )
        AppLanguage.ENGLISH -> ProfileSetupStrings(
            title = "Profile Setup",
            photo = "Profile Photo",
            name = "Name",
            bio = "Bio",
            bioHint = "Tell us about yourself",
            teachSkills = "Skills I can teach",
            learnSkills = "Skills I want to learn",
            next = "Next",
            skills = availableSkillsEn
        )
        AppLanguage.CHINESE -> ProfileSetupStrings(
            title = "个人资料设置",
            photo = "个人头像",
            name = "姓名",
            bio = "自我介绍",
            bioHint = "请介绍一下你自己",
            teachSkills = "我可以教的技能",
            learnSkills = "我想学的技能",
            next = "下一步",
            skills = availableSkillsCn
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Language Selector
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            LanguageSelector(
                currentLanguage = language,
                expanded = languageMenuExpanded,
                onExpandedChange = { languageMenuExpanded = it },
                onLanguageSelected = { language = it }
            )
        }

        Text(
            text = strings.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Photo Placeholder
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F0F0))
                .border(1.dp, Color.LightGray, CircleShape)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = Color.Gray)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(SoulGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
            }
        }
        
        Text(
            text = strings.photo,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(strings.name) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bio
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text(strings.bio) },
            placeholder = { Text(strings.bioHint) },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Teach Skills
        Text(text = strings.teachSkills, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
        FlowRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            strings.skills.forEachIndexed { index, skill ->
                SkillChip(text = skill, selected = selectedTeachSkills.contains(index), onSelectedChange = {
                    selectedTeachSkills = if (it) selectedTeachSkills + index else selectedTeachSkills - index
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Learn Skills
        Text(text = strings.learnSkills, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
        FlowRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            strings.skills.forEachIndexed { index, skill ->
                SkillChip(text = skill, selected = selectedLearnSkills.contains(index), onSelectedChange = {
                    selectedLearnSkills = if (it) selectedLearnSkills + index else selectedLearnSkills - index
                })
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        SoulButton(text = strings.next, onClick = onNextClick)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

private data class ProfileSetupStrings(
    val title: String,
    val photo: String,
    val name: String,
    val bio: String,
    val bioHint: String,
    val teachSkills: String,
    val learnSkills: String,
    val next: String,
    val skills: List<String>
)
