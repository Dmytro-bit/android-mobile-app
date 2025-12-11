package com.example.safeair.ui.theme.navigation

import com.example.safeair.ui.theme.screens.HomeScreenRoute
import com.example.safeair.ui.theme.screens.SettingsScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safeair.ui.theme.screens.LoginScreen
import com.example.safeair.ui.theme.viewmodel.HomeViewModelFactory
import com.example.safeair.ui.theme.viewmodel.LoginViewModelFactory


@Composable
fun AppNavigation(
    loginViewModelFactory: LoginViewModelFactory,
    homeViewModelFactory: HomeViewModelFactory
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController, viewModelFactory = loginViewModelFactory)
        }

        composable(Screen.Home.route) {
            HomeScreenRoute(navController = navController, viewModelFactory = homeViewModelFactory)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}

