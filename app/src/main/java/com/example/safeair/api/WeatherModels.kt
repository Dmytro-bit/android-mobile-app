package com.example.safeair.api

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val current_units: CurrentUnits,
    val current: CurrentWeather,
    val hourly_units: HourlyUnits,
    val hourly: HourlyWeather,
    val daily_units: DailyUnits,
    val daily: DailyWeather
)

data class CurrentUnits(
    val time: String,
    val interval: String,
    val cloud_cover: String
)

data class CurrentWeather(
    val time: String,
    val interval: Int,
    val cloud_cover: Double
)

data class HourlyUnits(
    val time: String,
    val temperature_2m: String,
    val weather_code: String,
    val relative_humidity_2m: String,
    val visibility: String,
    val wind_speed_10m: String,
    val cloud_cover_mid: String,
    val cloud_cover_low: String,
    val cloud_cover_high: String,
    val cloud_cover: String
)

data class HourlyWeather(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val weather_code: List<Int>,
    val relative_humidity_2m: List<Double>,
    val visibility: List<Double>,
    val wind_speed_10m: List<Double>,
    val cloud_cover_mid: List<Double>,
    val cloud_cover_low: List<Double>,
    val cloud_cover_high: List<Double>,
    val cloud_cover: List<Double>
)

data class DailyUnits(
    val time: String,
    val temperature_2m_max: String,
    val temperature_2m_min: String,
    val sunrise: String,
    val sunset: String,
    val uv_index_max: String,
    val rain_sum: String,
    val wind_speed_10m_max: String,
    val weather_code: String
)

data class DailyWeather(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    val uv_index_max: List<Double>,
    val rain_sum: List<Double>,
    val wind_speed_10m_max: List<Double>,
    val weather_code: List<Int>
)
