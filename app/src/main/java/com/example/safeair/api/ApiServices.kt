package com.example.safeair.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServices {
    @POST("auth/login")
    suspend fun login(@Body request: AuthModels.LoginRequest): Response<AuthModels.LoginResponse>

    @GET("data/3.0/onecall?appid=4e66c9fd5cbe74ec093077c62c23c0a6")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("exclude") exclude: String = "minutely,alerts"
    ): Response<WeatherResponse>
}