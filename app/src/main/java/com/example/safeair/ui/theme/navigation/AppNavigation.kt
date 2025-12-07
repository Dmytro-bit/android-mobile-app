package com.example.safeair.ui.theme.navigation

import com.example.safeair.ui.theme.screens.HomeScreenRoute
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safeair.ui.theme.navigation.Screen
import com.example.safeair.ui.theme.screens.LoginScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // NavHost is the container for all navigation destinations
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route // The app starts at the Login screen
    ) {
        // Define the Login screen destination
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        // Define the Home screen destination
        composable(Screen.Home.route) {
            // 2. CALL THIS FUNCTION INSTEAD
            HomeScreenRoute()
        }
    }
}

