package com.example.soul_android.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.components.SoulButton
import com.example.soul_android.ui.components.SoulGreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


// ============================================================
// Schedule Screen
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    userId: String,
    language: AppLanguage = AppLanguage.KOREAN,
    skill: String = "Python",
    onBackClick: () -> Unit = {},
    onScheduleConfirm: (ScheduleData) -> Unit = {}
) {

    val strings = getScheduleStrings(language)

    var selectedDate by rememberSaveable {
        mutableStateOf(getTodayDate())
    }

    var startTime by rememberSaveable {
        mutableStateOf("19:00")
    }

    var endTime by rememberSaveable {
        mutableStateOf("20:00")
    }

    var notes by rememberSaveable {
        mutableStateOf("")
    }

    var showDatePicker by rememberSaveable {
        mutableStateOf(false)
    }

    var showStartTimePicker by rememberSaveable {
        mutableStateOf(false)
    }

    var showEndTimePicker by rememberSaveable {
        mutableStateOf(false)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val coroutineScope = rememberCoroutineScope()


    // ========================================================
    // Date Picker
    // ========================================================

    if (showDatePicker) {

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dateStringToMillis(selectedDate)
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        datePickerState.selectedDateMillis?.let { millis ->

                            selectedDate = formatDate(millis)
                        }

                        showDatePicker = false
                    }
                ) {

                    Text(strings.ok)
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {

                    Text(strings.cancel)
                }
            }
        ) {

            DatePicker(
                state = datePickerState
            )
        }
    }


    // ========================================================
    // Start Time Picker
    // ========================================================

    if (showStartTimePicker) {

        val timePickerState = rememberTimePickerState(
            initialHour = getHour(startTime),
            initialMinute = getMinute(startTime),
            is24Hour = true
        )

        ScheduleTimePickerDialog(
            title = strings.startTime,
            state = timePickerState,
            confirmText = strings.ok,
            cancelText = strings.cancel,

            onConfirm = {

                startTime = formatTime(
                    timePickerState.hour,
                    timePickerState.minute
                )

                showStartTimePicker = false
            },

            onDismiss = {
                showStartTimePicker = false
            }
        )
    }


    // ========================================================
    // End Time Picker
    // ========================================================

    if (showEndTimePicker) {

        val timePickerState = rememberTimePickerState(
            initialHour = getHour(endTime),
            initialMinute = getMinute(endTime),
            is24Hour = true
        )

        ScheduleTimePickerDialog(
            title = strings.endTime,
            state = timePickerState,
            confirmText = strings.ok,
            cancelText = strings.cancel,

            onConfirm = {

                endTime = formatTime(
                    timePickerState.hour,
                    timePickerState.minute
                )

                showEndTimePicker = false
            },

            onDismiss = {
                showEndTimePicker = false
            }
        )
    }


    // ========================================================
    // Main UI
    // ========================================================

    Scaffold(

        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = strings.title,
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.back
                        )
                    }
                }
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 24.dp,
                    vertical = 20.dp
                ),

            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {


            // ====================================================
            // Skill
            // ====================================================

            InfoSection(
                label = strings.skillLabel,
                value = skill
            )


            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )


            // ====================================================
            // Partner
            // ====================================================

            InfoSection(
                label = strings.userLabel,
                value = userId
            )


            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )


            // ====================================================
            // Date
            // ====================================================

            PickerField(
                label = strings.dateLabel,
                value = selectedDate,
                onClick = {
                    showDatePicker = true
                }
            )


            // ====================================================
            // Time
            // ====================================================

            Text(
                text = strings.timeLabel,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {


                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    PickerField(
                        label = strings.startTime,
                        value = startTime,

                        onClick = {
                            showStartTimePicker = true
                        }
                    )
                }


                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    PickerField(
                        label = strings.endTime,
                        value = endTime,

                        onClick = {
                            showEndTimePicker = true
                        }
                    )
                }
            }


            // ====================================================
            // Notes
            // ====================================================

            OutlinedTextField(
                value = notes,

                onValueChange = {

                    if (it.length <= 300) {

                        notes = it
                    }
                },

                label = {

                    Text(
                        text = strings.notesLabel
                    )
                },

                placeholder = {

                    Text(
                        text = strings.notesHint
                    )
                },

                supportingText = {

                    Text(
                        text = "${notes.length}/300",
                        modifier = Modifier.fillMaxWidth()
                    )
                },

                minLines = 4,
                maxLines = 6,

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(12.dp)
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            // ====================================================
            // Confirm Button
            // ====================================================

            SoulButton(
                text = strings.confirm,

                onClick = {

                    // 检查结束时间是否晚于开始时间
                    if (!isValidTimeRange(startTime, endTime)) {

                        coroutineScope.launch {

                            snackbarHostState.showSnackbar(
                                message = strings.invalidTime
                            )
                        }

                    } else {

                        val scheduleData = ScheduleData(
                            userId = userId,
                            skill = skill,
                            date = selectedDate,
                            startTime = startTime,
                            endTime = endTime,
                            notes = notes.trim()
                        )


                        // 以后这里可以交给 ViewModel / Retrofit
                        onScheduleConfirm(scheduleData)


                        // 返回上一页
                        onBackClick()
                    }
                }
            )


            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}


// ============================================================
// 信息显示
// ============================================================

@Composable
private fun InfoSection(
    label: String,
    value: String
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = SoulGreen
        )
    }
}


// ============================================================
// 日期 / 时间点击框
// ============================================================

@Composable
private fun PickerField(
    label: String,
    value: String,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(12.dp),

        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        ),

        color = Color.Transparent
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 13.dp
                )
        ) {

            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


// ============================================================
// Time Picker Dialog
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleTimePickerDialog(
    title: String,
    state: TimePickerState,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {

            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                TimePicker(
                    state = state
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = onConfirm
            ) {

                Text(
                    text = confirmText
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    text = cancelText
                )
            }
        }
    )
}


// ============================================================
// Schedule Data
// 后面发送给 Spring Boot 时可以使用
// ============================================================

data class ScheduleData(
    val userId: String,
    val skill: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val notes: String
)


// ============================================================
// 日期相关
// ============================================================

private fun getTodayDate(): String {

    val formatter = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    )

    return formatter.format(
        Date()
    )
}


private fun formatDate(
    millis: Long
): String {

    val formatter = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    )

    formatter.timeZone = TimeZone.getTimeZone("UTC")

    return formatter.format(
        Date(millis)
    )
}


private fun dateStringToMillis(
    date: String
): Long? {

    return try {

        val formatter = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )

        formatter.timeZone = TimeZone.getTimeZone("UTC")

        formatter.parse(date)?.time

    } catch (e: Exception) {

        null
    }
}


// ============================================================
// 时间相关
// ============================================================

private fun formatTime(
    hour: Int,
    minute: Int
): String {

    return String.format(
        Locale.getDefault(),
        "%02d:%02d",
        hour,
        minute
    )
}


private fun getHour(
    time: String
): Int {

    return time
        .substringBefore(":")
        .toIntOrNull()
        ?: 0
}


private fun getMinute(
    time: String
): Int {

    return time
        .substringAfter(":")
        .toIntOrNull()
        ?: 0
}


// ============================================================
// 检查时间
// ============================================================

private fun isValidTimeRange(
    startTime: String,
    endTime: String
): Boolean {

    val startMinutes =
        getHour(startTime) * 60 +
                getMinute(startTime)

    val endMinutes =
        getHour(endTime) * 60 +
                getMinute(endTime)

    return endMinutes > startMinutes
}


// ============================================================
// 多语言
// ============================================================

private fun getScheduleStrings(
    language: AppLanguage
): ScheduleStrings {

    return when (language) {

        AppLanguage.KOREAN -> {

            ScheduleStrings(
                title = "일정 잡기",
                skillLabel = "스킬",
                userLabel = "상대방",
                dateLabel = "날짜",
                timeLabel = "시간",
                startTime = "시작 시간",
                endTime = "종료 시간",
                notesLabel = "메모",
                notesHint = "일정에 대한 메모를 입력하세요.",
                confirm = "일정 확정",
                invalidTime = "종료 시간은 시작 시간보다 늦어야 합니다.",
                ok = "확인",
                cancel = "취소",
                back = "뒤로가기"
            )
        }


        AppLanguage.ENGLISH -> {

            ScheduleStrings(
                title = "Schedule",
                skillLabel = "Skill",
                userLabel = "Partner",
                dateLabel = "Date",
                timeLabel = "Time",
                startTime = "Start",
                endTime = "End",
                notesLabel = "Notes",
                notesHint = "Add notes about this schedule.",
                confirm = "Confirm Schedule",
                invalidTime = "End time must be later than start time.",
                ok = "OK",
                cancel = "Cancel",
                back = "Back"
            )
        }


        AppLanguage.CHINESE -> {

            ScheduleStrings(
                title = "安排日程",
                skillLabel = "技能",
                userLabel = "对方",
                dateLabel = "日期",
                timeLabel = "时间",
                startTime = "开始时间",
                endTime = "结束时间",
                notesLabel = "备注",
                notesHint = "填写本次日程的备注。",
                confirm = "确认日程",
                invalidTime = "结束时间必须晚于开始时间。",
                ok = "确定",
                cancel = "取消",
                back = "返回"
            )
        }
    }
}


// ============================================================
// 多语言数据
// ============================================================

private data class ScheduleStrings(
    val title: String,
    val skillLabel: String,
    val userLabel: String,
    val dateLabel: String,
    val timeLabel: String,
    val startTime: String,
    val endTime: String,
    val notesLabel: String,
    val notesHint: String,
    val confirm: String,
    val invalidTime: String,
    val ok: String,
    val cancel: String,
    val back: String
)