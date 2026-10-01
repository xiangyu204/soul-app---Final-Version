package com.example.soul_android.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soul_android.data.DummyData
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.models.User
import com.example.soul_android.ui.components.BackgroundGalaxy
import com.example.soul_android.ui.components.BrandingSection
import com.example.soul_android.ui.components.LanguageSelector
import com.example.soul_android.ui.viewmodels.ChatListUiState
import com.example.soul_android.ui.viewmodels.ChatListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onChatClick: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToExplore: () -> Unit = {},
    onNavigateToMatches: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    chatListViewModel: ChatListViewModel = viewModel()
) {
    var language by remember {
        mutableStateOf(AppLanguage.KOREAN)
    }

    var languageMenuExpanded by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    /*
     * 현재 로그인한 사용자 가져오기
     */
    val currentUsername = remember(context) {

        val soulPrefs = context.getSharedPreferences(
            "soul_login_prefs",
            Context.MODE_PRIVATE
        )

        val userPrefs = context.getSharedPreferences(
            "user_prefs",
            Context.MODE_PRIVATE
        )

        soulPrefs.getString("username", "")
            ?.takeIf { it.isNotBlank() }
            ?: userPrefs.getString("username", "")
                ?.takeIf { it.isNotBlank() }
            ?: "xiangyu"
    }

    /*
     * 채팅방 상태
     */
    val chatListState by chatListViewModel.uiState.collectAsState()

    /*
     * 화면 진입 시 채팅방 조회
     */
    LaunchedEffect(currentUsername) {
        if (currentUsername.isNotBlank()) {
            chatListViewModel.fetchRooms(currentUsername)
        }
    }

    val strings = ChatListStrings(
        title = "채팅",
        home = "홈",
        explore = "탐색",
        matches = "매칭",
        chat = "채팅",
        profile = "프로필",
        lastMsgPrefix = "마지막 메시지: "
    )

    /*
     * 실제 채팅 데이터
     *
     * Success -> 서버 데이터
     * Loading/Error -> 임시 데이터
     */
    val activeChats: List<User> = when (val state = chatListState) {

        is ChatListUiState.Success -> {
            state.rooms
        }

        else -> {
            DummyData.users.take(6)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        /*
         * Galaxy 배경
         */
        BackgroundGalaxy()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,

            /*
             * 상단
             */
            topBar = {

                Column(
                    modifier = Modifier.statusBarsPadding()
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 8.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        BrandingSection(
                            title = strings.title,
                            subtitle = "Connect with Souls"
                        )

                        LanguageSelector(
                            currentLanguage = language,
                            expanded = languageMenuExpanded,
                            onExpandedChange = {
                                languageMenuExpanded = it
                            },
                            onLanguageSelected = {
                                language = it
                                languageMenuExpanded = false
                            }
                        )
                    }
                }
            },

            /*
             * 하단 네비게이션
             */
            bottomBar = {

                SoulNavigationBar(
                    strings = strings,
                    onHome = onNavigateToHome,
                    onExplore = onNavigateToExplore,
                    onMatches = onNavigateToMatches,
                    onProfile = onNavigateToProfile
                )
            }

        ) { paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),

                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = 100.dp
                ),

                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = activeChats,
                    key = { user -> user.id }
                ) { user ->

                    ChatListItem(
                        user = user,
                        prefix = strings.lastMsgPrefix,
                        onChatClick = onChatClick
                    )
                }
            }
        }
    }
}


/**
 * 하단 네비게이션
 */
@Composable
private fun SoulNavigationBar(
    strings: ChatListStrings,
    onHome: () -> Unit,
    onExplore: () -> Unit,
    onMatches: () -> Unit,
    onProfile: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = 28.dp,
                    topEnd = 28.dp
                )
            ),

        color = Color.White.copy(alpha = 0.08f),

        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.12f)
        )
    ) {

        NavigationBar(
            modifier = Modifier.height(80.dp),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {

            val navItems = listOf(
                Triple(
                    Icons.Default.Home,
                    strings.home,
                    false
                ),
                Triple(
                    Icons.Default.Search,
                    strings.explore,
                    false
                ),
                Triple(
                    Icons.Default.Favorite,
                    strings.matches,
                    false
                ),
                Triple(
                    Icons.AutoMirrored.Filled.Chat,
                    strings.chat,
                    true
                ),
                Triple(
                    Icons.Default.Person,
                    strings.profile,
                    false
                )
            )

            navItems.forEach { (icon, label, selected) ->

                NavigationBarItem(

                    selected = selected,

                    onClick = {

                        if (!selected) {

                            when (label) {

                                strings.home -> onHome()

                                strings.explore -> onExplore()

                                strings.matches -> onMatches()

                                strings.profile -> onProfile()
                            }
                        }
                    },

                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            modifier = Modifier.size(26.dp)
                        )
                    },

                    label = {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (selected) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    },

                    colors = NavigationBarItemDefaults.colors(

                        selectedIconColor =
                            MaterialTheme.colorScheme.primary,

                        selectedTextColor =
                            MaterialTheme.colorScheme.primary,

                        unselectedIconColor =
                            Color.White.copy(alpha = 0.5f),

                        unselectedTextColor =
                            Color.White.copy(alpha = 0.5f),

                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}


/**
 * 채팅 목록 Item
 */
@Composable
private fun ChatListItem(
    user: User,
    prefix: String,
    onChatClick: (String) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(24.dp)
            )
            .background(
                Color.White.copy(alpha = 0.07f)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable {
                onChatClick(user.id)
            }
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            /*
             * 사용자头像
             */
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF00D0D9),
                                Color(0xFF7E57C2)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = user.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )

                    Text(
                        text = "12:30 PM",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                val displayMsg = if (user.bio.isNotBlank() && user.bio != "SOUL User") user.bio else "대화를 시작해보세요!"
                Text(
                    text = "${prefix}$displayMsg",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 1
                )
            }
        }
    }
}


/**
 * 채팅 화면文字
 */
private data class ChatListStrings(
    val title: String,
    val home: String,
    val explore: String,
    val matches: String,
    val chat: String,
    val profile: String,
    val lastMsgPrefix: String
)