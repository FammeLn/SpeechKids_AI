package com.example.halfi.profile.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

data class UserInfo(
    val id: String = "",
    val email: String = "",
    val enabled: Boolean = false
)

/**
 * REST-клиент для обращения к auth-service.
 * Используется для валидации пользователей перед созданием профиля
 * и для стартовой синхронизации.
 */
@Component
class AuthServiceClient(
    @Value("\${auth-service.url:http://auth-service:8080}")
    private val authServiceUrl: String
) {
    private val log = LoggerFactory.getLogger(AuthServiceClient::class.java)
    private val restTemplate = RestTemplate()

    /** Проверить конкретного пользователя по ID */
    fun getUserById(userId: String): UserInfo? {
        return try {
            restTemplate.getForObject<UserInfo>("$authServiceUrl/internal/users/$userId")
        } catch (e: Exception) {
            log.warn("Failed to fetch user $userId from auth-service: ${e.message}")
            null
        }
    }

    /** Получить всех подтверждённых пользователей */
    fun getAllVerifiedUsers(): List<UserInfo> {
        return try {
            val response = restTemplate.getForObject<Array<UserInfo>>("$authServiceUrl/internal/users/verified")
            response?.toList() ?: emptyList()
        } catch (e: Exception) {
            log.warn("Failed to fetch verified users from auth-service: ${e.message}")
            emptyList()
        }
    }
}
