package com.example.soul_android.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soul_android.data.DummyData
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.models.Message
import com.example.soul_android.ui.components.BackgroundGalaxy
import com.example.soul_android.ui.components.LanguageSelector
import com.example.soul_android.ui.components.SoulTextField
import com.example.soul_android.ui.viewmodels.ChatUiState
import com.example.soul_android.ui.viewmodels.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    userId: String,
    onBackClick: () -> Unit = {},
    onScheduleClick: (String) -> Unit = {},
    onCustomerServiceClick: () -> Unit = {},
    chatViewModel: ChatViewModel = viewModel()
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }
    var showExchangeDialog by remember { mutableStateOf(false) }
    
    // 全局翻译模式控制开关
    var isGlobalTranslationEnabled by remember { mutableStateOf(false) }
    val apiService = remember { com.example.soul_android.data.network.SoulApiService.create() }
    val scope = rememberCoroutineScope()

    val strings = ChatStrings(
        onlineStatus = "온라인",
        inputPlaceholder = "메시지를 입력하세요...",
        schedule = "일정 잡기",
        exchange = "스킬 교환",
        exchangeTitle = "스킬 교환 안내",
        exchangeMsg = "상대방과 스킬 교환을 조율할 수 있습니다.",
        close = "닫기",
        typing = "입력 중..."
    )

    val user = DummyData.users.find { it.id == userId } ?: com.example.soul_android.models.User(id = userId, name = userId)
    val uiState by chatViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(userId) {
        chatViewModel.fetchMessages(userId)
    }

    val messages = if (uiState is ChatUiState.Success) {
        (uiState as ChatUiState.Success).messages
    } else {
        listOf()
    }

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
                                Text(user.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = user.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF4CAF50)))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = strings.onlineStatus, fontSize = 12.sp, color = Color(0xFF00D0D9).copy(alpha = 0.8f))
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        // 全局 AI 实时翻译触发开关
                        IconButton(
                            onClick = { isGlobalTranslationEnabled = !isGlobalTranslationEnabled },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isGlobalTranslationEnabled) Color(0xFF00D0D9).copy(alpha = 0.2f) else Color.Transparent)
                                .border(1.dp, if (isGlobalTranslationEnabled) Color(0xFF00D0D9) else Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "AI Global Translate",
                                tint = if (isGlobalTranslationEnabled) Color(0xFF00D0D9) else Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(onClick = onCustomerServiceClick) {
                            Icon(Icons.Default.Help, contentDescription = "AI Customer Service", tint = Color.White)
                        }
                        
                        Spacer(modifier = Modifier.width(4.dp))

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
                // Chat Area
                Box(modifier = Modifier.weight(1f)) {
                    if (uiState is ChatUiState.Loading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF00D0D9))
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(messages) { message ->
                                ChatBubbleComponent(
                                    message = message, 
                                    otherUserName = user.name,
                                    isGlobalTranslationEnabled = isGlobalTranslationEnabled,
                                    apiService = apiService,
                                    targetLangName = language.name
                                )
                            }
                        }
                    }
                }

                // Typing Indicator (Simulated for high-end feel)
                AnimatedVisibility(
                    visible = false, // Placeholder for real typing logic
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = strings.typing,
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }

                // Quick Actions
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AssistChip(
                        onClick = { onScheduleClick(userId) },
                        label = { Text(strings.schedule, color = Color.White, fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = Color(0xFF00D0D9), modifier = Modifier.size(16.dp)) },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color.White.copy(alpha = 0.05f))
                    )
                    AssistChip(
                        onClick = { showExchangeDialog = true },
                        label = { Text(strings.exchange, color = Color.White, fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.SwapHoriz, null, tint = Color(0xFF00D0D9), modifier = Modifier.size(16.dp)) },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color.White.copy(alpha = 0.05f))
                    )
                }

                // Improved Input Area
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White.copy(alpha = 0.03f),
                    border = BorderStroke(1.dp, color = Color.White.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .navigationBarsPadding().imePadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SoulTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = strings.inputPlaceholder,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        // Animated Send Button
                        val sendButtonScale by animateFloatAsState(if (messageText.isNotBlank()) 1.1f else 1f)
                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    chatViewModel.sendMessage(userId, messageText)
                                    messageText = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .scale(sendButtonScale)
                                .clip(CircleShape)
                                .background(
                                    if (messageText.isNotBlank()) 
                                        Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2)))
                                    else 
                                        Brush.linearGradient(colors = listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.1f)))
                                )
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
            title = { Text(strings.exchangeTitle, fontWeight = FontWeight.Bold) },
            text = { Text(strings.exchangeMsg) },
            confirmButton = {
                TextButton(onClick = { showExchangeDialog = false }) { 
                    Text(strings.close, color = Color(0xFF00D0D9), fontWeight = FontWeight.Bold) 
                }
            }
        )
    }
}

@Composable
private fun ChatBubbleComponent(message: Message, otherUserName: String, isGlobalTranslationEnabled: Boolean, apiService: com.example.soul_android.data.network.SoulApiService, targetLangName: String) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeStr = timeFormat.format(Date(message.timestamp))
    var translatedText by remember { mutableStateOf("") }
    var isTranslating by remember { mutableStateOf(false) }

    // 当全局翻译开关开启，且这条消息不是“我”发的，且还没有翻译过时，自动触发后端 AI 实时翻译
    LaunchedEffect(isGlobalTranslationEnabled) {
        if (isGlobalTranslationEnabled && !message.isMe && translatedText.isEmpty()) {
            isTranslating = true
            try {
                val resp = apiService.translateText(text = message.text, targetLang = targetLangName)
                if (resp.isSuccessful && resp.body() != null) {
                    translatedText = resp.body()!!.advice
                }
            } catch (e: Exception) {
                translatedText = "[翻译失败]"
            }
            isTranslating = false
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (message.isMe) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!message.isMe) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape)
                        .background(Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2))))
                        .align(Alignment.Top),
                    contentAlignment = Alignment.Center
                ) {
                    Text(otherUserName.take(1), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (message.isMe) Text(timeStr, fontSize = 10.sp, color = Color.White.copy(alpha = 0.3f), modifier = Modifier.padding(end = 8.dp, bottom = 4.dp))
            
            Column(horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start) {
                Surface(
                    color = if (message.isMe) Color.Transparent else Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(
                        topStart = if (message.isMe) 20.dp else 4.dp,
                        topEnd = if (message.isMe) 4.dp else 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    ),
                    border = if (message.isMe) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    modifier = if (message.isMe) Modifier.background(
                        Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2))),
                        shape = RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
                    ) else Modifier
                ) {
                    Text(
                        text = message.text,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        color = Color.White,
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )
                }
                
                // 全局翻译文本渲染区域 (优雅气泡下延展)
                if (isGlobalTranslationEnabled && !message.isMe) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isTranslating) "翻译中..." else if(translatedText.isNotEmpty()) "✨ $translatedText" else "",
                        color = Color(0xFF00D0D9),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }
            
            if (!message.isMe) Text(timeStr, fontSize = 10.sp, color = Color.White.copy(alpha = 0.3f), modifier = Modifier.padding(start = 8.dp, bottom = 4.dp))
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
    val close: String,
    val typing: String
)

