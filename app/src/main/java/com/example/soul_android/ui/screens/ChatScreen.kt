package com.example.soul_android.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.soul_android.data.network.SoulApiService
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.models.Message
import com.example.soul_android.ui.components.BackgroundGalaxy
import com.example.soul_android.ui.components.LanguageSelector
import com.example.soul_android.ui.components.SoulTextField
import com.example.soul_android.ui.viewmodels.ChatUiState
import com.example.soul_android.ui.viewmodels.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    userId: String,
    onBackClick: () -> Unit = {},
    onScheduleClick: (String) -> Unit = {},
    onCustomerServiceClick: () -> Unit = {},
    chatViewModel: ChatViewModel = viewModel()
) {

    var language by remember {
        mutableStateOf(AppLanguage.KOREAN)
    }

    var languageMenuExpanded by remember {
        mutableStateOf(false)
    }

    var messageText by remember {
        mutableStateOf("")
    }

    var showExchangeDialog by remember {
        mutableStateOf(false)
    }

    /*
     * 全局翻译开关
     */
    var isGlobalTranslationEnabled by remember {
        mutableStateOf(false)
    }

    /*
     * AI 翻译接口
     */
    val apiService = remember {
        SoulApiService.create()
    }

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


    // =====================================================
    // 当前登录用户
    // =====================================================

    val context = LocalContext.current

    val currentUsername = remember(context) {

        /*
         * 登录页面正常情况下应该把 username
         * 保存到 soul_login_prefs。
         */
        val soulPrefs =
            context.getSharedPreferences(
                "soul_login_prefs",
                Context.MODE_PRIVATE
            )

        val userPrefs =
            context.getSharedPreferences(
                "user_prefs",
                Context.MODE_PRIVATE
            )

        /*
         * 非常重要：
         *
         * 不再使用
         *
         * ?: "xiangyu"
         *
         * 如果没有登录信息，就返回空字符串。
         */
        soulPrefs
            .getString("username", "")
            ?.takeIf {
                it.isNotBlank()
            }
            ?: userPrefs
                .getString("username", "")
                ?.takeIf {
                    it.isNotBlank()
                }
            ?: ""
    }


    // =====================================================
    // 聊天对象资料
    // =====================================================

    /*
     * userId 是聊天对象的 username。
     *
     * 例如：
     * userId = "minho10"
     *
     * API 请求仍然必须使用 username，
     * 但是 UI 应显示用户真正的 name，例如 "서민호"。
     */
    var partnerName by remember(userId) {
        mutableStateOf(userId)
    }

    /*
     * 根据 username 从后端获取聊天对象资料。
     *
     * 这样：
     * API receiverUsername = minho10
     * UI 显示 name = 서민호
     */
    LaunchedEffect(userId) {

        if (userId.isBlank()) {
            partnerName = ""
            return@LaunchedEffect
        }

        // 请求开始前先用 username 兜底，避免页面空白
        partnerName = userId

        try {

            val response =
                apiService.getUserProfile(
                    username = userId
                )

            if (response.isSuccessful) {

                val profile =
                    response.body()

                partnerName =
                    profile
                        ?.name
                        ?.takeIf { it.isNotBlank() }
                        ?: profile
                            ?.username
                            ?.takeIf { it.isNotBlank() }
                                ?: userId

                android.util.Log.d(
                    "CHAT_SCREEN",
                    "聊天对象资料加载成功: username=$userId name=$partnerName"
                )

            } else {

                android.util.Log.e(
                    "CHAT_SCREEN",
                    "聊天对象资料加载失败: username=$userId code=${response.code()} error=${response.errorBody()?.string()}"
                )
            }

        } catch (e: Exception) {

            android.util.Log.e(
                "CHAT_SCREEN",
                "获取聊天对象资料异常: username=$userId",
                e
            )

            // 网络异常时仍然使用 username，不影响聊天 API
            partnerName = userId
        }
    }


    // =====================================================
    // ViewModel
    // =====================================================

    val uiState by
    chatViewModel.uiState.collectAsState()

    val listState =
        rememberLazyListState()


    // =====================================================
    // 进入聊天页面
    // =====================================================

    LaunchedEffect(
        userId,
        currentUsername
    ) {

        /*
         * 必须确保：
         *
         * 自己 username 不为空
         * 对方 username 不为空
         */
        if (
            currentUsername.isNotBlank() &&
            userId.isNotBlank()
        ) {

            android.util.Log.d(
                "CHAT_SCREEN",
                "进入聊天页面: " +
                        "me=$currentUsername " +
                        "partner=$userId"
            )

            chatViewModel.fetchMessages(
                otherUserId = userId,
                currentUsername = currentUsername
            )

        } else {

            android.util.Log.e(
                "CHAT_SCREEN",
                "无法获取聊天记录: " +
                        "currentUsername=$currentUsername " +
                        "userId=$userId"
            )
        }
    }


    // =====================================================
    // 消息列表
    // =====================================================

    val (messages, partnerAvatar) =
        when (val state = uiState) {

            is ChatUiState.Success -> {
                Pair(state.messages, state.partnerAvatar)
            }

            else -> {
                Pair(emptyList(), null)
            }
        }


    /*
     * 新消息出现后自动滚动到底部
     */
    LaunchedEffect(
        messages.size
    ) {

        if (messages.isNotEmpty()) {

            listState.animateScrollToItem(
                messages.size - 1
            )
        }
    }


    // =====================================================
    // 页面
    // =====================================================

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {

        BackgroundGalaxy()

        Scaffold(

            containerColor =
                Color.Transparent,

            // =================================================
            // 顶部栏
            // =================================================

            topBar = {

                TopAppBar(

                    colors =
                        TopAppBarDefaults
                            .topAppBarColors(
                                containerColor =
                                    Color.Transparent
                            ),

                    title = {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            /*
                             * 头像
                             */
                            Box(
                                modifier =
                                    Modifier
                                        .size(40.dp)
                                        .clip(
                                            CircleShape
                                        )
                                        .background(
                                            Brush.linearGradient(
                                                colors =
                                                    listOf(
                                                        Color(
                                                            0xFF00D0D9
                                                        ),
                                                        Color(
                                                            0xFF7E57C2
                                                        )
                                                    )
                                            )
                                        ),

                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(
                                    text =
                                        partnerName
                                            .take(1),

                                    color =
                                        Color.White,

                                    fontWeight =
                                        FontWeight.Bold,

                                    fontSize =
                                        16.sp
                                )
                            }


                            Spacer(
                                modifier =
                                    Modifier.width(
                                        12.dp
                                    )
                            )


                            Column {

                                Text(
                                    text =
                                        partnerName,

                                    fontSize =
                                        16.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        Color.White
                                )


                                Row(
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Box(
                                        modifier =
                                            Modifier
                                                .size(
                                                    8.dp
                                                )
                                                .clip(
                                                    CircleShape
                                                )
                                                .background(
                                                    Color(
                                                        0xFF4CAF50
                                                    )
                                                )
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(
                                                6.dp
                                            )
                                    )

                                    Text(
                                        text =
                                            strings.onlineStatus,

                                        fontSize =
                                            12.sp,

                                        color =
                                            Color(
                                                0xFF00D0D9
                                            ).copy(
                                                alpha =
                                                    0.8f
                                            )
                                    )
                                }
                            }
                        }
                    },


                    // =================================================
                    // 返回按钮
                    // =================================================

                    navigationIcon = {

                        IconButton(
                            onClick =
                                onBackClick
                        ) {

                            Icon(
                                imageVector =
                                    Icons
                                        .AutoMirrored
                                        .Filled
                                        .ArrowBack,

                                contentDescription =
                                    "Back",

                                tint =
                                    Color.White
                            )
                        }
                    },


                    // =================================================
                    // 顶部操作按钮
                    // =================================================

                    actions = {

                        /*
                         * AI 全局翻译
                         */
                        IconButton(

                            onClick = {

                                isGlobalTranslationEnabled =
                                    !isGlobalTranslationEnabled
                            },

                            modifier =
                                Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            12.dp
                                        )
                                    )
                                    .background(

                                        if (
                                            isGlobalTranslationEnabled
                                        ) {

                                            Color(
                                                0xFF00D0D9
                                            ).copy(
                                                alpha =
                                                    0.2f
                                            )

                                        } else {

                                            Color.Transparent
                                        }
                                    )
                                    .border(

                                        width =
                                            1.dp,

                                        color =
                                            if (
                                                isGlobalTranslationEnabled
                                            ) {

                                                Color(
                                                    0xFF00D0D9
                                                )

                                            } else {

                                                Color.White.copy(
                                                    alpha =
                                                        0.2f
                                                )
                                            },

                                        shape =
                                            RoundedCornerShape(
                                                12.dp
                                            )
                                    )
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.SwapHoriz,

                                contentDescription =
                                    "AI Global Translate",

                                tint =
                                    if (
                                        isGlobalTranslationEnabled
                                    ) {

                                        Color(
                                            0xFF00D0D9
                                        )

                                    } else {

                                        Color.White
                                    }
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.width(
                                    8.dp
                                )
                        )


                        /*
                         * AI 客服
                         */
                        IconButton(
                            onClick =
                                onCustomerServiceClick
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Help,

                                contentDescription =
                                    "AI Customer Service",

                                tint =
                                    Color.White
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.width(
                                    4.dp
                                )
                        )


                        /*
                         * 语言选择
                         */
                        LanguageSelector(

                            currentLanguage =
                                language,

                            expanded =
                                languageMenuExpanded,

                            onExpandedChange = {
                                languageMenuExpanded =
                                    it
                            },

                            onLanguageSelected = {
                                language =
                                    it

                                languageMenuExpanded =
                                    false
                            }
                        )
                    }
                )
            }

        ) { padding ->


            Column(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            padding
                        )
            ) {


                // =================================================
                // 聊天区域
                // =================================================

                Box(

                    modifier =
                        Modifier
                            .weight(
                                1f
                            )
                            .fillMaxWidth()
                ) {


                    /*
                     * 没有登录信息
                     */
                    if (
                        currentUsername.isBlank()
                    ) {

                        Box(

                            modifier =
                                Modifier
                                    .fillMaxSize(),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text =
                                    "로그인 정보를 찾을 수 없습니다.",

                                color =
                                    Color.Red.copy(
                                        alpha =
                                            0.8f
                                    )
                            )
                        }

                    } else {

                        when (
                            val state =
                                uiState
                        ) {


                            // =========================================
                            // Loading
                            // =========================================

                            ChatUiState.Loading -> {

                                Box(

                                    modifier =
                                        Modifier
                                            .fillMaxSize(),

                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    CircularProgressIndicator(
                                        color =
                                            Color(
                                                0xFF00D0D9
                                            )
                                    )
                                }
                            }


                            // =========================================
                            // Error
                            // =========================================

                            is ChatUiState.Error -> {

                                Box(

                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .padding(
                                                24.dp
                                            ),

                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Text(
                                        text =
                                            state.message,

                                        color =
                                            Color.Red.copy(
                                                alpha =
                                                    0.8f
                                            ),

                                        fontSize =
                                            14.sp
                                    )
                                }
                            }


                            // =========================================
                            // Success
                            // =========================================

                            is ChatUiState.Success -> {

                                LazyColumn(

                                    state =
                                        listState,

                                    modifier =
                                        Modifier
                                            .fillMaxSize(),

                                    contentPadding =
                                        PaddingValues(
                                            16.dp
                                        ),

                                    verticalArrangement =
                                        Arrangement.spacedBy(
                                            16.dp
                                        )
                                ) {

                                    items(
                                        items =
                                            state.messages,

                                        key = {
                                            it.id
                                        }
                                    ) { message ->

                                        ChatBubbleComponent(

                                            message =
                                                message,

                                            otherUserName =
                                                partnerName,

                                            partnerAvatar =
                                                partnerAvatar,

                                            isGlobalTranslationEnabled =
                                                isGlobalTranslationEnabled,

                                            apiService =
                                                apiService,

                                            targetLangName =
                                                language.name
                                        )
                                    }
                                }
                            }
                        }
                    }
                }


                // =================================================
                // 正在输入
                // =================================================

                AnimatedVisibility(

                    visible =
                        false,

                    enter =
                        fadeIn() +
                                expandVertically(),

                    exit =
                        fadeOut() +
                                shrinkVertically()
                ) {

                    Text(

                        text =
                            strings.typing,

                        color =
                            Color.White.copy(
                                alpha =
                                    0.4f
                            ),

                        fontSize =
                            12.sp,

                        modifier =
                            Modifier.padding(
                                horizontal =
                                    24.dp,

                                vertical =
                                    4.dp
                            )
                    )
                }


                // =================================================
                // 快捷按钮
                // =================================================

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal =
                                    16.dp,

                                vertical =
                                    8.dp
                            ),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        )
                ) {


                    AssistChip(

                        onClick = {
                            onScheduleClick(
                                userId
                            )
                        },

                        label = {

                            Text(
                                text =
                                    strings.schedule,

                                color =
                                    Color.White,

                                fontSize =
                                    12.sp
                            )
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons
                                        .Default
                                        .CalendarMonth,

                                contentDescription =
                                    null,

                                tint =
                                    Color(
                                        0xFF00D0D9
                                    ),

                                modifier =
                                    Modifier.size(
                                        16.dp
                                    )
                            )
                        },

                        shape =
                            RoundedCornerShape(
                                20.dp
                            ),

                        border =
                            BorderStroke(

                                width =
                                    1.dp,

                                color =
                                    Color.White.copy(
                                        alpha =
                                            0.12f
                                    )
                            ),

                        colors =
                            AssistChipDefaults
                                .assistChipColors(

                                    containerColor =
                                        Color.White.copy(
                                            alpha =
                                                0.05f
                                        )
                                )
                    )


                    AssistChip(

                        onClick = {
                            showExchangeDialog =
                                true
                        },

                        label = {

                            Text(
                                text =
                                    strings.exchange,

                                color =
                                    Color.White,

                                fontSize =
                                    12.sp
                            )
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.SwapHoriz,

                                contentDescription =
                                    null,

                                tint =
                                    Color(
                                        0xFF00D0D9
                                    ),

                                modifier =
                                    Modifier.size(
                                        16.dp
                                    )
                            )
                        },

                        shape =
                            RoundedCornerShape(
                                20.dp
                            ),

                        border =
                            BorderStroke(

                                width =
                                    1.dp,

                                color =
                                    Color.White.copy(
                                        alpha =
                                            0.12f
                                    )
                            ),

                        colors =
                            AssistChipDefaults
                                .assistChipColors(

                                    containerColor =
                                        Color.White.copy(
                                            alpha =
                                                0.05f
                                        )
                                )
                    )
                }


                // =================================================
                // 输入区域
                // =================================================

                Surface(

                    modifier =
                        Modifier
                            .fillMaxWidth(),

                    color =
                        Color.White.copy(
                            alpha =
                                0.03f
                        ),

                    border =
                        BorderStroke(

                            width =
                                1.dp,

                            color =
                                Color.White.copy(
                                    alpha =
                                        0.1f
                                )
                        )
                ) {


                    Row(

                        modifier =
                            Modifier
                                .padding(
                                    horizontal =
                                        16.dp,

                                    vertical =
                                        12.dp
                                )
                                .navigationBarsPadding()
                                .imePadding(),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {


                        SoulTextField(

                            value =
                                messageText,

                            onValueChange = {
                                messageText =
                                    it
                            },

                            placeholder =
                                strings.inputPlaceholder,

                            modifier =
                                Modifier.weight(
                                    1f
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    12.dp
                                )
                        )


                        // =================================================
                        // 发送按钮
                        // =================================================

                        val canSend =
                            messageText.isNotBlank() &&
                                    currentUsername.isNotBlank() &&
                                    userId.isNotBlank()


                        val sendButtonScale by
                        animateFloatAsState(

                            targetValue =
                                if (
                                    canSend
                                ) {
                                    1.1f
                                } else {
                                    1f
                                },

                            label =
                                "sendButtonScale"
                        )


                        IconButton(

                            onClick = {

                                if (
                                    canSend
                                ) {

                                    /*
                                     * 保存内容。
                                     *
                                     * 避免先 clear 后拿不到原消息。
                                     */
                                    val messageToSend =
                                        messageText.trim()


                                    android.util.Log.d(
                                        "CHAT_SCREEN",
                                        "点击发送: " +
                                                "sender=$currentUsername " +
                                                "receiver=$userId " +
                                                "content=$messageToSend"
                                    )


                                    /*
                                     * 非常重要：
                                     *
                                     * 现在必须传三个参数：
                                     *
                                     * receiverId
                                     * content
                                     * currentUsername
                                     */
                                    chatViewModel.sendMessage(

                                        receiverId =
                                            userId,

                                        content =
                                            messageToSend,

                                        currentUsername =
                                            currentUsername
                                    )


                                    /*
                                     * 清空输入框。
                                     *
                                     * 如果以后需要实现
                                     * “发送失败保留内容”，
                                     * 可以再把这个逻辑移到 ViewModel。
                                     */
                                    messageText =
                                        ""
                                }
                            },

                            enabled =
                                canSend,

                            modifier =
                                Modifier
                                    .size(
                                        48.dp
                                    )
                                    .scale(
                                        sendButtonScale
                                    )
                                    .clip(
                                        CircleShape
                                    )
                                    .background(

                                        if (
                                            canSend
                                        ) {

                                            Brush.linearGradient(

                                                colors =
                                                    listOf(
                                                        Color(
                                                            0xFF00D0D9
                                                        ),
                                                        Color(
                                                            0xFF7E57C2
                                                        )
                                                    )
                                            )

                                        } else {

                                            Brush.linearGradient(

                                                colors =
                                                    listOf(
                                                        Color.White.copy(
                                                            alpha =
                                                                0.1f
                                                        ),
                                                        Color.White.copy(
                                                            alpha =
                                                                0.1f
                                                        )
                                                    )
                                            )
                                        }
                                    )
                        ) {

                            Icon(

                                imageVector =
                                    Icons
                                        .AutoMirrored
                                        .Filled
                                        .Send,

                                contentDescription =
                                    "Send",

                                tint =
                                    Color.White
                            )
                        }
                    }
                }
            }
        }
    }


    // =====================================================
    // Skill Exchange Dialog
    // =====================================================

    if (
        showExchangeDialog
    ) {

        AlertDialog(

            onDismissRequest = {
                showExchangeDialog =
                    false
            },

            containerColor =
                Color(
                    0xFF1C1F26
                ),

            titleContentColor =
                Color.White,

            textContentColor =
                Color.White.copy(
                    alpha =
                        0.7f
                ),

            title = {

                Text(
                    text =
                        strings.exchangeTitle,

                    fontWeight =
                        FontWeight.Bold
                )
            },

            text = {

                Text(
                    text =
                        strings.exchangeMsg
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {
                        showExchangeDialog =
                            false
                    }
                ) {

                    Text(

                        text =
                            strings.close,

                        color =
                            Color(
                                0xFF00D0D9
                            ),

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        )
    }
}


// =========================================================
// Chat Bubble
// =========================================================

@Composable
private fun ChatBubbleComponent(

    message: Message,

    otherUserName: String,

    partnerAvatar: String?,

    isGlobalTranslationEnabled: Boolean,

    apiService: SoulApiService,

    targetLangName: String
) {


    val timeFormat =
        remember {

            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            )
        }


    val timeStr =
        timeFormat.format(
            Date(
                message.timestamp
            )
        )


    var translatedText by
    remember(
        message.id
    ) {
        mutableStateOf("")
    }


    var isTranslating by
    remember(
        message.id
    ) {
        mutableStateOf(false)
    }


    // =====================================================
    // AI 自动翻译
    // =====================================================

    LaunchedEffect(
        isGlobalTranslationEnabled,
        message.id,
        targetLangName
    ) {

        if (
            isGlobalTranslationEnabled &&
            !message.isMe &&
            translatedText.isEmpty()
        ) {

            isTranslating =
                true

            try {

                val resp =
                    apiService.translateText(

                        text =
                            message.text,

                        targetLang =
                            targetLangName
                    )


                if (
                    resp.isSuccessful &&
                    resp.body() != null
                ) {

                    translatedText =
                        resp.body()!!
                            .advice

                } else {

                    translatedText =
                        "[翻译失败]"
                }

            } catch (
                e: Exception
            ) {

                translatedText =
                    "[翻译失败]"
            }


            isTranslating =
                false
        }
    }


    // =====================================================
    // Message UI
    // =====================================================

    Column(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalAlignment =
            if (
                message.isMe
            ) {
                Alignment.End
            } else {
                Alignment.Start
            }
    ) {


        Row(

            verticalAlignment =
                Alignment.Bottom,

            horizontalArrangement =
                if (
                    message.isMe
                ) {
                    Arrangement.End
                } else {
                    Arrangement.Start
                },

            modifier =
                Modifier.fillMaxWidth()
        ) {


            /*
             * 对方头像
             */
            if (
                !message.isMe
            ) {
                val avatarUrl = buildAvatarUrl(partnerAvatar)

                Box(

                    modifier =
                        Modifier
                            .size(
                                32.dp
                            )
                            .clip(
                                CircleShape
                            )
                            .background(

                                Brush.linearGradient(

                                    colors =
                                        listOf(
                                            Color(
                                                0xFF00D0D9
                                            ),
                                            Color(
                                                0xFF7E57C2
                                            )
                                        )
                                )
                            )
                            .align(
                                Alignment.Top
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {
                    if (!avatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Partner Avatar",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(

                            text =
                                otherUserName
                                    .take(1),

                            color =
                                Color.White,

                            fontSize =
                                12.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.width(
                            8.dp
                        )
                )
            }


            /*
             * 自己消息时间
             */
            if (
                message.isMe
            ) {

                Text(

                    text =
                        timeStr,

                    fontSize =
                        10.sp,

                    color =
                        Color.White.copy(
                            alpha =
                                0.3f
                        ),

                    modifier =
                        Modifier.padding(
                            end =
                                8.dp,

                            bottom =
                                4.dp
                        )
                )
            }


            Column(

                horizontalAlignment =
                    if (
                        message.isMe
                    ) {
                        Alignment.End
                    } else {
                        Alignment.Start
                    }
            ) {


                Surface(

                    color =
                        if (
                            message.isMe
                        ) {

                            Color.Transparent

                        } else {

                            Color.White.copy(
                                alpha =
                                    0.08f
                            )
                        },

                    shape =
                        RoundedCornerShape(

                            topStart =
                                if (
                                    message.isMe
                                ) {
                                    20.dp
                                } else {
                                    4.dp
                                },

                            topEnd =
                                if (
                                    message.isMe
                                ) {
                                    4.dp
                                } else {
                                    20.dp
                                },

                            bottomStart =
                                20.dp,

                            bottomEnd =
                                20.dp
                        ),

                    border =
                        if (
                            message.isMe
                        ) {

                            null

                        } else {

                            BorderStroke(

                                width =
                                    1.dp,

                                color =
                                    Color.White.copy(
                                        alpha =
                                            0.08f
                                    )
                            )
                        },

                    modifier =
                        if (
                            message.isMe
                        ) {

                            Modifier.background(

                                brush =
                                    Brush.linearGradient(

                                        colors =
                                            listOf(
                                                Color(
                                                    0xFF00D0D9
                                                ),
                                                Color(
                                                    0xFF7E57C2
                                                )
                                            )
                                    ),

                                shape =
                                    RoundedCornerShape(
                                        topStart =
                                            20.dp,

                                        topEnd =
                                            4.dp,

                                        bottomEnd =
                                            20.dp,

                                        bottomStart =
                                            20.dp
                                    )
                            )

                        } else {

                            Modifier
                        }
                ) {


                    Text(

                        text =
                            message.text,

                        modifier =
                            Modifier.padding(

                                horizontal =
                                    14.dp,

                                vertical =
                                    10.dp
                            ),

                        color =
                            Color.White,

                        fontSize =
                            15.sp,

                        lineHeight =
                            20.sp
                    )
                }


                // =================================================
                // 翻译结果
                // =================================================

                if (
                    isGlobalTranslationEnabled &&
                    !message.isMe
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                4.dp
                            )
                    )


                    Text(

                        text =
                            when {

                                isTranslating -> {
                                    "翻译中..."
                                }

                                translatedText
                                    .isNotEmpty() -> {

                                    "✨ $translatedText"
                                }

                                else -> {
                                    ""
                                }
                            },

                        color =
                            Color(
                                0xFF00D0D9
                            ),

                        fontSize =
                            12.sp,

                        fontWeight =
                            FontWeight.Medium,

                        modifier =
                            Modifier.padding(
                                horizontal =
                                    6.dp
                            )
                    )
                }
            }


            /*
             * 对方消息时间
             */
            if (
                !message.isMe
            ) {

                Text(

                    text =
                        timeStr,

                    fontSize =
                        10.sp,

                    color =
                        Color.White.copy(
                            alpha =
                                0.3f
                        ),

                    modifier =
                        Modifier.padding(

                            start =
                                8.dp,

                            bottom =
                                4.dp
                        )
                )
            }
        }
    }
}


// =========================================================
// Strings
// =========================================================

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

private fun buildAvatarUrl(avatar: String?): String? {
    if (avatar.isNullOrBlank()) return null
    if (avatar.startsWith("http://") || avatar.startsWith("https://")) return avatar
    return SoulApiService.BASE_URL.trimEnd('/') + "/" + avatar.trimStart('/')
}
