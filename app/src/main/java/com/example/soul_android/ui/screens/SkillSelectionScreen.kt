package com.example.soul_android.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.components.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SkillSelectionScreen(
    onBackClick: () -> Unit = {},
    onDoneClick: () -> Unit = {}
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    var showAddDialog by remember { mutableStateOf(false) }
    var customSkillName by remember { mutableStateOf("") }
    var addingToTeach by remember { mutableStateOf(true) }

    val baseSkillsKr = listOf("영어", "한국어", "중국어", "프로그래밍", "Python", "Java", "사진", "영상 편집", "음악", "기타")
    val baseSkillsEn = listOf("English", "Korean", "Chinese", "Programming", "Python", "Java", "Photography", "Video Editing", "Music", "Other")
    val baseSkillsCn = listOf("英语", "韩语", "中文", "编程", "Python", "Java", "摄影", "视频编辑", "音乐", "其他")

    var teachSkills by remember { mutableStateOf(baseSkillsKr) }
    var learnSkills by remember { mutableStateOf(baseSkillsKr) }

    var selectedTeachSkills by remember { mutableStateOf(setOf<String>()) }
    var selectedLearnSkills by remember { mutableStateOf(setOf<String>()) }

    val strings = when (language) {
        AppLanguage.KOREAN -> SkillStrings("스킬 선택", "가르칠 수 있는 스킬", "배우고 싶은 스킬", "스킬 추가", "완료", "새 스킬 추가", "스킬 이름을 입력하세요", "취소", "추가", baseSkillsKr)
        AppLanguage.ENGLISH -> SkillStrings("Select Skills", "Skills I Can Teach", "Skills I Want to Learn", "Add Skill", "Done", "Add New Skill", "Enter skill name", "Cancel", "Add", baseSkillsEn)
        AppLanguage.CHINESE -> SkillStrings("选择技能", "我可以教授的技能", "我想学习的技能", "添加技能", "完成", "添加新技能", "输入技能名称", "取消", "添加", baseSkillsCn)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundGalaxy()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = { BrandingSection(title = strings.title, subtitle = "Choose your planet skills", titleSize = 20) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                GlassPanel {
                    SkillSectionComponent(
                        title = strings.teachSection,
                        skills = if (language == AppLanguage.KOREAN) teachSkills else strings.skills,
                        selectedSkills = selectedTeachSkills,
                        onSkillSelected = { skill ->
                            selectedTeachSkills = if (selectedTeachSkills.contains(skill)) selectedTeachSkills - skill else selectedTeachSkills + skill
                        },
                        onAddClick = { addingToTeach = true; showAddDialog = true },
                        addText = strings.addSkill
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    SkillSectionComponent(
                        title = strings.learnSection,
                        skills = if (language == AppLanguage.KOREAN) learnSkills else strings.skills,
                        selectedSkills = selectedLearnSkills,
                        onSkillSelected = { skill ->
                            selectedLearnSkills = if (selectedLearnSkills.contains(skill)) selectedLearnSkills - skill else selectedLearnSkills + skill
                        },
                        onAddClick = { addingToTeach = false; showAddDialog = true },
                        addText = strings.addSkill
                    )

                    Spacer(modifier = Modifier.height(48.dp))
                    SoulButton(text = strings.done, onClick = onDoneClick)
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF1C1F26),
            titleContentColor = Color.White,
            title = { Text(strings.dialogTitle) },
            text = {
                SoulTextField(
                    value = customSkillName,
                    onValueChange = { customSkillName = it },
                    placeholder = strings.dialogHint
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (customSkillName.isNotBlank()) {
                        if (addingToTeach) {
                            teachSkills = teachSkills + customSkillName
                            selectedTeachSkills = selectedTeachSkills + customSkillName
                        } else {
                            learnSkills = learnSkills + customSkillName
                            selectedLearnSkills = selectedLearnSkills + customSkillName
                        }
                        customSkillName = ""
                        showAddDialog = false
                    }
                }) {
                    Text(strings.add, color = Color(0xFF00D0D9))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(strings.cancel, color = Color.White.copy(alpha = 0.5f))
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillSectionComponent(
    title: String,
    skills: List<String>,
    selectedSkills: Set<String>,
    onSkillSelected: (String) -> Unit,
    onAddClick: () -> Unit,
    addText: String
) {
    Column {
        Text(text = title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            skills.forEach { skill ->
                SkillChip(text = skill, selected = selectedSkills.contains(skill), onSelectedChange = { onSkillSelected(skill) })
            }
            AssistChip(
                onClick = onAddClick,
                label = { Text(addText, color = Color.White) },
                leadingIcon = { Icon(Icons.Default.Add, null, tint = Color(0xFF00D0D9), modifier = Modifier.size(18.dp)) },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                colors = AssistChipDefaults.assistChipColors(containerColor = Color.White.copy(alpha = 0.1f))
            )
        }
    }
}

private data class SkillStrings(
    val title: String, val teachSection: String, val learnSection: String, val addSkill: String,
    val done: String, val dialogTitle: String, val dialogHint: String, val cancel: String,
    val add: String, val skills: List<String>
)
