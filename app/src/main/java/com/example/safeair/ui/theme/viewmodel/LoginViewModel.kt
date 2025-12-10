package com.example.safeair.ui.theme.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safeair.api.ApiServices
import com.example.safeair.api.AuthModels
import com.example.safeair.repository.TokenManager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authService: ApiServices,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    val isUserLoggedIn = tokenManager.accessToken

    fun login(username: String, password: String) {
        _loginError.value = null


        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = AuthModels.LoginRequest(username, password)

                val response = authService.login(request)

                if (response.isSuccessful) {
                    val accessToken = response.body()?.access_token

                    if (!accessToken.isNullOrEmpty()) {
                        tokenManager.saveToken(accessToken)
                    } else {
                        _loginError.value = response.body()?.msg ?: "Ошибка: Токен не получен."
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _loginError.value = "Ошибка входа: HTTP ${response.code()}. ${errorBody ?: "Неизвестно."}"
                }
            } catch (e: Exception) {
                _loginError.value = "Ошибка сети или сервера: ${e.message ?: "Неизвестная ошибка"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}