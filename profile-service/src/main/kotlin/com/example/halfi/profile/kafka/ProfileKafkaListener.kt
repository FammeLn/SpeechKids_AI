package com.example.halfi.profile.kafka

import com.example.halfi.profile.domain.Profile
import com.example.halfi.profile.repository.ProfileRepository
import com.example.halfi.profile.client.AuthServiceClient
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

data class UserRegisteredEvent(
    val userId: String = "",
    val email: String = ""
)

@Component
class ProfileKafkaListener(
    private val profileRepository: ProfileRepository,
    private val authServiceClient: AuthServiceClient
) {
    private val log = LoggerFactory.getLogger(ProfileKafkaListener::class.java)

    @KafkaListener(topics = ["user-registered-topic"], groupId = "profile-group")
    fun onUserRegistered(event: UserRegisteredEvent) {
        log.info("Received UserRegisteredEvent for user: ${event.userId}, email: ${event.email}")

        // Валидация: проверяем через REST, что пользователь реально существует и подтвердил почту
        val userInfo = authServiceClient.getUserById(event.userId)
        if (userInfo == null) {
            log.warn("REJECTED: User ${event.userId} not found in auth-service. Ignoring fake event.")
            return
        }
        if (!userInfo.enabled) {
            log.warn("REJECTED: User ${event.userId} exists but email is not verified (enabled=false). Ignoring.")
            return
        }

        createProfileIfNotExists(userInfo.id, userInfo.email)
    }

    fun createProfileIfNotExists(userId: String, email: String) {
        val existing = profileRepository.findByUserId(userId)
        if (existing != null) {
            log.info("Profile for user $userId already exists, skipping.")
            return
        }

        // Никнейм по умолчанию: часть email до @
        val defaultNickname = email.substringBefore("@")

        val newProfile = Profile(
            userId = userId,
            email = email,
            nickname = defaultNickname
        )
        profileRepository.save(newProfile)
        log.info("Created new profile for user: $userId with nickname: $defaultNickname")
    }
}
