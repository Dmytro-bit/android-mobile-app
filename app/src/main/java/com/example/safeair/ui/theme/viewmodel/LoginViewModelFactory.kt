package com.example.safeair.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.safeair.api.ApiServices
import com.example.safeair.repository.TokenManager

open class LoginViewModelFactory(
    private val authService: ApiServices,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                authService = authService,
                tokenManager = tokenManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}