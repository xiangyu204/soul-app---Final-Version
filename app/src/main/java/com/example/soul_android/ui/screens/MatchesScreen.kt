package com.example.soul_android.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soul_android.data.network.MatchUserResponse
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.models.User
import com.example.soul_android.ui.components.*
import com.example.soul_android.ui.viewmodels.MatchUiState
import com.example.soul_android.ui.viewmodels.MatchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    onUserClick: (String) -> Unit = {},
    onChatClick: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToExplore: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onReviewClick: (String) -> Unit = {},
    matchViewModel: MatchViewModel = viewModel()
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showMatchDialog by remember { mutableStateOf(false) }
    var lastMatchedUser by remember { mutableStateOf<User?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val currentUsername = remember(context) {
        val soulPrefs = context.getSharedPreferences("soul_login_prefs", Context.MODE_PRIVATE)
        val userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        soulPrefs.getString("username", "")?.takeIf { it.isNotBlank() }
            ?: userPrefs.getString("username", "")?.takeIf { it.isNotBlank() }
            ?: ""
    }

    val matchState by matchViewModel.uiState.collectAsState()

    LaunchedEffect(currentUsername) {
        if (currentUsername.isNotBlank()) {
            matchViewModel.findMatches(username = currentUsername)
        }
    }

    fun toUser(resp: MatchUserResponse): User = User(
        id = resp.username,
        name = resp.name,
        bio = resp.nationality ?: "Soul User",
        languages = emptyList(),
        teachSkills = resp.skillOffer ?: emptyList(),
        learnSkills = resp.skillWant ?: emptyList(),
        matchRate = ((resp.averageRating ?: 4.0) * 20).toInt().coerceIn(0, 100),
        isOnline = true
    )

    val recommendedUsers = (matchState as? MatchUiState.Success)
        ?.recommendedUsers?.map(::toUser).orEmpty()
    val myMatches = (matchState as? MatchUiState.Success)
        ?.myMatches?.map(::toUser).orEmpty()

    val strings = MatchesStrings(
        title = "매칭",
        receivedTab = "추천",
        myMatchesTab = "내 매칭",
        accept = "매칭",
        reject = "건너뛰기",
        chat = "채팅하기",
        teachPrefix = "교류 가능한 스킬",
        learnPrefix = "관심 스킬",
        matchRate = "매칭률: ",
        home = "홈",
        explore = "탐색",
        matches = "매칭",
        chatLabel = "채팅",
        profile = "프로필",
        acceptedMsg = "새로운 매칭이 탄생했습니다!",
        rejectedMsg = "새로운 소울이 연결되었어요. 지금 바로 대화를 시작해보세요!"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundGalaxy()
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = { BrandingSection(title = strings.title, subtitle = "Your Connections", titleSize = 20) },
                    actions = {
                        LanguageSelector(
                            currentLanguage = language,
                            expanded = languageMenuExpanded,
                            onExpandedChange = { languageMenuExpanded = it },
                            onLanguageSelected = { language = it; languageMenuExpanded = false }
                        )
                    }
                )
            },
            bottomBar = {
                SoulNavigationBar(strings, onNavigateToHome, onNavigateToExplore, onNavigateToChat, onNavigateToProfile)
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White.copy(alpha = 0.02f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {},
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text(strings.receivedTab, fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal, color = if (selectedTabIndex == 0) Color.White else Color.White.copy(alpha = 0.5f)) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text(strings.myMatchesTab, fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal, color = if (selectedTabIndex == 1) Color.White else Color.White.copy(alpha = 0.5f)) }
                    )
                }

                when (val state = matchState) {
                    MatchUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF00D0D9))
                    }
                    is MatchUiState.Error -> MatchEmptyState(state.message)
                    is MatchUiState.Success -> {
                        if (selectedTabIndex == 0) {
                            if (recommendedUsers.isEmpty()) {
                                MatchEmptyState(if (language == AppLanguage.CHINESE) "暂无推荐用户" else if (language == AppLanguage.ENGLISH) "No recommendations yet" else "추천할 사용자가 없습니다")
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                                    contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(recommendedUsers, key = { it.id }) { user ->
                                        ReceivedRequestCard(
                                            user = user,
                                            strings = strings,
                                            onUserClick = onUserClick,
                                            onAccept = {
                                                matchViewModel.acceptMatch(
                                                    currentUsername = currentUsername,
                                                    targetUsername = user.id,
                                                    onSuccess = {
                                                        lastMatchedUser = user
                                                        showMatchDialog = true
                                                    },
                                                    onError = { errorMessage = it }
                                                )
                                            },
                                            onReject = { matchViewModel.rejectLocally(user.id) }
                                        )
                                    }
                                }
                            }
                        } else {
                            if (myMatches.isEmpty()) {
                                MatchEmptyState(if (language == AppLanguage.CHINESE) "暂无已匹配用户" else if (language == AppLanguage.ENGLISH) "No matches yet" else "매칭된 소울러가 없습니다")
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                                    contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(myMatches, key = { it.id }) { user ->
                                        MyMatchCard(user, strings, onUserClick, onChatClick, onReviewClick)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showMatchDialog && lastMatchedUser != null) {
            ItsAMatchDialog(
                user = lastMatchedUser!!,
                strings = strings,
                onDismiss = { showMatchDialog = false },
                onChatClick = {
                    showMatchDialog = false
                    onChatClick(it)
                }
            )
        }

        if (errorMessage != null) {
            AlertDialog(
                onDismissRequest = { errorMessage = null },
                title = { Text("매칭 실패") },
                text = { Text(errorMessage.orEmpty()) },
                confirmButton = { TextButton(onClick = { errorMessage = null }) { Text("확인") } }
            )
        }
    }
}

@Composable
private fun MatchEmptyState(message: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Restart), label = "p1"
    )
    val pulseAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Restart), label = "a1"
    )
    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(3000, delayMillis = 1500, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Restart), label = "p2"
    )
    val pulseAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(3000, delayMillis = 1500, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Restart), label = "a2"
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = Color(0xFF00D0D9), radius = (size.minDimension / 2) * pulseScale1, alpha = pulseAlpha1, style = Stroke(2.dp.toPx()))
                    drawCircle(color = Color(0xFF7E57C2), radius = (size.minDimension / 2) * pulseScale2, alpha = pulseAlpha2, style = Stroke(2.dp.toPx()))
                }
                Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f)).border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.FavoriteBorder, null, tint = Color(0xFF00D0D9), modifier = Modifier.size(36.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = message, color = Color.White.copy(alpha = 0.6f), fontSize = 15.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun SoulNavigationBar(strings: MatchesStrings, onHome: () -> Unit, onExplore: () -> Unit, onChat: () -> Unit, onProfile: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp, modifier = Modifier.height(80.dp)) {
            val navItems = listOf(Triple(Icons.Default.Home, strings.home, false), Triple(Icons.Default.Search, strings.explore, false), Triple(Icons.Default.Favorite, strings.matches, true), Triple(Icons.AutoMirrored.Filled.Chat, strings.chatLabel, false), Triple(Icons.Default.Person, strings.profile, false))
            navItems.forEach { (icon, label, selected) ->
                NavigationBarItem(selected = selected, onClick = { if(!selected) when(label) { strings.home -> onHome(); strings.explore -> onExplore(); strings.chatLabel -> onChat(); strings.profile -> onProfile() } }, icon = { Icon(icon, null, modifier = Modifier.size(26.dp)) }, label = { Text(label, fontSize = 11.sp, fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary, selectedTextColor = MaterialTheme.colorScheme.primary, unselectedIconColor = Color.White.copy(alpha = 0.5f), unselectedTextColor = Color.White.copy(alpha = 0.5f), indicatorColor = Color.Transparent))
            }
        }
    }
}

@Composable
private fun ReceivedRequestCard(user: User, strings: MatchesStrings, onUserClick: (String) -> Unit, onAccept: () -> Unit, onReject: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Color.White.copy(alpha = 0.05f)).border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(28.dp)).clickable { onUserClick(user.id) }.padding(20.dp)) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2)))), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(30.dp), tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        Surface(color = Color(0xFF00D0D9).copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp)) {
                            Text("${user.matchRate}% Match", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 11.sp, color = Color(0xFF00D0D9), fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(user.bio, fontSize = 13.sp, color = Color.White.copy(alpha = 0.5f), maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
            Spacer(modifier = Modifier.height(14.dp))

            // Skills Badges Display
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (user.teachSkills.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(strings.teachPrefix, fontSize = 12.sp, color = Color.White.copy(alpha = 0.4f), modifier = Modifier.width(85.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            user.teachSkills.forEach { skill ->
                                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF00D0D9).copy(alpha = 0.1f)).border(1.dp, Color(0xFF00D0D9).copy(alpha = 0.2f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                    Text(skill, color = Color(0xFF00D0D9), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
                if (user.learnSkills.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(strings.learnPrefix, fontSize = 12.sp, color = Color.White.copy(alpha = 0.4f), modifier = Modifier.width(85.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            user.learnSkills.forEach { skill ->
                                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF7E57C2).copy(alpha = 0.1f)).border(1.dp, Color(0xFF7E57C2).copy(alpha = 0.2f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                    Text(skill, color = Color(0xFFB39DDB), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onReject, modifier = Modifier.weight(1f).height(46.dp), shape = RoundedCornerShape(23.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.06f), contentColor = Color.White.copy(alpha = 0.6f))) {
                    Text(strings.reject, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Button(onClick = onAccept, modifier = Modifier.weight(1.3f).height(46.dp), shape = RoundedCornerShape(23.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                    Text(strings.accept, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MyMatchCard(user: User, strings: MatchesStrings, onUserClick: (String) -> Unit, onChatClick: (String) -> Unit, onReviewClick: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(Color.White.copy(alpha = 0.05f)).border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(26.dp)).clickable { onUserClick(user.id) }.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2)))), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(30.dp), tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
                Spacer(modifier = Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    user.teachSkills.take(2).forEach { skill ->
                        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFF00D0D9).copy(alpha = 0.08f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text(skill, color = Color(0xFF00D0D9), fontSize = 11.sp)
                        }
                    }
                }
            }
            IconButton(onClick = { onReviewClick(user.id) }, modifier = Modifier.size(46.dp).clip(CircleShape).background(Color(0xFFFFC107).copy(alpha = 0.12f)).border(1.dp, Color(0xFFFFC107).copy(alpha = 0.2f), CircleShape)) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { onChatClick(user.id) }, modifier = Modifier.size(46.dp).clip(CircleShape).background(Color(0xFF00D0D9).copy(alpha = 0.12f)).border(1.dp, Color(0xFF00D0D9).copy(alpha = 0.2f), CircleShape)) {
                Icon(Icons.AutoMirrored.Filled.Chat, null, tint = Color(0xFF00D0D9), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun ItsAMatchDialog(
    user: User,
    strings: MatchesStrings,
    onDismiss: () -> Unit,
    onChatClick: (String) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1500, easing = SineWaveEasing), repeatMode = RepeatMode.Reverse), label = "g"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.88f)).clickable { onDismiss() }, contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp).clickable(enabled = false) {}) {

                // Animated Match Heart / Icon
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp).scale(glowScale)) {
                    Box(modifier = Modifier.size(140.dp).blur(30.dp).background(Color(0xFF00D0D9).copy(alpha = 0.25f), CircleShape))
                    Box(modifier = Modifier.size(100.dp).blur(25.dp).background(Color(0xFF7E57C2).copy(alpha = 0.25f), CircleShape))
                    Icon(Icons.Default.Favorite, null, tint = Color(0xFFFF4081), modifier = Modifier.size(80.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = strings.acceptedMsg, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = strings.rejectedMsg, fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f), textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp))

                Spacer(modifier = Modifier.height(40.dp))

                // Avatar connection graphic
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)).border(2.dp, Color.White, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Box(modifier = Modifier.width(40.dp).height(2.dp).background(Brush.horizontalGradient(listOf(Color.White, Color(0xFF00D0D9)))))
                    Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2)))).border(2.dp, Color(0xFF00D0D9), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                }
                Text(user.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp, modifier = Modifier.padding(top = 12.dp))

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = { onChatClick(user.id) },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D0D9), contentColor = Color.White)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.chat, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = onDismiss) {
                    Text(if(strings.title == "매칭") "계속 매칭하기" else if(strings.title == "Matches") "Keep Exploring" else "继续探索", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                }
            }
        }
    }
}

private val SineWaveEasing = Easing { fraction ->
    val radians = fraction * Math.PI * 2
    ((Math.sin(radians) + 1) / 2).toFloat()
}

private data class MatchesStrings(
    val title: String, val receivedTab: String, val myMatchesTab: String, val accept: String, val reject: String,
    val chat: String, val teachPrefix: String, val learnPrefix: String, val matchRate: String, val home: String,
    val explore: String, val matches: String, val chatLabel: String, val profile: String, val acceptedMsg: String, val rejectedMsg: String
)

