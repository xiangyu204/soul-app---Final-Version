package com.example.soul_android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.models.AppLanguage

val SoulGreen = Color(0xFF4CAF70)

@Composable
fun SoulButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = SoulGreen,
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SkillChip(
    text: String,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = { onSelectedChange(!selected) },
        label = { Text(text) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = SoulGreen,
            selectedLabelColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun LanguageSelector(
    currentLanguage: AppLanguage,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    Box {
        TextButton(onClick = { onExpandedChange(true) }) {
            val langText = when (currentLanguage) {
                AppLanguage.KOREAN -> "한국어"
                AppLanguage.ENGLISH -> "English"
                AppLanguage.CHINESE -> "中文"
            }
            Text("🌐 $langText", color = SoulGreen)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            DropdownMenuItem(text = { Text("한국어") }, onClick = { onLanguageSelected(AppLanguage.KOREAN); onExpandedChange(false) })
            DropdownMenuItem(text = { Text("English") }, onClick = { onLanguageSelected(AppLanguage.ENGLISH); onExpandedChange(false) })
            DropdownMenuItem(text = { Text("中文") }, onClick = { onLanguageSelected(AppLanguage.CHINESE); onExpandedChange(false) })
        }
    }
}
