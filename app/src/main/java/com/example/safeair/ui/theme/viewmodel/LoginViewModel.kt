package com.example.safeair.ui.theme.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safeair.api.ApiServices
import com.example.safeair.api.AuthModels
import com.example.safeair.repository.TokenManager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authService: ApiServices,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError = _loginError.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _loginError.value = null

                val response = authService.login(AuthModels.LoginRequest(username, password))

                if (response.isSuccessful) {
                    val token = response.body()?.access_token

                    if (!token.isNullOrBlank()) {
                        tokenManager.saveToken(token)
                        _loginSuccess.value = true
                    } else {
                        _loginError.value = "Token not received"
                    }

                } else {
                    _loginError.value = "Login failed: ${response.code()}"
                }

            } catch (e: Exception) {
                _loginError.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}