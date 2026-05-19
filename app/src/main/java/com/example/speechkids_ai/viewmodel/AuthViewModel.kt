package com.example.speechkids_ai.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speechkids_ai.model.AuthState
import com.example.speechkids_ai.model.User
import com.example.speechkids_ai.model.UserRole
import com.example.speechkids_ai.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    val authState: LiveData<AuthState> = repository.authState
    val currentUser: LiveData<User?> = repository.currentUser

    private val _loginError = MutableLiveData<String?>(null)
    val loginError: LiveData<String?> = _loginError

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password)
            _loginError.postValue(null)
        }
    }

    fun register(email: String, name: String, password: String, confirmPassword: String, role: UserRole) {
        viewModelScope.launch {
            repository.register(email, name, password, role)
            _loginError.postValue(null)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun selectRole(role: UserRole) {
        // Will be used after auth to set the user role
        // This is more for UI navigation
    }
}

