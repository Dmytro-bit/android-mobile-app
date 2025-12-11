package com.example.safeair.ui.theme.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Home : Screen("home_screen")
    object Settings : Screen("settings_screen")
}
