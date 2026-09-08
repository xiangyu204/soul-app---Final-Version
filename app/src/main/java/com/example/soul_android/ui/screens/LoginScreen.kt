package com.example.soul_android.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.soul_android.R
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.components.LanguageSelector
import com.example.soul_android.ui.components.SoulButton
import com.example.soul_android.ui.components.SoulGreen

@Composable
fun LoginScreen(
    onSignUpClick: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    val strings = when (language) {
        AppLanguage.KOREAN -> LoginStrings("로그인", "이메일", "비밀번호", "비밀번호를 잊으셨나요?", "계정이 없으신가요? 회원가입")
        AppLanguage.ENGLISH -> LoginStrings("Login", "Email", "Password", "Forgot your password?", "Don't have an account? Sign Up")
        AppLanguage.CHINESE -> LoginStrings("登录", "邮箱", "密码", "忘记密码？", "还没有账号？注册")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Language Selector
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            LanguageSelector(
                currentLanguage = language,
                expanded = languageMenuExpanded,
                onExpandedChange = { languageMenuExpanded = it },
                onLanguageSelected = { language = it }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // SOUL Logo
        Image(
            painter = painterResource(id = R.drawable.soul_logo),
            contentDescription = "SOUL Logo",
            modifier = Modifier.fillMaxWidth().height(180.dp)
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(strings.email) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(strings.password) },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot Password
        TextButton(onClick = { }) {
            Text(text = strings.forgot, color = SoulGreen)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Login Button
        SoulButton(
            text = strings.login,
            onClick = onLoginSuccess
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Sign Up
        TextButton(onClick = onSignUpClick) {
            Text(text = strings.signUp, color = SoulGreen)
        }
    }
}

private data class LoginStrings(
    val login: String,
    val email: String,
    val password: String,
    val forgot: String,
    val signUp: String
)
