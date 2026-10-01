package com.example.soul_android.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.R
import com.example.soul_android.data.DummyData
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.components.*
import com.example.soul_android.ui.theme.SoulCyan
import com.example.soul_android.ui.theme.SoulPurple
import com.example.soul_android.ui.viewmodels.ProfileUiState
import com.example.soul_android.ui.viewmodels.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToExplore: () -> Unit = {},
    onNavigateToMatches: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }

    val context = LocalContext.current
    val currentUsername = remember {
        val prefs = context.getSharedPreferences("soul_login_prefs", android.content.Context.MODE_PRIVATE)
        prefs.getString("username", "")?.takeIf { it.isNotBlank() } 
            ?: context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE).getString("username", "")?.takeIf { it.isNotBlank() }
            ?: "xiangyu"
    }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(currentUsername) {
        viewModel.fetchProfile(currentUsername)
    }

    val strings = ProfileStrings(
        title = "개인정보 페이지",
        btnEdit = "정보 수정",
        btnHome = "홈",
        logout = "로그아웃",
        labelId = "아이디",
        labelAge = "나이",
        labelGender = "성별",
        labelNationality = "국적",
        labelPhone = "전화번호",
        labelEmail = "이메일",
        labelAddress = "주소",
        labelTeach = "가르칠 수 있는 기술",
        labelLearn = "배우고 싶은 기술",
        labelTime = "학습 시간대",
        labelProjects = "항목 / 상점",
        welcome = "개인센터에 오신 것을 환영합니다"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundGalaxy()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // Header Title
            Text(
                text = strings.title,
                style = TextStyle(
                    brush = Brush.horizontalGradient(listOf(SoulPurple, SoulCyan)),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 4.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
            )

            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SoulCyan)
                    }
                }
                is ProfileUiState.Success -> {
                    val profile = state.data
                    ProfileContent(profile, strings, onEditProfileClick, onSettingsClick, onLogoutClick)
                }
                is ProfileUiState.Error -> {
                    val fallbackProfile = com.example.soul_android.ui.viewmodels.ProfileUiData(
                        name = currentUsername,
                        username = currentUsername,
                        email = "$currentUsername@gmail.com",
                        avatar = null,
                        teachSkills = listOf("Java", "Spring"),
                        learnSkills = listOf("Python", "AI"),
                        rating = "★ 5.0",
                        nationality = "Korea",
                        age = "25",
                        gender = "M",
                        phone = "010-1234-5678",
                        address = "Seoul",
                        skillOfferLevel = "Advanced",
                        skillWantLevel = "Intermediate",
                        timeSlot = "평일 오전",
                        projects = "-"
                    )
                    ProfileContent(fallbackProfile, strings, onEditProfileClick, onSettingsClick, onLogoutClick)
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: com.example.soul_android.ui.viewmodels.ProfileUiData,
    strings: ProfileStrings,
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar Box
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(SoulCyan, SoulPurple)))
                .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = profile.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = "id:${profile.username}", fontSize = 14.sp, color = SoulCyan)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = strings.welcome, fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f))

        Spacer(modifier = Modifier.height(28.dp))

        // Info Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
            color = Color.White.copy(alpha = 0.05f)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoItem(strings.labelId, profile.username)
                    InfoItem(strings.labelAge, profile.age)
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoItem(strings.labelGender, profile.gender)
                    InfoItem(strings.labelNationality, profile.nationality)
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                InfoItem(strings.labelPhone, profile.phone)
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                InfoItem(strings.labelEmail, profile.email)
                
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(strings.labelTeach, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        profile.teachSkills.forEach { skill ->
                            Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(SoulCyan.copy(alpha = 0.15f)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                                Text(skill, color = SoulCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(strings.labelLearn, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        profile.learnSkills.forEach { skill ->
                            Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(SoulPurple.copy(alpha = 0.15f)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                                Text(skill, color = Color(0xFFD0BCFF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onEditProfileClick,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoulPurple)
            ) {
                Text(strings.btnEdit, fontWeight = FontWeight.Bold)
            }
            
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }

            Button(
                onClick = onLogoutClick,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text(strings.logout, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column {
        Text(text = label, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

data class ProfileStrings(
    val title: String,
    val btnEdit: String,
    val btnHome: String,
    val logout: String,
    val labelId: String,
    val labelAge: String,
    val labelGender: String,
    val labelNationality: String,
    val labelPhone: String,
    val labelEmail: String,
    val labelAddress: String,
    val labelTeach: String,
    val labelLearn: String,
    val labelTime: String,
    val labelProjects: String,
    val welcome: String
)
