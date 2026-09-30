package com.example.soul_android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SoulCyan,
    secondary = SoulPurple,
    tertiary = SoulPink,
    background = SoulDarkBg,
    surface = SoulCardDark,
    onPrimary = SoulDarkBg,
    onSecondary = SoulTextWhite,
    onTertiary = SoulTextWhite,
    onBackground = SoulTextWhite,
    onSurface = SoulTextWhite,
)

private val LightColorScheme = lightColorScheme(
    primary = SoulCyan,
    secondary = SoulPurple,
    tertiary = SoulPink,
    background = SoulTextWhite,
    surface = SoulTextWhite,
    onPrimary = SoulTextWhite,
    onSecondary = SoulDarkBg,
    onTertiary = SoulDarkBg,
    onBackground = SoulDarkBg,
    onSurface = SoulDarkBg,
)

@Composable
fun SOUL_AndroidTheme(
    darkTheme: Boolean = false, // Force light theme by default to avoid dark/black screen issues
    content: @Composable () -> Unit
) {
    // Force dark theme for "Soul App" look if you want, 
    // but here we respect system theme while providing Soul colors.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
