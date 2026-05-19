package com.halfi.auth.controller

import com.halfi.auth.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Внутренний API для межсервисного взаимодействия.
 * Используется profile-service для валидации пользователей.
 * НЕ должен быть доступен через API Gateway для внешних клиентов.
 */
@RestController
@RequestMapping("/internal/users")
class InternalUserController(
    private val userRepository: UserRepository
) {

    data class UserInfo(
        val id: String,
        val email: String,
        val enabled: Boolean
    )

    /** Проверить конкретного пользователя по ID */
    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: String): ResponseEntity<UserInfo> {
        val uuid = try { UUID.fromString(userId) } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }
        val user = userRepository.findById(uuid).orElse(null)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(UserInfo(
            id = user.id.toString(),
            email = user.email,
            enabled = user.enabled
        ))
    }

    /** Получить всех подтверждённых (enabled=true) пользователей */
    @GetMapping("/verified")
    fun getAllVerifiedUsers(): ResponseEntity<List<UserInfo>> {
        val users = userRepository.findAllByEnabledTrue()
        return ResponseEntity.ok(users.map {
            UserInfo(
                id = it.id.toString(),
                email = it.email,
                enabled = it.enabled
            )
        })
    }
}
