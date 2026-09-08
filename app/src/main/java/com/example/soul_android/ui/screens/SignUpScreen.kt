package com.example.soul_android.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.components.*
import kotlinx.coroutines.delay

private data class SignUpStrings(
    val title: String,
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val signUpBtn: String,
    val loginLink: String
)

@Composable
fun SignUpScreen(
    onBackToLogin: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    val strings = when (language) {
        AppLanguage.KOREAN -> SignUpStrings("회원가입", "이름", "이메일", "비밀번호", "비밀번호 확인", "회원가입", "이미 계정이 있으신가요? 로그인")
        AppLanguage.ENGLISH -> SignUpStrings("Sign Up", "Name", "Email", "Password", "Confirm Password", "Sign Up", "Already have an account? Log In")
        AppLanguage.CHINESE -> SignUpStrings("注册", "姓名", "邮箱", "密码", "确认密码", "注册", "已经有账号？登录")
    }

    if (isLoading) {
        LaunchedEffect(Unit) {
            delay(1500)
            onSignUpSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundGalaxy()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                LanguageSelector(
                    currentLanguage = language,
                    expanded = languageMenuExpanded,
                    onExpandedChange = { languageMenuExpanded = it },
                    onLanguageSelected = { language = it }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
            BrandingSection(title = strings.title, subtitle = "Start Your Journey")
            Spacer(modifier = Modifier.height(40.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp)),
                color = Color.White.copy(alpha = 0.07f)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    SoulTextField(value = name, onValueChange = { name = it }, placeholder = strings.name)
                    Spacer(modifier = Modifier.height(16.dp))
                    SoulTextField(value = email, onValueChange = { email = it }, placeholder = strings.email)
                    Spacer(modifier = Modifier.height(16.dp))
                    SoulTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = strings.password,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onTogglePassword = { passwordVisible = !passwordVisible }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SoulTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = strings.confirmPassword,
                        isPassword = true,
                        passwordVisible = confirmPasswordVisible,
                        onTogglePassword = { confirmPasswordVisible = !confirmPasswordVisible }
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    SoulButton(
                        text = strings.signUpBtn,
                        onClick = {
                            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && password == confirmPassword) {
                                isLoading = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onBackToLogin) {
                Text(
                    text = strings.loginLink, 
                    color = Color.White.copy(alpha = 0.8f),
                    style = TextStyle(letterSpacing = 1.sp, fontWeight = FontWeight.Light)
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
        }

        if (isLoading) {
            PlanetLoadingOverlay("Creating Your Soul ID...")
        }
    }
}
