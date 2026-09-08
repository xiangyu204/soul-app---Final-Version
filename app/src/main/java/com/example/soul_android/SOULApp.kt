package com.example.soul_android

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.soul_android.ui.screens.*

@Composable
fun SOULApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // --- Authentication & Onboarding ---
        composable("login") {
            LoginScreen(
                onSignUpClick = { navController.navigate("signup") },
                onLoginSuccess = { 
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                onBackToLogin = { navController.popBackStack() },
                onSignUpSuccess = { navController.navigate("profile_setup") }
            )
        }

        composable("profile_setup") {
            ProfileSetupScreen(
                onNextClick = { navController.navigate("skill_selection") }
            )
        }

        composable("skill_selection") {
            SkillSelectionScreen(
                onBackClick = { navController.popBackStack() },
                onDoneClick = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // --- Main Tabs ---
        composable("home") {
            HomeScreen(
                onNavigateToExplore = { navController.navigate("explore") },
                onNavigateToMatches = { navController.navigate("matches") },
                onNavigateToChat = { navController.navigate("chat_list") },
                onNavigateToProfile = { navController.navigate("profile_tab") },
                onUserClick = { userId -> navController.navigate("user_profile/$userId") }
            )
        }

        composable("explore") {
            ExploreScreen(
                onUserClick = { userId -> navController.navigate("user_profile/$userId") },
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToMatches = { navController.navigate("matches") },
                onNavigateToChat = { navController.navigate("chat_list") },
                onNavigateToProfile = { navController.navigate("profile_tab") }
            )
        }

        composable("matches") {
            MatchesScreen(
                onUserClick = { userId -> navController.navigate("user_profile/$userId") },
                onChatClick = { userId -> navController.navigate("chat/$userId") },
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToExplore = { navController.navigate("explore") },
                onNavigateToChat = { navController.navigate("chat_list") },
                onNavigateToProfile = { navController.navigate("profile_tab") }
            )
        }

        composable("chat_list") {
            ChatListScreen(
                onChatClick = { userId -> navController.navigate("chat/$userId") },
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToExplore = { navController.navigate("explore") },
                onNavigateToMatches = { navController.navigate("matches") },
                onNavigateToProfile = { navController.navigate("profile_tab") }
            )
        }

        composable("profile_tab") {
            ProfileScreen(
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToExplore = { navController.navigate("explore") },
                onNavigateToMatches = { navController.navigate("matches") },
                onNavigateToChat = { navController.navigate("chat_list") },
                onEditProfileClick = { /* No Edit Screen yet, placeholder */ },
                onSettingsClick = { navController.navigate("settings") },
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // --- Details & Interaction ---
        composable("user_profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserProfileScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() },
                onMatchRequestClick = { uid -> navController.navigate("match_request/$uid") },
                onChatClick = { uid -> navController.navigate("chat/$uid") }
            )
        }

        composable("match_request/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            MatchRequestScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() },
                onSendRequestClick = {
                    navController.popBackStack("explore", inclusive = false)
                }
            )
        }

        composable("chat/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ChatScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() },
                onScheduleClick = { uid -> navController.navigate("schedule/$uid") }
            )
        }

        composable("schedule/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ScheduleScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("review/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ReviewScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
