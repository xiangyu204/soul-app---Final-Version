package com.example.soul_android.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.data.DummyData
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.models.Message
import com.example.soul_android.ui.components.LanguageSelector
import com.example.soul_android.ui.components.SoulGreen
import java.text.SimpleDateFormat
import java.util.*

private val ChatBubbleGray = Color(0xFFF1F1F1)

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
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = user.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = strings.onlineStatus, fontSize = 12.sp, color = SoulGreen)
                        }
                    }
                },
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
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(state = listState, modifier = Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(messages) { message ->
                    ChatBubbleComponent(message)
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = { onScheduleClick(userId) }, label = { Text(strings.schedule) }, leadingIcon = { Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(18.dp)) }, shape = RoundedCornerShape(20.dp), colors = AssistChipDefaults.assistChipColors(labelColor = SoulGreen, leadingIconContentColor = SoulGreen))
                AssistChip(onClick = { showExchangeDialog = true }, label = { Text(strings.exchange) }, leadingIcon = { Icon(Icons.Default.SwapHoriz, null, modifier = Modifier.size(18.dp)) }, shape = RoundedCornerShape(20.dp), colors = AssistChipDefaults.assistChipColors(labelColor = SoulGreen, leadingIconContentColor = SoulGreen))
            }

            Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).navigationBarsPadding().imePadding(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = messageText, onValueChange = { messageText = it }, modifier = Modifier.weight(1f), placeholder = { Text(strings.inputPlaceholder) }, shape = RoundedCornerShape(24.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoulGreen, unfocusedBorderColor = Color.LightGray), maxLines = 3)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {
                        if (messageText.isNotBlank()) {
                            val newMessage = Message(UUID.randomUUID().toString(), "me", messageText, System.currentTimeMillis(), true)
                            messages = messages + newMessage
                            messageText = ""
                        }
                    }, colors = IconButtonDefaults.iconButtonColors(containerColor = SoulGreen, contentColor = Color.White), modifier = Modifier.size(48.dp)) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }

    if (showExchangeDialog) {
        AlertDialog(onDismissRequest = { showExchangeDialog = false }, title = { Text(strings.exchangeTitle) }, text = { Text(strings.exchangeMsg) }, confirmButton = {
            TextButton(onClick = { showExchangeDialog = false }) { Text(strings.close, color = SoulGreen) }
        })
    }
}

@Composable
private fun ChatBubbleComponent(message: Message) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeStr = timeFormat.format(Date(message.timestamp))

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start) {
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(start = if (message.isMe) 40.dp else 0.dp, end = if (message.isMe) 0.dp else 40.dp)) {
            if (message.isMe) Text(text = timeStr, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(end = 4.dp, bottom = 4.dp))
            Surface(color = if (message.isMe) SoulGreen else ChatBubbleGray, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = if (message.isMe) 16.dp else 0.dp, bottomEnd = if (message.isMe) 0.dp else 16.dp)) {
                Text(text = message.text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = if (message.isMe) Color.White else Color.Black, fontSize = 15.sp)
            }
            if (!message.isMe) Text(text = timeStr, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
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
