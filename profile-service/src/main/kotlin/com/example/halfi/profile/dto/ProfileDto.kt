package com.example.halfi.profile.dto

data class ProfileDto(
    val userId: String,
    val email: String,
    val nickname: String?,
    val phoneNumber: String?,
    val avatarUrl: String?,
    val balance: Long
)

data class ProfileUpdateRequest(
    val nickname: String?,
    val phoneNumber: String?,
    val avatarUrl: String?
)
