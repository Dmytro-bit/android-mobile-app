package com.example.safeair.ui.theme.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.safeair.api.RetrofitWeatherInstance
import com.example.safeair.api.WeatherModels.CurrentWeatherData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val weatherService = RetrofitWeatherInstance().apiServices
    private val _currentWeather = MutableStateFlow<List<CurrentWeatherData>>(emptyList())
    val airQualityData = _currentWeather.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Fetching weather data...")
                _isLoading.value = true

                val cities = listOf("Dublin", "London", "Paris", "Berlin", "Madrid")
                val weatherList = mutableListOf<CurrentWeatherData>()
                
                cities.forEach { city ->
                    try {
                        val response = weatherService.getWeatherByCity(city = city)
                        if (response.isSuccessful && response.body() != null) {
                            val weatherData = response.body()!!.data
                            weatherList.addAll(weatherData)
                            Log.d("HomeViewModel", "Fetched weather for $city: ${weatherData.size} items")
                        } else {
                            Log.e("HomeViewModel", "Failed to fetch weather for $city: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        Log.e("HomeViewModel", "Error fetching weather for $city", e)
                    }
                }
                
                _currentWeather.value = weatherList
                _isLoading.value = false
                Log.d("HomeViewModel", "Data fetched successfully. Total items: ${weatherList.size}")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching weather data", e)
                _isLoading.value = false
            }
        }
    }
}

@Composable
fun HomeScreenRoute(
    viewModel: HomeViewModel = viewModel(),
    navController: NavController
) {
    val airQualityList by viewModel.airQualityData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    HomeScreen(
        isLoading = isLoading,
        airQualityList = airQualityList,
        onCardClick = { location ->
            Log.d("HomeScreenRoute", "Card clicked for location: $location")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isLoading: Boolean,
    airQualityList: List<CurrentWeatherData>,
    onCardClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LoadingIndicator()
            }

            AnimatedVisibility(
                visible = !isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (airQualityList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No weather data available")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(airQualityList) { weatherData ->
                            WeatherCard(
                                weatherData = weatherData,
                                onClick = { onCardClick(weatherData.city_name) }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherCard(
    weatherData: CurrentWeatherData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = weatherData.city_name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${weatherData.temp}°C",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Feels like ${weatherData.app_temp}°C",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "AQI: ${weatherData.aqi}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Clouds: ${weatherData.clouds}%",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Wind: ${weatherData.wind_spd} m/s",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AsyncImage(
                    model = "https://cdn.weatherbit.io/static/img/icons/${weatherData.weather.icon}.png",
                    contentDescription = weatherData.weather.description,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = weatherData.weather.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}



