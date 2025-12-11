package com.example.safeair.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.safeair.api.WeatherApiService

class HomeViewModelFactory(
    private val weatherApiService: WeatherApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(weatherApiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
