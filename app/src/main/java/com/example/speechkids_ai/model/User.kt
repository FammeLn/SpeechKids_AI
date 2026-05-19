package com.example.speechkids_ai.model

enum class UserRole {
    ADMIN, THERAPIST, PARENT, CHILD
}

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole,
    val avatar: String? = null,
    val preferences: UserPreferences = UserPreferences()
)

data class UserPreferences(
    val theme: String = "light",
    val language: String = "ru",
    val notificationsEnabled: Boolean = true,
    val childMode: Boolean = false
)

enum class AuthState {
    AUTHENTICATED, UNAUTHENTICATED, LOADING, ERROR
}

