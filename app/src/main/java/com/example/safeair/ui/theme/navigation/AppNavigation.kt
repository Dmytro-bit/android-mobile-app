package com.example.safeair.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safeair.ui.theme.screens.HomeScreenRoute
import com.example.safeair.ui.theme.screens.LoginScreen
import com.example.safeair.ui.theme.viewmodel.LoginViewModelFactory


@Composable
fun AppNavigation(loginViewModelFactory: LoginViewModelFactory) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController, viewModelFactory = loginViewModelFactory)
        }

        composable(Screen.Home.route) {
            HomeScreenRoute(navController = navController)
        }
    }
}

