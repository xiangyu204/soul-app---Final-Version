package com.example.soul_android.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.R
import com.example.soul_android.data.DummyData
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.models.User
import com.example.soul_android.ui.components.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToExplore: () -> Unit = {},
    onNavigateToMatches: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onUserClick: (String) -> Unit = {}
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }
    val user = DummyData.currentUser
    val strings = when (language) {
        AppLanguage.KOREAN -> HomeStrings("안녕하세요, ${user.name}님!", "오늘의 추천 매칭입니다.", "추천 매칭", "매칭률", "홈", "탐색", "매칭", "채팅", "프로필")
        AppLanguage.ENGLISH -> HomeStrings("Hello, ${user.name}!", "Today's recommended matches.", "Recommended Matches", "Match Rate", "Home", "Explore", "Matches", "Chat", "Profile")
        AppLanguage.CHINESE -> HomeStrings("你好，${user.name}！", "今日推荐匹配。", "推荐匹配", "匹配率", "首页", "发现", "匹配", "聊天", "个人资料")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundGalaxy()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 优化后的 Logo 展示
                    Box(modifier = Modifier.size(45.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.1f)).border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))) {
                        Image(
                            painter = painterResource(id = R.drawable.soul_logo),
                            contentDescription = "SOUL",
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    
                    LanguageSelector(
                        currentLanguage = language,
                        expanded = languageMenuExpanded,
                        onExpandedChange = { languageMenuExpanded = it },
                        onLanguageSelected = { language = it }
                    )
                }
            },
            bottomBar = {
                SoulNavigationBar(strings, onNavigateToExplore, onNavigateToMatches, onNavigateToChat, onNavigateToProfile)
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                item {
                    Column {
                        Text(text = strings.welcome, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = strings.subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.5f))
                    }
                }

                item {
                    SectionHeader(title = if (language == AppLanguage.KOREAN) "온라인 소울러" else "Online Soulers")
                    Spacer(modifier = Modifier.height(16.dp))
                    Planet3D(users = DummyData.users, onUserClick = onUserClick)
                }

                item {
                    SectionHeader(title = strings.matchTitle)
                    Spacer(modifier = Modifier.height(16.dp))
                    RecommendedUsersRow(onUserClick)
                }

                item {
                    SectionHeader(title = if (language == AppLanguage.KOREAN) "나의 학습 현황" else "My Learning Status")
                    Spacer(modifier = Modifier.height(16.dp))
                    LearningStatusCard()
                }
                
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = 0.5.sp)
}

@Composable
fun Planet3D(users: List<User>, onUserClick: (String) -> Unit) {
    var rotation by remember { mutableFloatStateOf(0f) }
    var dragRotation by remember { mutableFloatStateOf(0f) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            rotation += 0.3f
            if (rotation >= 360f) rotation -= 360f
            delay(30)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White.copy(alpha = 0.04f)) // 调淡背景，增强通透感
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(32.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    dragRotation += dragAmount.x * 0.5f
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val planetRadius = 90f
            
            // 增强后的星球发光
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00D0D9).copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(centerX, centerY),
                    radius = planetRadius * 3f
                ),
                radius = planetRadius * 3f
            )

            // 星球本体 - 更丰富的渐变
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE0F7FA), Color(0xFF00D0D9), Color(0xFF1A237E)),
                    center = Offset(centerX - 15f, centerY - 15f),
                    radius = planetRadius
                ),
                radius = planetRadius,
                center = Offset(centerX, centerY)
            )
        }

        if (users.isNotEmpty()) {
            users.forEachIndexed { index, user ->
                val baseAngle = 360f / users.size * index
                val angle = baseAngle + rotation + dragRotation
                val radians = Math.toRadians(angle.toDouble())
                val orbitRadius = 135f
                val x = cos(radians).toFloat() * orbitRadius
                val y = sin(radians).toFloat() * orbitRadius * 0.45f
                val z = sin(radians).toFloat()
                val scale = 0.75f + ((z + 1f) / 2f) * 0.45f
                val alpha = 0.4f + ((z + 1f) / 2f) * 0.6f

                Box(
                    modifier = Modifier.align(Alignment.Center).offset(x = x.dp, y = y.dp).size((46 * scale).dp).alpha(alpha)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2))))
                        .border(1.5.dp * scale, Color.White.copy(alpha = 0.3f), CircleShape)
                        .clickable { selectedUser = user },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size((26 * scale).dp))
                }
            }
        }

        selectedUser?.let { user ->
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.Black.copy(alpha = 0.4f), // 增加暗度以突出文字
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFF00D0D9)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        Text("${user.matchRate}% Match", color = Color(0xFF00D0D9), fontSize = 11.sp)
                    }
                    IconButton(onClick = { onUserClick(user.id) }) {
                        Icon(Icons.AutoMirrored.Filled.Chat, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = { selectedUser = null }) {
                        Icon(Icons.Default.Close, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendedUsersRow(onUserClick: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(end = 20.dp)) {
        items(DummyData.users) { user ->
            UserMatchCard(user, onUserClick)
        }
    }
}

@Composable
fun UserMatchCard(user: User, onUserClick: (String) -> Unit) {
    Box(
        modifier = Modifier.width(140.dp).height(190.dp).clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.05f)) // 更透亮的玻璃效果
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(28.dp))
            .clickable { onUserClick(user.id) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape)
                    .background(Brush.linearGradient(colors = listOf(Color(0xFF00D0D9), Color(0xFF7E57C2))))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(user.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
            Text(user.teachSkills.firstOrNull() ?: "", color = Color(0xFF00D0D9), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(color = Color(0xFF00D0D9).copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp)) {
                Text("${user.matchRate}%", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, color = Color(0xFF00D0D9), fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun LearningStatusCard() {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(colors = listOf(Color(0xFF7E57C2).copy(alpha = 0.3f), Color(0xFF00D0D9).copy(alpha = 0.3f))))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(28.dp))
    ) {
        Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Python Programming", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text("Matching with Sarah", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
            }
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(progress = { 0.7f }, color = Color(0xFF00D0D9), strokeWidth = 5.dp, trackColor = Color.White.copy(alpha = 0.1f))
                Text("70%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SoulNavigationBar(
    strings: HomeStrings,
    onExplore: () -> Unit,
    onMatches: () -> Unit,
    onChat: () -> Unit,
    onProfile: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(80.dp)
        ) {
            val navItems = listOf(
                Triple(Icons.Default.Home, strings.home, true),
                Triple(Icons.Default.Search, strings.explore, false),
                Triple(Icons.Default.Favorite, strings.matches, false),
                Triple(Icons.AutoMirrored.Filled.Chat, strings.chat, false),
                Triple(Icons.Default.Person, strings.profile, false)
            )
            navItems.forEach { (icon, label, selected) ->
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            when(label) {
                                strings.explore -> onExplore()
                                strings.matches -> onMatches()
                                strings.chat -> onChat()
                                strings.profile -> onProfile()
                            }
                        }
                    },
                    icon = { Icon(icon, null, modifier = Modifier.size(26.dp)) },
                    label = { Text(label, fontSize = 11.sp, fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
                        unselectedTextColor = Color.White.copy(alpha = 0.5f),
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

private data class HomeStrings(
    val welcome: String, val subtitle: String, val matchTitle: String, val matchRate: String,
    val home: String, val explore: String, val matches: String, val chat: String, val profile: String
)
