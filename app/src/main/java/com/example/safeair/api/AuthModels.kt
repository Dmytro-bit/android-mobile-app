package com.example.safeair.api

class AuthModels {
    data class LoginRequest(
        val username: String,
        val password: String
    )

    data class LoginResponse(
        val msg: String? = null,
        val access_token: String? = null,

    )

}