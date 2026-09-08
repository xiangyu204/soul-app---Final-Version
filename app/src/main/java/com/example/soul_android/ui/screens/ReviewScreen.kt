package com.example.soul_android.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.components.SoulButton
import com.example.soul_android.ui.components.SoulGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    userId: String,
    onBackClick: () -> Unit = {}
) {
    var language by remember { mutableStateOf(AppLanguage.KOREAN) }
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    val strings = when (language) {
        AppLanguage.KOREAN -> ReviewStrings(
            title = "리뷰 작성",
            ratingLabel = "평점",
            commentLabel = "후기",
            commentHint = "수업은 어떠셨나요? 후기를 남겨주세요.",
            submit = "제출",
            success = "리뷰가 제출되었습니다!"
        )
        AppLanguage.ENGLISH -> ReviewStrings(
            title = "Write a Review",
            ratingLabel = "Rating",
            commentLabel = "Comment",
            commentHint = "How was the exchange? Leave a review.",
            submit = "Submit",
            success = "Review submitted!"
        )
        AppLanguage.CHINESE -> ReviewStrings(
            title = "撰写评价",
            ratingLabel = "评分",
            commentLabel = "评价内容",
            commentHint = "交流体验如何？请留下您的评价。",
            submit = "提交",
            success = "评价已提交！"
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(text = "Review for $userId", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

            // Star Rating
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { rating = i }
                    )
                }
            }

            // Comment
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text(strings.commentLabel) },
                placeholder = { Text(strings.commentHint) },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            SoulButton(
                text = strings.submit,
                onClick = {
                    // Logic to submit review
                    onBackClick()
                }
            )
        }
    }
}

private data class ReviewStrings(
    val title: String,
    val ratingLabel: String,
    val commentLabel: String,
    val commentHint: String,
    val submit: String,
    val success: String
)
