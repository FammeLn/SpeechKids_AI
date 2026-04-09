package com.halfi.auth.dto

data class RegisterRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val id: java.util.UUID,
    val email: String,
    val message: String
)