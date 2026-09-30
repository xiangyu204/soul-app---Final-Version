package com.example.soul_android.ui.screens


import android.content.Context

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Quiz

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.soul_android.ui.viewmodels.AiQuizViewModel
import com.example.soul_android.ui.viewmodels.QuizUiState


@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun AiQuizScreen(

    onBackClick: () -> Unit = {},

    viewModel: AiQuizViewModel =
        viewModel()
) {


    // =================================================
    // Context
    // =================================================

    val context =
        LocalContext.current


    // =================================================
    // 获取登录 username
    // =================================================
    //
    // 暂时按照 SharedPreferences：
    //
    // user_prefs
    // username
    //
    // 如果你的登录保存方式不同，
    // 后面把这里换掉即可。
    // =================================================

    val sharedPreferences =
        remember {

            context.getSharedPreferences(
                "soul_login_prefs",
                Context.MODE_PRIVATE
            )
        }


    val username =
        sharedPreferences.getString(
            "username",
            ""
        ) ?: ""


    val isLoggedIn =
        sharedPreferences.getBoolean(
            "is_logged_in",
            false
        )


    // =================================================
    // UI State
    // =================================================

    var topic by remember {

        mutableStateOf(
            ""
        )
    }


    val uiState by
    viewModel
        .uiState
        .collectAsState()


    val scrollState =
        rememberScrollState()


    /*
     * Key   = questionId
     * Value = 选项位置 0 / 1 / 2 / 3
     */
    val userSelections =
        remember {

            mutableStateMapOf<
                    Long,
                    Int
                    >()
        }


    // =================================================
    // 推荐主题
    // =================================================

    val presetTopics =
        listOf(

            "영어 회화",

            "파이썬",

            "한국어 맞춤법",

            "안드로이드"
        )


    // =================================================
    // Scaffold
    // =================================================

    Scaffold(

        containerColor =
            Color(
                0xFFF4F6F9
            ),


        topBar = {

            TopAppBar(

                title = {

                    Text(

                        text =
                            "AI 맞춤형 객관식 퀴즈",

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color(
                                0xFF1C1F26
                            )
                    )
                },


                navigationIcon = {

                    IconButton(

                        onClick =
                            onBackClick

                    ) {

                        Icon(

                            imageVector =
                                Icons
                                    .AutoMirrored
                                    .Filled
                                    .ArrowBack,

                            contentDescription =
                                "Back",

                            tint =
                                Color(
                                    0xFF1C1F26
                                )
                        )
                    }
                },


                colors =
                    TopAppBarDefaults
                        .topAppBarColors(

                            containerColor =
                                Color.White
                        )
            )
        }

    ) { paddingValues ->


        Column(

            modifier =
                Modifier

                    .padding(
                        paddingValues
                    )

                    .fillMaxSize()

                    .verticalScroll(
                        scrollState
                    )

                    .padding(
                        20.dp
                    ),

            verticalArrangement =
                Arrangement.spacedBy(
                    20.dp
                )

        ) {


            // =================================================
            // Header
            // =================================================

            Surface(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        20.dp
                    ),

                color =
                    Color.Transparent

            ) {


                Box(

                    modifier =
                        Modifier

                            .fillMaxWidth()

                            .background(

                                Brush.linearGradient(

                                    colors =
                                        listOf(

                                            Color(
                                                0xFF00D0D9
                                            ),

                                            Color(
                                                0xFF7E57C2
                                            )
                                        )
                                )
                            )

                            .padding(
                                24.dp
                            )

                ) {


                    Column {


                        Text(

                            text =
                                "💡 AI 4지선다 객관식 퀴즈",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color.White
                        )


                        Spacer(

                            modifier =
                                Modifier.height(
                                    6.dp
                                )
                        )


                        Text(

                            text =
                                "원하는 학습 주제를 입력하면 AI가 5문제를 출제합니다. " +
                                        "보기를 선택하고 제출하여 테스트 결과를 확인하세요!",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,

                            color =
                                Color.White.copy(
                                    alpha = 0.9f
                                )
                        )
                    }
                }
            }


            // =================================================
            // Login information
            // =================================================

            if (
                username.isBlank()
            ) {

                Text(

                    text =
                        "⚠ 로그인이 필요합니다.",

                    color =
                        Color.Red,

                    fontWeight =
                        FontWeight.Bold
                )
            }


            // =================================================
            // Preset topics
            // =================================================

            Column(

                verticalArrangement =
                    Arrangement.spacedBy(
                        8.dp
                    )

            ) {


                Text(

                    text =
                        "추천 학습 주제",

                    style =
                        MaterialTheme
                            .typography
                            .labelLarge,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color.Gray
                )


                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )

                ) {


                    presetTopics.forEach { preset ->


                        SuggestionChip(

                            onClick = {


                                topic =
                                    preset


                                userSelections
                                    .clear()


                                viewModel
                                    .generateQuiz(

                                        topic =
                                            preset,

                                        username =
                                            username,

                                        targetLang =
                                            "ko",

                                        quizType =
                                            "learn"
                                    )
                            },


                            label = {

                                Text(

                                    text =
                                        preset,

                                    fontWeight =
                                        FontWeight.Medium
                                )
                            },


                            colors =
                                SuggestionChipDefaults
                                    .suggestionChipColors(

                                        containerColor =
                                            Color.White,

                                        labelColor =
                                            Color(
                                                0xFF7E57C2
                                            )
                                    )
                        )
                    }
                }
            }


            // =================================================
            // Topic input
            // =================================================

            OutlinedTextField(

                value =
                    topic,


                onValueChange = {

                    topic =
                        it
                },


                placeholder = {

                    Text(

                        text =
                            "직접 학습 주제를 입력하세요 " +
                                    "(예: 머신러닝, 비즈니스 영어)..."
                    )
                },


                modifier =
                    Modifier.fillMaxWidth(),


                shape =
                    RoundedCornerShape(
                        16.dp
                    ),


                colors =
                    OutlinedTextFieldDefaults
                        .colors(

                            focusedContainerColor =
                                Color.White,

                            unfocusedContainerColor =
                                Color.White,

                            focusedBorderColor =
                                Color(
                                    0xFF00D0D9
                                ),

                            unfocusedBorderColor =
                                Color
                                    .LightGray
                                    .copy(
                                        alpha = 0.5f
                                    )
                        )
            )


            // =================================================
            // Generate Quiz button
            // =================================================

            Button(

                onClick = {


                    userSelections
                        .clear()


                    viewModel
                        .generateQuiz(

                            topic =
                                topic,

                            username =
                                username,

                            targetLang =
                                "ko",

                            quizType =
                                "learn"
                        )
                },


                modifier =
                    Modifier

                        .fillMaxWidth()

                        .height(
                            54.dp
                        ),


                shape =
                    RoundedCornerShape(
                        16.dp
                    ),


                colors =
                    ButtonDefaults
                        .buttonColors(

                            containerColor =
                                Color(
                                    0xFF00D0D9
                                )
                        ),


                enabled =

                    uiState
                            !is
                            QuizUiState.Loading

                            &&

                            username.isNotBlank()

                            &&

                            topic.isNotBlank()

            ) {


                Icon(

                    imageVector =
                        Icons.Default.Quiz,

                    contentDescription =
                        null,

                    tint =
                        Color.White
                )


                Spacer(

                    modifier =
                        Modifier.width(
                            8.dp
                        )
                )


                Text(

                    text =
                        "AI 객관식 퀴즈 5문제 출제하기",

                    color =
                        Color.White,

                    fontSize =
                        16.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }


            // =================================================
            // State
            // =================================================

            when (
                val state =
                    uiState
            ) {


                // =============================================
                // Loading
                // =============================================

                is QuizUiState.Loading -> {


                    Box(

                        modifier =
                            Modifier

                                .fillMaxWidth()

                                .padding(
                                    60.dp
                                ),

                        contentAlignment =
                            Alignment.Center

                    ) {


                        CircularProgressIndicator(

                            color =
                                Color(
                                    0xFF00D0D9
                                )
                        )
                    }
                }


                // =============================================
                // Success
                // =============================================

                is QuizUiState.Success -> {


                    val answeredCount =
                        userSelections.size


                    val totalCount =
                        state.questions.size


                    val progress =

                        if (
                            totalCount == 0
                        ) {

                            0f

                        } else {

                            answeredCount
                                .toFloat()

                            totalCount
                                .toFloat()
                        }


                    // =========================================
                    // Progress bar
                    // =========================================

                    LinearProgressIndicator(

                        progress = {

                            progress
                        },


                        modifier =
                            Modifier

                                .fillMaxWidth()

                                .height(
                                    8.dp
                                )

                                .clip(

                                    RoundedCornerShape(
                                        4.dp
                                    )
                                ),


                        color =
                            Color(
                                0xFF7E57C2
                            ),


                        trackColor =
                            Color
                                .LightGray
                                .copy(
                                    alpha = 0.3f
                                )
                    )


                    Text(

                        text =
                            "진행 상황: " +
                                    "$answeredCount / " +
                                    "$totalCount 완료",

                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,

                        color =
                            Color.Gray
                    )


                    // =========================================
                    // Questions
                    // =========================================

                    state.questions
                        .forEachIndexed { questionIndex,
                                          q ->


                            Surface(

                                modifier =
                                    Modifier
                                        .fillMaxWidth(),

                                shape =
                                    RoundedCornerShape(
                                        18.dp
                                    ),

                                color =
                                    Color.White,

                                shadowElevation =
                                    3.dp

                            ) {


                                Column(

                                    modifier =
                                        Modifier.padding(
                                            20.dp
                                        )

                                ) {


                                    // 显示 1,2,3,4,5
                                    // 不直接显示数据库 questionId
                                    Text(

                                        text =
                                            "Q${questionIndex + 1}. " +
                                                    q.question,

                                        style =
                                            MaterialTheme
                                                .typography
                                                .titleMedium,

                                        fontWeight =
                                            FontWeight.Bold,

                                        color =
                                            Color(
                                                0xFF1C1F26
                                            )
                                    )


                                    Spacer(

                                        modifier =
                                            Modifier.height(
                                                14.dp
                                            )
                                    )


                                    // =================================
                                    // Options
                                    // =================================

                                    q.options
                                        .forEachIndexed { optIndex,
                                                          optionText ->


                                            val isSelected =

                                                userSelections[
                                                    q.questionId
                                                ] == optIndex


                                            Surface(

                                                modifier =
                                                    Modifier

                                                        .fillMaxWidth()

                                                        .padding(
                                                            vertical =
                                                                4.dp
                                                        )

                                                        .clip(

                                                            RoundedCornerShape(
                                                                12.dp
                                                            )
                                                        )

                                                        .clickable {

                                                            userSelections[
                                                                q.questionId
                                                            ] =
                                                                optIndex
                                                        },


                                                color =

                                                    if (
                                                        isSelected
                                                    ) {

                                                        Color(
                                                            0xFF00D0D9
                                                        )
                                                            .copy(
                                                                alpha =
                                                                    0.12f
                                                            )

                                                    } else {

                                                        Color(
                                                            0xFFF8F9FA
                                                        )
                                                    },


                                                shape =
                                                    RoundedCornerShape(
                                                        12.dp
                                                    ),


                                                border =
                                                    BorderStroke(

                                                        width =
                                                            1.dp,

                                                        color =

                                                            if (
                                                                isSelected
                                                            ) {

                                                                Color(
                                                                    0xFF00D0D9
                                                                )

                                                            } else {

                                                                Color.Transparent
                                                            }
                                                    )

                                            ) {


                                                Row(

                                                    modifier =
                                                        Modifier.padding(
                                                            14.dp
                                                        ),

                                                    verticalAlignment =
                                                        Alignment.CenterVertically

                                                ) {


                                                    RadioButton(

                                                        selected =
                                                            isSelected,


                                                        onClick = {

                                                            userSelections[
                                                                q.questionId
                                                            ] =
                                                                optIndex
                                                        },


                                                        colors =
                                                            RadioButtonDefaults
                                                                .colors(

                                                                    selectedColor =
                                                                        Color(
                                                                            0xFF00D0D9
                                                                        )
                                                                )
                                                    )


                                                    Spacer(

                                                        modifier =
                                                            Modifier.width(
                                                                8.dp
                                                            )
                                                    )


                                                    Text(

                                                        text =
                                                            "${optIndex + 1}) " +
                                                                    optionText,

                                                        style =
                                                            MaterialTheme
                                                                .typography
                                                                .bodyMedium,

                                                        color =

                                                            if (
                                                                isSelected
                                                            ) {

                                                                Color(
                                                                    0xFF00D0D9
                                                                )

                                                            } else {

                                                                Color(
                                                                    0xFF1C1F26
                                                                )
                                                            },


                                                        fontWeight =

                                                            if (
                                                                isSelected
                                                            ) {

                                                                FontWeight.Bold

                                                            } else {

                                                                FontWeight.Normal
                                                            }
                                                    )
                                                }
                                            }
                                        }
                                }
                            }
                        }


                    Spacer(

                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )


                    // =========================================
                    // Submit
                    // =========================================

                    Button(

                        onClick = {


                            viewModel
                                .submitAnswers(

                                    username =
                                        username,

                                    selections =
                                        userSelections
                                )
                        },


                        modifier =
                            Modifier

                                .fillMaxWidth()

                                .height(
                                    54.dp
                                ),


                        shape =
                            RoundedCornerShape(
                                16.dp
                            ),


                        colors =
                            ButtonDefaults
                                .buttonColors(

                                    containerColor =
                                        Color(
                                            0xFF7E57C2
                                        )
                                ),


                        enabled =

                            userSelections.size
                                    ==
                                    state.questions.size

                    ) {


                        Icon(

                            imageVector =
                                Icons.Default.CheckCircle,

                            contentDescription =
                                null,

                            tint =
                                Color.White
                        )


                        Spacer(

                            modifier =
                                Modifier.width(
                                    8.dp
                                )
                        )


                        Text(

                            text =
                                "제출하고 결과 확인하기",

                            color =
                                Color.White,

                            fontSize =
                                16.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }


                // =============================================
                // Graded
                // =============================================

                is QuizUiState.Graded -> {


                    Surface(

                        modifier =
                            Modifier
                                .fillMaxWidth(),

                        shape =
                            RoundedCornerShape(
                                20.dp
                            ),

                        color =
                            Color.White,

                        shadowElevation =
                            4.dp

                    ) {


                        Column(

                            modifier =
                                Modifier.padding(
                                    24.dp
                                ),

                            horizontalAlignment =
                                Alignment.CenterHorizontally

                        ) {


                            Text(

                                text =
                                    "🎉 퀴즈 채점 완료!",

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleLarge,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color(
                                        0xFF7E57C2
                                    )
                            )


                            Spacer(

                                modifier =
                                    Modifier.height(
                                        20.dp
                                    )
                            )


                            // score
                            Text(

                                text =
                                    "${state.score} / ${state.total}",

                                fontSize =
                                    34.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color(
                                        0xFF00D0D9
                                    )
                            )


                            Spacer(

                                modifier =
                                    Modifier.height(
                                        16.dp
                                    )
                            )


                        }
                    }


                    // =========================================
                    // Retry
                    // =========================================

                    Button(

                        onClick = {


                            userSelections
                                .clear()


                            viewModel
                                .generateQuiz(

                                    topic =
                                        topic,

                                    username =
                                        username,

                                    targetLang =
                                        "ko",

                                    quizType =
                                        "learn"
                                )
                        },


                        modifier =
                            Modifier

                                .fillMaxWidth()

                                .height(
                                    50.dp
                                ),


                        shape =
                            RoundedCornerShape(
                                16.dp
                            ),


                        colors =
                            ButtonDefaults
                                .buttonColors(

                                    containerColor =
                                        Color(
                                            0xFF00D0D9
                                        )
                                )

                    ) {


                        Text(

                            text =
                                "새로운 퀴즈 다시 풀기",

                            color =
                                Color.White,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }


                // =============================================
                // Error
                // =============================================

                is QuizUiState.Error -> {


                    Surface(

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(
                                12.dp
                            ),

                        color =
                            Color(
                                0xFFFFEBEE
                            )

                    ) {


                        Text(

                            text =
                                "오류 발생: ${state.message}",

                            color =
                                Color.Red,

                            modifier =
                                Modifier.padding(
                                    16.dp
                                )
                        )
                    }
                }


                QuizUiState.Idle -> {

                    // 什么都不显示
                }
            }
        }
    }
}