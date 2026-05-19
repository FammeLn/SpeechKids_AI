package com.example.speechkids_ai.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.speechkids_ai.model.AuthState
import com.example.speechkids_ai.model.User
import com.example.speechkids_ai.model.UserRole
import kotlinx.coroutines.delay

class AuthRepository {
    private val _authState = MutableLiveData<AuthState>(AuthState.UNAUTHENTICATED)
    val authState: LiveData<AuthState> = _authState

    private val _currentUser = MutableLiveData<User?>(null)
    val currentUser: LiveData<User?> = _currentUser

    // Mock users for testing
    private val mockUsers = mapOf(
        "parent@test.com" to User(
            id = "parent_1",
            email = "parent@test.com",
            name = "Родитель Тест",
            role = UserRole.PARENT
        ),
        "therapist@test.com" to User(
            id = "therapist_1",
            email = "therapist@test.com",
            name = "Логопед Тест",
            role = UserRole.THERAPIST
        ),
        "admin@test.com" to User(
            id = "admin_1",
            email = "admin@test.com",
            name = "Админ Тест",
            role = UserRole.ADMIN
        )
    )

    suspend fun login(email: String, password: String): Boolean {
        _authState.postValue(AuthState.LOADING)
        delay(1000)  // Simulate network call

        val user = if (mockUsers.containsKey(email)) {
            mockUsers[email]!!
        } else {
            val resolvedRole = when {
                email.contains("therapist", ignoreCase = true) -> UserRole.THERAPIST
                email.contains("admin", ignoreCase = true) -> UserRole.ADMIN
                else -> UserRole.PARENT
            }
            User(
                id = "mock_${resolvedRole.name.lowercase()}_${System.currentTimeMillis()}",
                email = if (email.isBlank()) "guest@test.com" else email,
                name = if (email.isBlank()) "Гость" else email.substringBefore("@"),
                role = resolvedRole
            )
        }
        _currentUser.postValue(user)
        _authState.postValue(AuthState.AUTHENTICATED)
        return true
    }

    suspend fun register(email: String, name: String, password: String, role: UserRole): Boolean {
        _authState.postValue(AuthState.LOADING)
        delay(1000)  // Simulate network call

        val user = if (mockUsers.containsKey(email)) {
            mockUsers[email]!!
        } else {
            User(
                id = "${role.name.lowercase()}_${System.currentTimeMillis()}",
                email = if (email.isBlank()) "guest_reg@test.com" else email,
                name = if (name.isBlank()) "Новый Пользователь" else name,
                role = role
            )
        }
        _currentUser.postValue(user)
        _authState.postValue(AuthState.AUTHENTICATED)
        return true
    }

    suspend fun logout() {
        _currentUser.postValue(null)
        _authState.postValue(AuthState.UNAUTHENTICATED)
    }

    fun isAuthenticated(): Boolean = _authState.value == AuthState.AUTHENTICATED

    fun getCurrentUserRole(): UserRole? = _currentUser.value?.role
}

