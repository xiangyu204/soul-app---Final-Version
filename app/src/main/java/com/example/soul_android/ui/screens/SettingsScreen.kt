package com.example.soul_android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.components.SoulGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    val strings = when (language) {
        AppLanguage.KOREAN -> SettingsStrings(
            title = "설정",
            notifications = "알림",
            darkMode = "다크 모드",
            language = "언어",
            logout = "로그아웃"
        )
        AppLanguage.ENGLISH -> SettingsStrings(
            title = "Settings",
            notifications = "Notifications",
            darkMode = "Dark Mode",
            language = "Language",
            logout = "Logout"
        )
        AppLanguage.CHINESE -> SettingsStrings(
            title = "设置",
            notifications = "通知",
            darkMode = "深色模式",
            language = "语言",
            logout = "登出"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = strings.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = strings.notifications,
                trailing = {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = SoulGreen, checkedTrackColor = SoulGreen.copy(alpha = 0.5f))
                    )
                }
            )
            
            SettingsItem(
                icon = Icons.Default.DarkMode,
                title = strings.darkMode,
                trailing = {
                    Switch(
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = SoulGreen, checkedTrackColor = SoulGreen.copy(alpha = 0.5f))
                    )
                }
            )

            SettingsItem(
                icon = Icons.Default.Language,
                title = strings.language,
                trailing = {
                    Text(
                        text = when (language) {
                            AppLanguage.KOREAN -> "한국어"
                            AppLanguage.ENGLISH -> "English"
                            AppLanguage.CHINESE -> "中文"
                        },
                        color = SoulGreen
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(
                onClick = onLogoutClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = strings.logout, color = androidx.compose.ui.graphics.Color.Red, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = SoulGreen)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 16.sp)
        }
        trailing()
    }
}

private data class SettingsStrings(
    val title: String,
    val notifications: String,
    val darkMode: String,
    val language: String,
    val logout: String
)
