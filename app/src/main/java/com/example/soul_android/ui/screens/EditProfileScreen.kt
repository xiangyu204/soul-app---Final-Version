package com.example.soul_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.components.*
import com.example.soul_android.ui.theme.SoulCyan
import com.example.soul_android.ui.theme.SoulPurple
import com.example.soul_android.ui.viewmodels.ProfileUiData
import com.example.soul_android.ui.viewmodels.ProfileUiState
import com.example.soul_android.ui.viewmodels.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    val uiState by viewModel.uiState.collectAsState()

    val strings = when (language) {
        AppLanguage.KOREAN -> EditProfileStrings("정보 수정", "저장", "성명", "전화번호", "주소", "학습 시간대", "나이", "성별", "국적")
        AppLanguage.ENGLISH -> EditProfileStrings("Edit Info", "Save", "Name", "Phone", "Address", "Time Slot", "Age", "Gender", "Nationality")
        AppLanguage.CHINESE -> EditProfileStrings("修改信息", "保存", "姓名", "电话", "地址", "学习时间", "年龄", "性别", "国籍")
    }

    // Local form state
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var timeSlot by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }

    // Initialize form with current data
    LaunchedEffect(Unit) {
        viewModel.fetchProfile("xiangyu")
    }
    
    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Success) {
            val user = (uiState as ProfileUiState.Success).data
            name = user.name
            phone = user.phone
            address = user.address
            timeSlot = user.timeSlot
            age = user.age
            gender = user.gender
            nationality = user.nationality
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundGalaxy()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header with Back Button
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                
                Text(
                    text = strings.title,
                    style = TextStyle(
                        brush = Brush.horizontalGradient(listOf(SoulPurple, SoulCyan)),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        letterSpacing = 2.sp
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            GlassPanel(modifier = Modifier.padding(horizontal = 20.dp)) {
                EditField(strings.labelName, name) { name = it }
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        EditField(strings.labelAge, age) { age = it }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        EditField(strings.labelGender, gender) { gender = it }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                EditField(strings.labelPhone, phone) { phone = it }
                Spacer(modifier = Modifier.height(16.dp))
                EditField(strings.labelNationality, nationality) { nationality = it }
                Spacer(modifier = Modifier.height(16.dp))
                EditField(strings.labelAddress, address) { address = it }
                Spacer(modifier = Modifier.height(16.dp))
                EditField(strings.labelTime, timeSlot) { timeSlot = it }
                
                Spacer(modifier = Modifier.height(32.dp))

                SoulButton(
                    text = strings.btnSave,
                    onClick = { 
                        if (uiState is ProfileUiState.Success) {
                            val currentUser = (uiState as ProfileUiState.Success).data
                            val updated = currentUser.copy(
                                name = name,
                                phone = phone,
                                address = address,
                                timeSlot = timeSlot,
                                age = age,
                                gender = gender,
                                nationality = nationality
                            )
                            viewModel.updateProfile(updated)
                        }
                        onBackClick() 
                    },
                    containerColor = SoulCyan
                )
            }
        }
    }
}

@Composable
private fun EditField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label, 
            color = Color.White.copy(alpha = 0.7f), 
            fontSize = 14.sp, 
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        SoulTextField(value = value, onValueChange = onValueChange, placeholder = "Enter $label")
    }
}

private data class EditProfileStrings(
    val title: String, val btnSave: String,
    val labelName: String, val labelPhone: String, val labelAddress: String, val labelTime: String,
    val labelAge: String, val labelGender: String, val labelNationality: String
)
