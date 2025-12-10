package com.example.safeair.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import com.example.safeair.R
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
    error: String?,
    isDay: Boolean = true
) {

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(
                id = R.drawable.day
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.weight(1f))

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
                CurrentWeatherCard(weatherData, isDay)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (weatherData != null) {
                ForecastPanel(weatherData, isDay)
            }
        }
    }
}


@Composable
fun CurrentWeatherCard(weatherData: WeatherResponse, isDay: Boolean) {
    val currentTemp = weatherData.hourly.temperature_2m.firstOrNull() ?: 0.0
    val tempCelsius = currentTemp.toInt()

    val currentWeatherCode = weatherData.hourly.weather_code.firstOrNull() ?: 0

    val location = weatherData.timezone.split("/").lastOrNull() ?: weatherData.timezone

    val dateFormat = SimpleDateFormat("EEEE dd/M/yyyy", Locale.getDefault())
    val currentDate = dateFormat.format(Date())

    val iconUrl = getWeatherIconUrl(currentWeatherCode, isDay)

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
            Image(
                painter = rememberAsyncImagePainter(iconUrl),
                contentDescription = getWeatherDescription(currentWeatherCode),
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = location,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$tempCelsius°C",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentDate,
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun ForecastPanel(weatherData: WeatherResponse, isDay: Boolean) {
    val dailyForecast = weatherData.daily.time.take(5).mapIndexed { index, date ->
        DailyForecastItem(
            date = date,
            temperature = weatherData.daily.temperature_2m_max.getOrNull(index) ?: 0.0,
            weatherCode = weatherData.daily.weather_code.getOrNull(index) ?: 0
        )
    }

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

data class DailyForecastItem(
    val date: String,
    val temperature: Double,
    val weatherCode: Int
)

@Composable
fun ForecastDayItem(day: DailyForecastItem, isDay: Boolean) {
    val dayName = try {
        val date = Instant.parse(day.date).atZone(ZoneId.systemDefault()).toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
        date.format(formatter)
    } catch (e: Exception) {
        day.date
    }

    val tempCelsius = day.temperature.toInt()

    val iconUrl = getWeatherIconUrl(day.weatherCode, isDay)

    Column(
        modifier = Modifier
            .width(100.dp)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = dayName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Image(
            painter = rememberAsyncImagePainter(iconUrl),
            contentDescription = getWeatherDescription(day.weatherCode),
            modifier = Modifier.size(48.dp)
        )

        Text(
            text = "$tempCelsius°C",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}



// Chat GPT conversion
fun getWeatherIconUrl(wmoCode: Int, isDay: Boolean): String {
    val icon = when (wmoCode) {
        0 -> "01" // Clear sky
        1 -> "02" // Mainly clear
        2 -> "02" // Partly cloudy
        3 -> "04" // Overcast
        45, 48 -> "50" // Fog
        51, 53, 55 -> "09" // Drizzle
        56, 57 -> "09" // Freezing drizzle
        61, 63, 65 -> "10" // Rain
        66, 67 -> "13" // Freezing rain
        71, 73, 75 -> "13" // Snow
        77 -> "13" // Snow grains
        80, 81, 82 -> "09" // Rain showers
        85, 86 -> "13" // Snow showers
        95 -> "11" // Thunderstorm
        96, 99 -> "11" // Thunderstorm with hail
        else -> "02" // Default
    }
    val dayNight = if (isDay) "d" else "n"
    return "https://openweathermap.org/img/wn/${icon}${dayNight}@2x.png"
}

fun getWeatherDescription(wmoCode: Int): String {
    return when (wmoCode) {
        0 -> "Clear sky"
        1 -> "Mainly clear"
        2 -> "Partly cloudy"
        3 -> "Overcast"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        56, 57 -> "Freezing drizzle"
        61, 63, 65 -> "Rain"
        66, 67 -> "Freezing rain"
        71, 73, 75 -> "Snow"
        77 -> "Snow grains"
        80, 81, 82 -> "Rain showers"
        85, 86 -> "Snow showers"
        95 -> "Thunderstorm"
        96, 99 -> "Thunderstorm with hail"
        else -> "Unknown"
    }
}
