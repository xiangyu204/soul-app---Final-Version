package com.example.soul_android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.components.SoulButton
import com.example.soul_android.ui.components.SoulGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    userId: String,
    onBackClick: () -> Unit = {}
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    
    var selectedSkill by remember { mutableStateOf("Python") }
    var selectedDate by remember { mutableStateOf("2026-09-15") }
    var startTime by remember { mutableStateOf("19:00") }
    var endTime by remember { mutableStateOf("20:00") }
    var notes by remember { mutableStateOf("") }

    val strings = when (language) {
        AppLanguage.KOREAN -> ScheduleStrings(
            title = "일정 잡기",
            skillLabel = "스킬",
            userLabel = "상대방",
            dateLabel = "날짜",
            timeLabel = "시간",
            notesLabel = "메모",
            confirm = "일정 확정",
            success = "일정이 확정되었습니다!"
        )
        AppLanguage.ENGLISH -> ScheduleStrings(
            title = "Schedule",
            skillLabel = "Skill",
            userLabel = "Partner",
            dateLabel = "Date",
            timeLabel = "Time",
            notesLabel = "Notes",
            confirm = "Confirm Schedule",
            success = "Schedule confirmed!"
        )
        AppLanguage.CHINESE -> ScheduleStrings(
            title = "安排日程",
            skillLabel = "技能",
            userLabel = "对方",
            dateLabel = "日期",
            timeLabel = "时间",
            notesLabel = "备注",
            confirm = "确认日程",
            success = "日程已确认！"
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = strings.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Skill
            InfoSection(strings.skillLabel, selectedSkill)
            
            // User
            InfoSection(strings.userLabel, userId)

            // Date Picker (Placeholder for now, just text field)
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { selectedDate = it },
                label = { Text(strings.dateLabel) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Time Selection
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Start") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("End") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(strings.notesLabel) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            SoulButton(
                text = strings.confirm,
                onClick = {
                    // Show success
                    // In real app, save to state/DB
                    onBackClick()
                }
            )
        }
    }
}

@Composable
private fun InfoSection(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = SoulGreen)
    }
}

private data class ScheduleStrings(
    val title: String,
    val skillLabel: String,
    val userLabel: String,
    val dateLabel: String,
    val timeLabel: String,
    val notesLabel: String,
    val confirm: String,
    val success: String
)
