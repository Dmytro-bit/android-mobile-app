package com.example.safeair.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safeair.api.ApiServices
import com.example.safeair.api.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val apiService: ApiServices
) : ViewModel() {
    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData = _weatherData.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        // Default coordinates (can be made configurable)
        fetchWeather(33.44, -94.04)
    }

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getWeather(lat, lon)
                if (response.isSuccessful) {
                    _weatherData.value = response.body()
                } else {
                    _error.value = "Failed to fetch weather: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
