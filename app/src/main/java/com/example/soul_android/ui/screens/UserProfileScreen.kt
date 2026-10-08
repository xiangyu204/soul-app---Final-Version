package com.example.soul_android.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.soul_android.data.network.ProfileResponse
import com.example.soul_android.data.network.SoulApiService
import com.example.soul_android.models.AppLanguage
import com.example.soul_android.ui.components.BackgroundGalaxy
import com.example.soul_android.ui.components.LanguageSelector
import com.example.soul_android.ui.components.SoulButton
import com.example.soul_android.ui.viewmodels.UserProfileUiState
import com.example.soul_android.ui.viewmodels.UserProfileViewModel


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun UserProfileScreen(
    userId: String,
    onBackClick: () -> Unit = {},
    onMatchRequestClick: (String) -> Unit = {},
    onChatClick: (String) -> Unit = {},
    profileViewModel: UserProfileViewModel = viewModel()
) {

    var language by remember {
        mutableStateOf(AppLanguage.KOREAN)
    }

    var languageMenuExpanded by remember {
        mutableStateOf(false)
    }

    val uiState by
    profileViewModel.uiState.collectAsState()


    /*
     * userId 现在实际上就是 username
     *
     * MatchesScreen 中：
     * User.id = resp.username
     *
     * 所以点击用户后传过来的 userId
     * 就可以直接用于查询后端用户资料
     */
    LaunchedEffect(userId) {

        if (userId.isNotBlank()) {
            profileViewModel.loadProfile(userId)
        }
    }


    val strings =
        when (language) {

            AppLanguage.KOREAN ->
                UserProfileStrings(
                    title = "프로필",
                    about = "자기소개",
                    teach = "가르칠 수 있는 스킬",
                    learn = "배우고 싶은 스킬",
                    matchRate = "매칭률",
                    requestBtn = "매칭 요청",
                    messageBtn = "메시지 보내기",

                    nationality = "국적",
                    gender = "성별",
                    age = "나이",
                    email = "이메일",
                    phone = "전화번호",
                    address = "주소",

                    teachLevel = "가르치기 레벨",
                    learnLevel = "학습 레벨",

                    rating = "평점",
                    noInfo = "사용자가 작성하지 않았습니다",

                    retry = "다시 시도",
                    loading = "프로필을 불러오는 중입니다"
                )


            AppLanguage.ENGLISH ->
                UserProfileStrings(
                    title = "Profile",
                    about = "About Me",
                    teach = "Skills I Can Teach",
                    learn = "Skills I Want to Learn",
                    matchRate = "Match Rate",
                    requestBtn = "Match Request",
                    messageBtn = "Send Message",

                    nationality = "Nationality",
                    gender = "Gender",
                    age = "Age",
                    email = "Email",
                    phone = "Phone",
                    address = "Address",

                    teachLevel = "Teaching Level",
                    learnLevel = "Learning Level",

                    rating = "Rating",
                    noInfo = "Not provided",

                    retry = "Retry",
                    loading = "Loading profile"
                )


            AppLanguage.CHINESE ->
                UserProfileStrings(
                    title = "个人资料",
                    about = "个人介绍",
                    teach = "我可以教授的技能",
                    learn = "我想学习的技能",
                    matchRate = "匹配率",
                    requestBtn = "发送匹配请求",
                    messageBtn = "发送消息",

                    nationality = "国籍",
                    gender = "性别",
                    age = "年龄",
                    email = "邮箱",
                    phone = "电话",
                    address = "地址",

                    teachLevel = "教授等级",
                    learnLevel = "学习等级",

                    rating = "评分",
                    noInfo = "用户未填写",

                    retry = "重新加载",
                    loading = "正在加载个人资料"
                )
        }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        BackgroundGalaxy()


        Scaffold(
            containerColor = Color.Transparent,

            topBar = {

                TopAppBar(

                    colors =
                        TopAppBarDefaults
                            .topAppBarColors(
                                containerColor =
                                    Color.Transparent
                            ),

                    title = {

                        Text(
                            text = strings.title,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color = Color.White
                        )
                    },

                    navigationIcon = {

                        IconButton(
                            onClick = onBackClick
                        ) {

                            Icon(
                                imageVector =
                                    Icons.AutoMirrored
                                        .Filled
                                        .ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },

                    actions = {

                        LanguageSelector(
                            currentLanguage = language,
                            expanded =
                                languageMenuExpanded,

                            onExpandedChange = {
                                languageMenuExpanded = it
                            },

                            onLanguageSelected = {
                                language = it
                                languageMenuExpanded = false
                            }
                        )
                    }
                )
            }

        ) { padding ->


            when (
                val state = uiState
            ) {


                /*
                 * 加载中
                 */
                UserProfileUiState.Loading -> {

                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(padding),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            CircularProgressIndicator(
                                color =
                                    Color(0xFF00D0D9)
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(16.dp)
                            )

                            Text(
                                text =
                                    strings.loading,
                                color =
                                    Color.White.copy(
                                        alpha = 0.65f
                                    )
                            )
                        }
                    }
                }


                /*
                 * 网络错误
                 */
                is UserProfileUiState.Error -> {

                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(32.dp),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(
                                text =
                                    state.message,

                                color =
                                    Color.White.copy(
                                        alpha = 0.75f
                                    ),

                                textAlign =
                                    TextAlign.Center,

                                fontSize = 15.sp
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(20.dp)
                            )


                            Button(
                                onClick = {
                                    profileViewModel
                                        .loadProfile(
                                            userId
                                        )
                                },

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
                                    strings.retry
                                )
                            }
                        }
                    }
                }


                /*
                 * 获取成功
                 */
                is UserProfileUiState.Success -> {

                    ProfileContent(
                        profile =
                            state.profile,

                        strings =
                            strings,

                        language =
                            language,

                        padding =
                            padding,

                        onMatchRequestClick =
                            onMatchRequestClick,

                        onChatClick =
                            onChatClick
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileContent(
    profile: ProfileResponse,
    strings: UserProfileStrings,
    language: AppLanguage,
    padding: PaddingValues,
    onMatchRequestClick: (String) -> Unit,
    onChatClick: (String) -> Unit
) {


    /*
     * 后端目前 skillOffer / skillWant
     * 是 String
     *
     * 如果数据库里是：
     *
     * Java,React,Spring
     *
     * 就会自动拆成：
     *
     * Java
     * React
     * Spring
     */
    val teachSkills =
        parseSkills(
            profile.skillOffer
        )

    val learnSkills =
        parseSkills(
            profile.skillWant
        )


    /*
     * 评分换算成百分比
     *
     * 5.0 -> 100
     * 4.5 -> 90
     * 4.0 -> 80
     */
    val matchRate =
        if (
            profile.averageRating != null
            && profile.averageRating > 0
        ) {

            (
                    profile.averageRating * 20
                    )
                .toInt()
                .coerceIn(
                    0,
                    100
                )

        } else {

            0
        }


    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 24.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally

    ) {


        Spacer(
            modifier =
                Modifier.height(16.dp)
        )


        /*
         * 头像
         */
        val avatarUrl = remember(profile.avatar) {
            val url = buildAvatarUrl(profile.avatar)
            android.util.Log.d("AVATAR_DEBUG", "username=${profile.username}, rawAvatar=${profile.avatar}, builtUrl=$url")
            url
        }

        Box(

            modifier =
                Modifier
                    .size(120.dp)
                    .clip(CircleShape)
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
                    ),

            contentAlignment =
                Alignment.Center

        ) {
            if (!avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector =
                        Icons.Default.Person,

                    contentDescription = null,

                    modifier =
                        Modifier.size(64.dp),

                    tint = Color.White
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )


        /*
         * 用户姓名
         */
        Text(

            text =
                profile.name
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: profile.username,

            style =
                MaterialTheme
                    .typography
                    .headlineMedium,

            fontWeight =
                FontWeight.Bold,

            color = Color.White
        )


        Spacer(
            modifier =
                Modifier.height(4.dp)
        )


        /*
         * username
         */
        Text(

            text =
                "@${profile.username}",

            color =
                Color.White.copy(
                    alpha = 0.45f
                ),

            fontSize = 13.sp
        )


        /*
         * 匹配率
         */
        Surface(

            color =
                Color(0xFF00D0D9)
                    .copy(
                        alpha = 0.15f
                    ),

            shape =
                RoundedCornerShape(
                    12.dp
                ),

            modifier =
                Modifier.padding(
                    top = 10.dp
                )

        ) {

            Text(

                text =
                    "${strings.matchRate} $matchRate%",

                modifier =
                    Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    ),

                fontSize = 14.sp,

                color =
                    Color(0xFF00D0D9),

                fontWeight =
                    FontWeight.Black
            )
        }


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        /*
         * 后端真实评分
         */
        Text(

            text =
                if (
                    profile.averageRating != null
                ) {

                    "${strings.rating}: " +
                            String.format(
                                "%.1f",
                                profile.averageRating
                            ) +
                            " ★" +
                            if (
                                profile.ratingCount != null
                            ) {
                                " (${profile.ratingCount})"
                            } else {
                                ""
                            }

                } else {

                    "${strings.rating}: -"
                },

            color =
                Color(0xFFFFC107),

            fontSize = 13.sp,

            fontWeight =
                FontWeight.Medium
        )


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        /*
         * 雷达图
         */
        SkillRadarChart(
            matchRate =
                matchRate,

            currentLanguage =
                language
        )


        Spacer(
            modifier =
                Modifier.height(18.dp)
        )


        /*
         * 个人信息
         */
        GlassProfilePanel {


            /*
             * 个人介绍
             */



            /*
             * 国籍
             */
            ProfileInfoRow(
                label =
                    strings.nationality,

                value =
                    profile.nationality
            )


            /*
             * 性别
             */
            ProfileInfoRow(
                label =
                    strings.gender,

                value =
                    profile.gender
            )


            /*
             * 年龄
             */
            ProfileInfoRow(
                label =
                    strings.age,

                value =
                    profile.age
                        ?.toString()
            )


            /*
             * Email
             */
            ProfileInfoRow(
                label =
                    strings.email,

                value =
                    profile.email
            )


            /*
             * Phone
             */
            ProfileInfoRow(
                label =
                    strings.phone,

                value =
                    profile.phone
            )


            /*
             * Address
             */
            ProfileInfoRow(
                label =
                    strings.address,

                value =
                    profile.address
            )
        }


        Spacer(
            modifier =
                Modifier.height(18.dp)
        )


        /*
         * 技能
         */
        GlassProfilePanel {


            ProfileSkillSectionComponent(

                title =
                    strings.teach,

                skills =
                    teachSkills,

                themeColor =
                    Color(
                        0xFF00D0D9
                    ),

                emptyText =
                    strings.noInfo
            )


            if (
                !profile.skillOfferLevel
                    .isNullOrBlank()
            ) {

                ProfileInfoRow(
                    label =
                        strings.teachLevel,

                    value =
                        profile.skillOfferLevel
                )
            }


            ProfileSkillSectionComponent(

                title =
                    strings.learn,

                skills =
                    learnSkills,

                themeColor =
                    Color(
                        0xFF7E57C2
                    ),

                emptyText =
                    strings.noInfo
            )


            if (
                !profile.skillWantLevel
                    .isNullOrBlank()
            ) {

                ProfileInfoRow(
                    label =
                        strings.learnLevel,

                    value =
                        profile.skillWantLevel
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(36.dp)
        )


        /*
         * 匹配按钮
         *
         * 注意：
         * 这里传的是 username
         */
        SoulButton(

            text =
                strings.requestBtn,

            onClick = {

                onMatchRequestClick(
                    profile.username
                )
            }
        )


        Spacer(
            modifier =
                Modifier.height(14.dp)
        )


        /*
         * 聊天按钮
         */
        Button(

            onClick = {

                onChatClick(
                    profile.username
                )
            },

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(54.dp),

            shape =
                RoundedCornerShape(
                    27.dp
                ),

            colors =
                ButtonDefaults
                    .buttonColors(

                        containerColor =
                            Color.White.copy(
                                alpha = 0.06f
                            ),

                        contentColor =
                            Color(
                                0xFF00D0D9
                            )
                    ),

            border =
                BorderStroke(
                    1.2.dp,
                    Color(
                        0xFF00D0D9
                    ).copy(
                        alpha = 0.4f
                    )
                )

        ) {

            Text(

                text =
                    strings.messageBtn,

                fontSize = 16.sp,

                fontWeight =
                    FontWeight.Bold,

                letterSpacing =
                    0.5.sp
            )
        }


        Spacer(
            modifier =
                Modifier.height(40.dp)
        )
    }
}



@Composable
private fun GlassProfilePanel(
    content:
    @Composable ColumnScope.() -> Unit
) {

    Box(

        modifier =
            Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        24.dp
                    )
                )
                .background(
                    Color.White.copy(
                        alpha = 0.05f
                    )
                )
                .border(
                    width = 1.dp,
                    color =
                        Color.White.copy(
                            alpha = 0.12f
                        ),
                    shape =
                        RoundedCornerShape(
                            24.dp
                        )
                )
                .padding(
                    20.dp
                )
    ) {

        Column(
            content = content
        )
    }
}



@Composable
private fun ProfileSectionComponent(
    title: String,
    content: String
) {

    Column(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 20.dp
                )
    ) {

        Text(

            text = title,

            fontSize = 15.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                Color(
                    0xFF00D0D9
                )
        )


        Spacer(
            modifier =
                Modifier.height(6.dp)
        )


        Text(

            text = content,

            fontSize = 15.sp,

            color =
                Color.White.copy(
                    alpha = 0.72f
                ),

            lineHeight = 22.sp
        )
    }
}



@Composable
private fun ProfileInfoRow(
    label: String,
    value: String?
) {

    if (
        value.isNullOrBlank()
    ) {
        return
    }


    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 7.dp
                ),

        verticalAlignment =
            Alignment.Top

    ) {

        Text(

            text = label,

            modifier =
                Modifier.width(
                    100.dp
                ),

            color =
                Color.White.copy(
                    alpha = 0.4f
                ),

            fontSize = 13.sp
        )


        Text(

            text = value,

            modifier =
                Modifier.weight(
                    1f
                ),

            color =
                Color.White.copy(
                    alpha = 0.8f
                ),

            fontSize = 14.sp
        )
    }
}



@OptIn(
    ExperimentalLayoutApi::class
)
@Composable
private fun ProfileSkillSectionComponent(
    title: String,
    skills: List<String>,
    themeColor: Color,
    emptyText: String
) {

    Column(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 20.dp
                )
    ) {

        Text(

            text = title,

            fontSize = 15.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                themeColor
        )


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        if (
            skills.isEmpty()
        ) {

            Text(

                text =
                    emptyText,

                color =
                    Color.White.copy(
                        alpha = 0.4f
                    ),

                fontSize = 13.sp
            )

        } else {


            FlowRow(

                horizontalArrangement =
                    Arrangement.spacedBy(
                        8.dp
                    ),

                verticalArrangement =
                    Arrangement.spacedBy(
                        8.dp
                    )

            ) {


                skills.forEach { skill ->


                    Box(

                        modifier =
                            Modifier
                                .clip(
                                    RoundedCornerShape(
                                        8.dp
                                    )
                                )
                                .background(
                                    themeColor.copy(
                                        alpha = 0.08f
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color =
                                        themeColor.copy(
                                            alpha = 0.15f
                                        ),
                                    shape =
                                        RoundedCornerShape(
                                            8.dp
                                        )
                                )
                                .padding(
                                    horizontal =
                                        12.dp,
                                    vertical =
                                        5.dp
                                )

                    ) {

                        Text(

                            text = skill,

                            color =
                                if (
                                    themeColor ==
                                    Color(
                                        0xFF7E57C2
                                    )
                                ) {

                                    Color(
                                        0xFFB39DDB
                                    )

                                } else {

                                    themeColor
                                },

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}



private fun parseSkills(
    value: String?
): List<String> {

    if (
        value.isNullOrBlank()
    ) {
        return emptyList()
    }


    return value

        /*
         * 支持这些格式：
         *
         * Java,React
         * Java / React
         * Java|React
         * Java;React
         */
        .split(
            ",",
            "/",
            "|",
            ";"
        )

        .map {
            it.trim()
        }

        .filter {
            it.isNotBlank()
        }
}



private data class UserProfileStrings(

    val title: String,

    val about: String,

    val teach: String,

    val learn: String,

    val matchRate: String,

    val requestBtn: String,

    val messageBtn: String,

    val nationality: String,

    val gender: String,

    val age: String,

    val email: String,

    val phone: String,

    val address: String,

    val teachLevel: String,

    val learnLevel: String,

    val rating: String,

    val noInfo: String,

    val retry: String,

    val loading: String
)



@Composable
fun SkillRadarChart(
    matchRate: Int,
    currentLanguage: AppLanguage
) {

    /*
     * 目前后端没有真正的五维雷达数据，
     * 所以先继续根据评分生成。
     *
     * 后面如果 AI 测试有五维结果，
     * 再直接替换这里即可。
     */
    val stats =
        remember(matchRate) {

            listOf(

                (
                        matchRate *
                                0.90f
                        ).coerceIn(
                        0f,
                        100f
                    ),

                (
                        matchRate *
                                1.0f
                        ).coerceIn(
                        0f,
                        100f
                    ),

                (
                        matchRate *
                                0.85f
                        ).coerceIn(
                        0f,
                        100f
                    ),

                (
                        matchRate *
                                0.95f
                        ).coerceIn(
                        0f,
                        100f
                    ),

                (
                        matchRate *
                                1.05f
                        ).coerceIn(
                        0f,
                        100f
                    )
            )
        }


    /*
     * 雷达图语言也跟着切换
     */
    val labels =
        when (
            currentLanguage
        ) {

            AppLanguage.KOREAN ->
                listOf(
                    "지식 출력",
                    "흡수 속도",
                    "활발한 공명",
                    "안정성",
                    "적합도"
                )


            AppLanguage.ENGLISH ->
                listOf(
                    "Knowledge",
                    "Learning",
                    "Activity",
                    "Stability",
                    "Compatibility"
                )


            AppLanguage.CHINESE ->
                listOf(
                    "知识输出",
                    "学习速度",
                    "活跃程度",
                    "稳定性",
                    "适合度"
                )
        }


    Box(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(
                    190.dp
                )
                .padding(
                    vertical = 12.dp
                ),

        contentAlignment =
            Alignment.Center

    ) {


        Canvas(
            modifier =
                Modifier.fillMaxSize()
        ) {


            val centerX =
                size.width / 2f

            val centerY =
                size.height / 2f

            val maxRadius =
                size.minDimension /
                        2.5f


            /*
             * 网格
             */
            for (
            j in 1..3
            ) {

                val radius =
                    maxRadius *
                            (
                                    j /
                                            3f
                                    )


                val path =
                    Path()


                for (
                i in 0..4
                ) {

                    val angle =
                        Math.toRadians(
                            (
                                    i *
                                            72 -
                                            90
                                    ).toDouble()
                        )


                    val x =
                        centerX +
                                Math.cos(
                                    angle
                                )
                                    .toFloat() *
                                radius


                    val y =
                        centerY +
                                Math.sin(
                                    angle
                                )
                                    .toFloat() *
                                radius


                    if (
                        i == 0
                    ) {

                        path.moveTo(
                            x,
                            y
                        )

                    } else {

                        path.lineTo(
                            x,
                            y
                        )
                    }
                }


                path.close()


                drawPath(

                    path = path,

                    color =
                        Color.White.copy(
                            alpha = 0.08f
                        ),

                    style =
                        Stroke(
                            width =
                                1.dp.toPx()
                        )
                )
            }


            /*
             * 五条中心线
             */
            for (
            i in 0..4
            ) {

                val angle =
                    Math.toRadians(
                        (
                                i *
                                        72 -
                                        90
                                ).toDouble()
                    )


                val x =
                    centerX +
                            Math.cos(
                                angle
                            )
                                .toFloat() *
                            maxRadius


                val y =
                    centerY +
                            Math.sin(
                                angle
                            )
                                .toFloat() *
                            maxRadius


                drawLine(

                    color =
                        Color.White.copy(
                            alpha = 0.05f
                        ),

                    start =
                        androidx.compose.ui.geometry.Offset(
                            centerX,
                            centerY
                        ),

                    end =
                        androidx.compose.ui.geometry.Offset(
                            x,
                            y
                        ),

                    strokeWidth =
                        1.dp.toPx()
                )
            }


            /*
             * 数据区域
             */
            val statPath =
                Path()


            for (
            i in 0..4
            ) {

                val angle =
                    Math.toRadians(
                        (
                                i *
                                        72 -
                                        90
                                ).toDouble()
                    )


                val radius =
                    maxRadius *
                            (
                                    stats[i] /
                                            100f
                                    )


                val x =
                    centerX +
                            Math.cos(
                                angle
                            )
                                .toFloat() *
                            radius


                val y =
                    centerY +
                            Math.sin(
                                angle
                            )
                                .toFloat() *
                            radius


                if (
                    i == 0
                ) {

                    statPath.moveTo(
                        x,
                        y
                    )

                } else {

                    statPath.lineTo(
                        x,
                        y
                    )
                }
            }


            statPath.close()


            drawPath(

                path =
                    statPath,

                brush =
                    Brush.radialGradient(
                        colors =
                            listOf(

                                Color(
                                    0xFF00D0D9
                                ).copy(
                                    alpha = 0.4f
                                ),

                                Color(
                                    0xFF7E57C2
                                ).copy(
                                    alpha = 0.35f
                                )
                            )
                    ),

                style =
                    Fill
            )


            drawPath(

                path =
                    statPath,

                color =
                    Color(
                        0xFF00D0D9
                    ),

                style =
                    Stroke(
                        width =
                            1.5.dp.toPx()
                    )
            )
        }


        /*
         * 标签
         */
        labels
            .forEachIndexed {
                    index,
                    label ->


                val angle =
                    Math.toRadians(
                        (
                                index *
                                        72 -
                                        90
                                ).toDouble()
                    )


                val xOffset =
                    (
                            Math.cos(
                                angle
                            ) *
                                    92
                            )
                        .toInt()
                        .dp


                val yOffset =
                    (
                            Math.sin(
                                angle
                            ) *
                                    72
                            )
                        .toInt()
                        .dp


                Text(

                    text =
                        label,

                    color =
                        Color.White.copy(
                            alpha = 0.6f
                        ),

                    fontSize =
                        10.sp,

                    fontWeight =
                        FontWeight.Bold,

                    modifier =
                        Modifier.offset(
                            x =
                                xOffset,
                            y =
                                yOffset
                        )
                )
            }
    }
}

private fun buildAvatarUrl(avatar: String?): String? {
    if (avatar.isNullOrBlank()) return null
    if (avatar.startsWith("http://") || avatar.startsWith("https://")) return avatar
    return SoulApiService.BASE_URL.trimEnd('/') + "/" + avatar.trimStart('/')
}