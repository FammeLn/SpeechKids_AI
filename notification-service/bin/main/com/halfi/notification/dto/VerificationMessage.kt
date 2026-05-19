package com.halfi.notification.dto

data class VerificationMessage(
    val email: String,
    val code: String,
    val type: String = "REGISTRATION"
)
