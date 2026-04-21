package com.example.nutriscan.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Onboarding : Screen(route = "onboarding_screen")
    object Home : Screen("home_screen")
    object Scan : Screen("scan_screen")
    object Result : Screen("result_screen")
    object Histori : Screen("history_screen")
    object DetailHistory : Screen("detail_history/{historyId}") {
        // Fungsi pembantu agar mudah mengirim ID
        fun createRoute(historyId: String) = "detail_history/$historyId"
    }
    object Profile : Screen("profile_screen")
}