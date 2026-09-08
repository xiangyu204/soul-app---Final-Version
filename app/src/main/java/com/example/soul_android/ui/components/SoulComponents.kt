package com.example.soul_android.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.R
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.theme.SoulCyan
import com.example.soul_android.ui.theme.SoulPurple
import kotlin.random.Random

val SoulGreen = SoulCyan

@Composable
fun BackgroundGalaxy() {
    val infiniteTransition = rememberInfiniteTransition(label = "universe")
    
    // 星云流动动画控制
    val nebulaMove by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "nebula"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F1115))) {
        // --- 1. 底层深邃星云 ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // 青色星云团
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SoulCyan.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(canvasWidth * (0.2f + 0.1f * nebulaMove), canvasHeight * (0.3f - 0.05f * nebulaMove)),
                    radius = canvasWidth * 0.8f
                ),
                radius = canvasWidth * 0.8f
            )

            // 紫色星云团
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SoulPurple.copy(alpha = 0.1f), Color.Transparent),
                    center = Offset(canvasWidth * (0.8f - 0.1f * nebulaMove), canvasHeight * (0.7f + 0.05f * nebulaMove)),
                    radius = canvasWidth * 0.9f
                ),
                radius = canvasWidth * 0.9f
            )
        }

        // --- 2. 动态繁星 (三层深度) ---
        val starCounts = listOf(40, 20, 10) // 远、中、近景星星数量
        starCounts.forEachIndexed { layer, count ->
            val stars = remember { List(count) { Offset(Random.nextFloat(), Random.nextFloat()) } }
            
            stars.forEach { pos ->
                val starAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.1f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(Random.nextInt(1000 + layer * 1000, 3000 + layer * 1000)),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "star_blink"
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.White.copy(alpha = starAlpha),
                        radius = (layer + 1).toFloat() * (Random.nextFloat() + 0.5f),
                        center = Offset(pos.x * size.width, pos.y * size.height)
                    )
                }
            }
        }
        
        // --- 3. 顶部微光滤镜 ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.2f))
                    )
                )
        )
    }
}

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(32.dp)),
        color = Color.White.copy(alpha = 0.06f)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

@Composable
fun SoulButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(27.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}

@Composable
fun SoulTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.35f), fontSize = 14.sp) },
        singleLine = true,
        shape = RoundedCornerShape(22.dp),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onTogglePassword) {
                    Icon(imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = Color.White.copy(alpha = 0.4f))
                }
            }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.03f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}

@Composable
fun BrandingSection(
    title: String = "SOUL PLANET",
    subtitle: String = "Find Your Universe",
    titleSize: Int = 26
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(100.dp).blur(35.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), CircleShape))
            Image(painter = painterResource(id = R.drawable.soul_logo), contentDescription = "SOUL", modifier = Modifier.height(85.dp), contentScale = ContentScale.Fit)
        }
        Text(text = title, color = Color.White, style = TextStyle(fontSize = titleSize.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 6.sp))
        Text(text = subtitle, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Normal, letterSpacing = 2.sp)
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
        label = { Text(text, fontSize = 13.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = Color.White,
            containerColor = Color.White.copy(alpha = 0.05f),
            labelColor = Color.White.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if(selected) Color.Transparent else Color.White.copy(alpha = 0.15f))
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
        IconButton(onClick = { onExpandedChange(true) }) {
            Icon(Icons.Default.Language, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }, modifier = Modifier.background(Color(0xFF1C1F26))) {
            val langs = listOf(AppLanguage.KOREAN to "한국어", AppLanguage.ENGLISH to "English", AppLanguage.CHINESE to "中文")
            langs.forEach { (lang, name) ->
                DropdownMenuItem(text = { Text(name, color = Color.White) }, onClick = { onLanguageSelected(lang); onExpandedChange(false) })
            }
        }
    }
}

@Composable
fun PlanetLoadingOverlay(text: String = "Finding Your Soulmate...") {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing)),
        label = "rotate"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        brush = Brush.sweepGradient(listOf(Color(0xFF00D0D9), Color(0xFF7E57C2), Color(0xFF00D0D9))),
                        startAngle = rotation, sweepAngle = 180f, useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    )
                }
                Icon(Icons.Default.Language, null, tint = Color.White, modifier = Modifier.size(50.dp))
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(text, color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, fontSize = 16.sp)
        }
    }
}

@Composable
fun SocialCircleIcon(iconRes: Int, color: Color) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = color.copy(alpha = 0.8f), modifier = Modifier.size(24.dp))
    }
}
