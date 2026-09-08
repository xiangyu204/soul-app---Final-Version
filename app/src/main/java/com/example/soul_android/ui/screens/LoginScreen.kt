package com.example.soul_android.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.soul_android.R
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.components.*
import kotlinx.coroutines.delay

private data class LoginStrings(
    val login: String,
    val email: String,
    val password: String,
    val forgot: String,
    val signUp: String,
    val orLoginWith: String
)

@Composable
fun LoginScreen(
    onSignUpClick: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    val strings = when (language) {
        AppLanguage.KOREAN -> LoginStrings("로그인", "이메일", "비밀번호", "비밀번호를 잊으셨나요?", "계정이 없으신가요? 회원가입", "또는 다음으로 로그인")
        AppLanguage.ENGLISH -> LoginStrings("Login", "Email", "Password", "Forgot your password?", "Don't have an account? Sign Up", "Or login with")
        AppLanguage.CHINESE -> LoginStrings("登录", "邮箱", "密码", "忘记密码？", "还没有账号？注册", "或其他方式登录")
    }

    if (isLoading) {
        LaunchedEffect(Unit) {
            delay(2000)
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundGalaxy()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
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

            Spacer(modifier = Modifier.weight(0.3f))
            BrandingSection()
            Spacer(modifier = Modifier.weight(0.5f))

            GlassLoginPanel(
                email = email,
                password = password,
                passwordVisible = passwordVisible,
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onTogglePassword = { passwordVisible = !passwordVisible },
                strings = strings,
                onLoginClick = { if (email.isNotBlank() && password.isNotBlank()) isLoading = true }
            )

            Spacer(modifier = Modifier.height(24.dp))
            SocialLoginSection(strings)
            Spacer(modifier = Modifier.weight(0.2f))

            TextButton(onClick = onSignUpClick) {
                Text(
                    text = strings.signUp, 
                    color = Color.White.copy(alpha = 0.8f),
                    style = TextStyle(letterSpacing = 1.sp, fontWeight = FontWeight.Light)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (isLoading) {
            PlanetLoadingOverlay()
        }
    }
}

@Composable
private fun GlassLoginPanel(
    email: String,
    password: String,
    passwordVisible: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    strings: LoginStrings,
    onLoginClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp)),
        color = Color.White.copy(alpha = 0.07f)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            SoulTextField(value = email, onValueChange = onEmailChange, placeholder = strings.email)
            Spacer(modifier = Modifier.height(16.dp))
            SoulTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = strings.password,
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = onTogglePassword
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = {}) {
                    Text(strings.forgot, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            SoulButton(text = strings.login, onClick = onLoginClick, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun SocialLoginSection(strings: LoginStrings) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = strings.orLoginWith, color = Color.White.copy(alpha = 0.3f), fontSize = 11.sp, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            SocialCircleIcon(R.drawable.soul_logo, Color.White)
            SocialCircleIcon(R.drawable.soul_logo, Color(0xFF07C160))
        }
    }
}
