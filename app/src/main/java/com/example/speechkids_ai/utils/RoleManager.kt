package com.example.speechkids_ai.utils

import com.example.speechkids_ai.model.UserRole

object RoleManager {
    private var currentRole: UserRole = UserRole.PARENT

    fun setRole(role: UserRole) {
        currentRole = role
    }

    fun getRole(): UserRole = currentRole
}

