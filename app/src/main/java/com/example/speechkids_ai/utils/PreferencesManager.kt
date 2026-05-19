package com.example.speechkids_ai.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.speechkids_ai.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_preferences")

object PreferencesManager {
    private val SELECTED_ROLE_KEY = stringPreferencesKey("selected_role")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    private val ONBOARDING_COMPLETED_KEY = stringPreferencesKey("onboarding_completed")
    private val CURRENT_MODE_KEY = stringPreferencesKey("current_mode")

    fun getSelectedRole(context: Context): Flow<UserRole?> {
        return context.dataStore.data.map { prefs ->
            prefs[SELECTED_ROLE_KEY]?.let { UserRole.valueOf(it) }
        }
    }

    suspend fun setSelectedRole(context: Context, role: UserRole) {
        context.dataStore.edit { prefs ->
            prefs[SELECTED_ROLE_KEY] = role.name
        }
    }

    fun getUserId(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs -> prefs[USER_ID_KEY] }
    }

    suspend fun setUserId(context: Context, userId: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    fun getUserEmail(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs -> prefs[USER_EMAIL_KEY] }
    }

    suspend fun setUserEmail(context: Context, email: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_EMAIL_KEY] = email
        }
    }

    fun isOnboardingCompleted(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[ONBOARDING_COMPLETED_KEY] == "true"
        }
    }

    suspend fun setOnboardingCompleted(context: Context, completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED_KEY] = completed.toString()
        }
    }

    fun getCurrentMode(context: Context): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[CURRENT_MODE_KEY] ?: "adult"
        }
    }

    suspend fun setCurrentMode(context: Context, mode: String) {
        context.dataStore.edit { prefs ->
            prefs[CURRENT_MODE_KEY] = mode
        }
    }

    suspend fun clearAll(context: Context) {
        context.dataStore.edit { it.clear() }
    }
}

