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

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchProfile("xiangyu")
    }

    val strings = ProfileStrings(
        title = "개인정보 페이지",
        btnEdit = "정보 수정",
        btnHome = "홈",
        btnLogout = "로그아웃",
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

            // Main Info Card - Using Project Standard GlassPanel
            GlassPanel(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                // Profile Header in Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // Standard Soul Glow
                        Box(modifier = Modifier.size(90.dp).blur(30.dp).background(SoulPurple.copy(alpha = 0.3f), CircleShape))
                        Image(
                            painter = painterResource(id = R.drawable.soul_logo), 
                            contentDescription = null,
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, SoulCyan.copy(alpha = 0.5f), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        val user = (uiState as? ProfileUiState.Success)?.data
                        Text(text = user?.name ?: "Loading...", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = "id:${user?.username ?: ""}", fontSize = 13.sp, color = SoulCyan.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = strings.welcome, fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (uiState is ProfileUiState.Success) {
                    val user = (uiState as ProfileUiState.Success).data
                    
                    InfoRow(strings.labelId, user.username, strings.labelAge, user.age)
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 14.dp))
                    
                    InfoRow(strings.labelGender, user.gender, strings.labelNationality, user.nationality)
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 14.dp))
                    
                    InfoRow(strings.labelPhone, user.phone, "", "")
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 14.dp))
                    
                    InfoRow(strings.labelEmail, user.email, "", "")
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 14.dp))
                    
                    SkillInfoRow(strings.labelTeach, user.teachSkills.firstOrNull() ?: "-", user.skillOfferLevel)
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 14.dp))
                    
                    SkillInfoRow(strings.labelLearn, user.learnSkills.firstOrNull() ?: "-", user.skillWantLevel)
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 14.dp))
                    
                    InfoRow(strings.labelTime, user.timeSlot, "", "")
                }
                
                Spacer(modifier = Modifier.height(32.dp))

                // Standard Soul Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SoulButton(
                        text = strings.btnEdit,
                        onClick = onEditProfileClick,
                        modifier = Modifier.weight(1f),
                        containerColor = Color(0xFF4C6FFF)
                    )
                    
                    Button(
                        onClick = onNavigateToHome,
                        modifier = Modifier.size(54.dp),
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Home, null, tint = Color.White)
                    }
                    
                    SoulButton(
                        text = strings.btnLogout,
                        onClick = onLogoutClick,
                        modifier = Modifier.weight(1f),
                        containerColor = Color(0xFFE53935)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(label1: String, value1: String, label2: String, value2: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = label1, fontSize = 13.sp, color = Color.White.copy(alpha = 0.4f), modifier = Modifier.width(70.dp))
                Text(text = value1, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
        if (label2.isNotEmpty()) {
            Column(modifier = Modifier.weight(0.9f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = label2, fontSize = 13.sp, color = Color.White.copy(alpha = 0.4f), modifier = Modifier.width(40.dp))
                    Text(text = value2, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SkillInfoRow(label: String, skill: String, level: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontSize = 13.sp, color = Color.White.copy(alpha = 0.4f), modifier = Modifier.width(100.dp))
        Text(text = skill, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.width(12.dp))
        Surface(
            color = SoulCyan.copy(alpha = 0.15f),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(0.5.dp, SoulCyan.copy(alpha = 0.3f))
        ) {
            Text(
                text = level,
                fontSize = 10.sp,
                color = SoulCyan,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

private data class ProfileStrings(
    val title: String, val btnEdit: String, val btnHome: String, val btnLogout: String,
    val labelId: String, val labelAge: String, val labelGender: String, val labelNationality: String,
    val labelPhone: String, val labelEmail: String, val labelAddress: String,
    val labelTeach: String, val labelLearn: String, val labelTime: String, val labelProjects: String,
    val welcome: String
)

