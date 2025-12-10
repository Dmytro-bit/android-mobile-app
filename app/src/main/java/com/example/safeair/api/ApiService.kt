package com.example.safeair.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiServices {
    @POST("auth/login")
    suspend fun login(@Body request: AuthModels.LoginRequest): Response<AuthModels.LoginResponse>

}