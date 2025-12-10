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

interface WeatherService {
    @GET("current")
    suspend fun getWeatherByCity(
        @Query("key") apiKey: String = "eaccf7a46b8d42a494e10970aa9a1db0",
        @Query("city") city: String
    ): Response<WeatherModels.WeatherApiResponse>
}