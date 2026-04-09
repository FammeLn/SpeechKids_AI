package com.halfi.dto.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val role: String
)