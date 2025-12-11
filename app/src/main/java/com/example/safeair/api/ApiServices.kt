package com.example.safeair.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServices {
    @POST("auth/login")
    suspend fun login(@Body request: AuthModels.LoginRequest): Response<AuthModels.LoginResponse>
}

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max,rain_sum,wind_speed_10m_max,weather_code",
        @Query("hourly") hourly: String = "temperature_2m,weather_code,relative_humidity_2m,visibility,wind_speed_10m,cloud_cover_mid,cloud_cover_low,cloud_cover_high,cloud_cover",
        @Query("current") current: String = "cloud_cover"
    ): Response<WeatherResponse>
}