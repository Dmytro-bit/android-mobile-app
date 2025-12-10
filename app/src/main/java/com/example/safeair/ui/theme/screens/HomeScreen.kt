package com.example.safeair.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.safeair.api.WeatherResponse
import com.example.safeair.ui.theme.viewmodel.HomeViewModel
import com.example.safeair.ui.theme.viewmodel.HomeViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreenRoute(
    viewModelFactory: HomeViewModelFactory? = null,
    viewModel: HomeViewModel = if (viewModelFactory != null) {
        androidx.lifecycle.viewmodel.compose.viewModel(factory = viewModelFactory)
    } else {
        viewModel()
    }
) {
    val weatherData by viewModel.weatherData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    HomeScreen(
        weatherData = weatherData,
        isLoading = isLoading,
        error = error
    )
}

@Composable
fun HomeScreen(
    weatherData: WeatherResponse?,
    isLoading: Boolean,
    error: String?
) {
    val context = LocalContext.current
    
    // Determine if it's day or night based on current time and sunrise/sunset
    val isDay = weatherData?.let { data ->
        val currentTime = System.currentTimeMillis() / 1000
        // Use current time from API if available, otherwise use system time
        val timeToCheck = if (data.current.dt > 0) data.current.dt else currentTime
        timeToCheck >= data.current.sunrise && timeToCheck < data.current.sunset
    } ?: true

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(
                id = if (isDay) R.drawable.day else R.drawable.night
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top Bar
            TopBar()

            Spacer(modifier = Modifier.weight(1f))

            // Current Weather Card
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.White
                )
            } else if (error != null) {
                Text(
                    text = error,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (weatherData != null) {
                CurrentWeatherCard(weatherData)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Forecast Panel
            if (weatherData != null) {
                ForecastPanel(weatherData.daily.take(5), isDay)
            }
        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hamburger Menu
        IconButton(onClick = { /* Handle menu click */ }) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // ADD Button
        Button(
            onClick = { /* Handle add click */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text(
                text = "ADD",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CurrentWeatherCard(weatherData: WeatherResponse) {
    val current = weatherData.current
    val location = weatherData.timezone.split("/").lastOrNull() ?: weatherData.timezone
    
    // Convert Kelvin to Celsius (if temp > 100, assume it's Kelvin)
    val tempCelsius = if (current.temp > 100) {
        (current.temp - 273.15).toInt()
    } else {
        current.temp.toInt()
    }
    
    // Format date
    val dateFormat = SimpleDateFormat("EEEE dd/M/yyyy", Locale.getDefault())
    val date = dateFormat.format(Date(current.dt * 1000))
    
    // Weather icon URL
    val iconUrl = "https://openweathermap.org/img/wn/${current.weather.firstOrNull()?.icon}@2x.png"
    val isDay = current.dt >= weatherData.current.sunrise && current.dt < weatherData.current.sunset

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDay) {
                Color.White.copy(alpha = 0.25f)
            } else {
                Color(0xFF2D1B3D).copy(alpha = 0.6f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Weather Icon
            Image(
                painter = rememberAsyncImagePainter(iconUrl),
                contentDescription = current.weather.firstOrNull()?.description,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Location
            Text(
                text = location,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Temperature
            Text(
                text = "$tempCelsius°C",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date
            Text(
                text = date,
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun ForecastPanel(dailyForecast: List<com.example.safeair.api.DailyWeather>, isDay: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDay) {
                Color(0xFFE8E8E8).copy(alpha = 0.4f)
            } else {
                Color(0xFF2D1B3D).copy(alpha = 0.7f)
            }
        )
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(dailyForecast) { day ->
                ForecastDayItem(day, isDay)
            }
        }

        // Scroll indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        Color.White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
fun ForecastDayItem(day: com.example.safeair.api.DailyWeather, isDay: Boolean) {
    val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    val dayName = dateFormat.format(Date(day.dt * 1000))
    
    // Convert Kelvin to Celsius (if temp > 100, assume it's Kelvin)
    val tempCelsius = if (day.temp.day > 100) {
        (day.temp.day - 273.15).toInt()
    } else {
        day.temp.day.toInt()
    }
    
    // Weather icon URL
    val iconUrl = "https://openweathermap.org/img/wn/${day.weather.firstOrNull()?.icon}@2x.png"

    Column(
        modifier = Modifier
            .width(100.dp)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Day name
        Text(
            text = dayName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        // Weather Icon
        Image(
            painter = rememberAsyncImagePainter(iconUrl),
            contentDescription = day.weather.firstOrNull()?.description,
            modifier = Modifier.size(48.dp)
        )

        // Temperature
        Text(
            text = "$tempCelsius°C",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
