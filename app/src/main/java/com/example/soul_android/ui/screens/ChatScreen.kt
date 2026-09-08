package com.example.soul_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
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
import com.example.soul_android.models.Message
import com.example.soul_android.ui.components.BackgroundGalaxy
import com.example.soul_android.ui.components.LanguageSelector
import com.example.soul_android.ui.components.SoulTextField
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    userId: String,
    onBackClick: () -> Unit = {},
    onScheduleClick: (String) -> Unit = {}
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }
    var showExchangeDialog by remember { mutableStateOf(false) }

    val strings = when (language) {
        AppLanguage.KOREAN -> ChatStrings("온라인", "메시지를 입력하세요...", "일정 잡기", "스킬 교환", "스킬 교환 안내", "상대방과 스킬 교환을 조율할 수 있습니다.", "닫기")
        AppLanguage.ENGLISH -> ChatStrings("Online", "Type a message...", "Schedule", "Skill Exchange", "Skill Exchange", "You can arrange a skill exchange with this user.", "Close")
        AppLanguage.CHINESE -> ChatStrings("在线", "输入消息...", "安排时间", "技能交换", "技能交换说明", "您可以与此用户安排技能交换。", "关闭")
    }

    val user = DummyData.users.find { it.id == userId } ?: DummyData.currentUser
    var messages by remember { mutableStateOf(DummyData.chatMessages) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundGalaxy()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape)
                                    .background(Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = user.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(text = strings.onlineStatus, fontSize = 12.sp, color = Color(0xFF00D0D9))
                            }
                        }
                    },
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
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { message ->
                        ChatBubbleComponent(message)
                    }
                }

                // Quick Actions
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = { onScheduleClick(userId) },
                        label = { Text(strings.schedule, color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = Color(0xFF00D0D9), modifier = Modifier.size(18.dp)) },
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color.White.copy(alpha = 0.2f)
                        ),
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color.White.copy(alpha = 0.1f))
                    )
                    AssistChip(
                        onClick = { showExchangeDialog = true },
                        label = { Text(strings.exchange, color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.SwapHoriz, null, tint = Color(0xFF00D0D9), modifier = Modifier.size(18.dp)) },
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color.White.copy(alpha = 0.2f)
                        ),
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color.White.copy(alpha = 0.1f))
                    )
                }

                // Input Area (Glassmorphism)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.05f))
                        .blur(10.dp)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding().imePadding()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SoulTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = strings.inputPlaceholder,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    val newMessage = Message(UUID.randomUUID().toString(), "me", messageText, System.currentTimeMillis(), true)
                                    messages = messages + newMessage
                                    messageText = ""
                                }
                            },
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF00D0D9))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = Color.White)
                        }
                    }
                }
            }
        }
    }

    if (showExchangeDialog) {
        AlertDialog(
            onDismissRequest = { showExchangeDialog = false },
            containerColor = Color(0xFF1C1F26),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.7f),
            title = { Text(strings.exchangeTitle) },
            text = { Text(strings.exchangeMsg) },
            confirmButton = {
                TextButton(onClick = { showExchangeDialog = false }) { Text(strings.close, color = Color(0xFF00D0D9)) }
            }
        )
    }
}

@Composable
private fun ChatBubbleComponent(message: Message) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeStr = timeFormat.format(Date(message.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (message.isMe) Text(timeStr, fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f), modifier = Modifier.padding(end = 6.dp, bottom = 4.dp))
            
            Surface(
                color = if (message.isMe) Color.Transparent else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(
                    topStart = 20.dp, topEnd = 20.dp,
                    bottomStart = if (message.isMe) 20.dp else 4.dp,
                    bottomEnd = if (message.isMe) 4.dp else 20.dp
                ),
                border = if (message.isMe) null else androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                modifier = if (message.isMe) Modifier.background(
                    Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2))),
                    shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
                ) else Modifier
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = Color.White,
                    fontSize = 15.sp
                )
            }
            
            if (!message.isMe) Text(timeStr, fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f), modifier = Modifier.padding(start = 6.dp, bottom = 4.dp))
        }
    }
}

private data class ChatStrings(
    val onlineStatus: String,
    val inputPlaceholder: String,
    val schedule: String,
    val exchange: String,
    val exchangeTitle: String,
    val exchangeMsg: String,
    val close: String
)
