package com.example.safeair.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.safeair.ui.theme.navigation.Screen
import com.example.safeair.ui.theme.SafeAirTheme
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.safeair.ui.theme.viewmodel.LoginViewModel
import com.example.safeair.ui.theme.viewmodel.LoginViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModelFactory: LoginViewModelFactory) {
    var username by remember { mutableStateOf("test") }
    var password by remember { mutableStateOf("qwerty123") }
    val viewModel: LoginViewModel = viewModel(factory = viewModelFactory)
    val isLoading by viewModel.isLoading.collectAsState()
    val loginError by viewModel.loginError.collectAsState()
    val isLoggedIn by viewModel.isUserLoggedIn.collectAsState(initial = null)

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn.isNullOrEmpty()) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Safe Air",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (loginError != null) {
            Text(
                text = loginError!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                viewModel.login(username, password)
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Login")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val loginViewModelFactory = object : LoginViewModelFactory(
        authService = null as Any as com.example.safeair.api.ApiServices,
        tokenManager = null as Any as com.example.safeair.repository.TokenManager
    ) {}

    SafeAirTheme {
        LoginScreen(
            navController = rememberNavController(),
            viewModelFactory = loginViewModelFactory
        )
    }
}