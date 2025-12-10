package com.example.safeair.api

class WeatherModels {
    data class WeatherApiResponse(
        val count: Int,
        val data: List<CurrentWeatherData>
    )

    data class CurrentWeatherData(
        val temp: Double,
        val app_temp: Double,
        val city_name: String,
        val aqi: Int,
        val clouds: Int,
        val pres: Double,
        val wind_spd: Double,
        val lat: Double,
        val sunrise: String,
        val sunset: String,
        val ob_time: String,
        val weather: Weather
    )

    data class Weather(
        val description: String,
        val icon: String,
        val code: Int
    )
}