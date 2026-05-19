package com.example.halfi.profile.sync

import com.example.halfi.profile.client.AuthServiceClient
import com.example.halfi.profile.kafka.ProfileKafkaListener
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

/**
 * Компонент для стартовой синхронизации.
 * При запуске profile-service проходит по всем подтверждённым пользователям
 * из auth-service и создаёт профили для тех, у кого их ещё нет.
 *
 * Это покрывает ситуацию, когда пользователь зарегистрировался и подтвердил email ДО того,
 * как profile-service начал работать (или Kafka-сообщение было потеряно).
 */
@Component
class ProfileStartupSync(
    private val authServiceClient: AuthServiceClient,
    private val profileKafkaListener: ProfileKafkaListener
) {
    private val log = LoggerFactory.getLogger(ProfileStartupSync::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun syncProfilesOnStartup() {
        log.info("Starting profile sync with auth-service...")

        val verifiedUsers = authServiceClient.getAllVerifiedUsers()
        if (verifiedUsers.isEmpty()) {
            log.info("No verified users found in auth-service (or auth-service is unavailable).")
            return
        }

        log.info("Found ${verifiedUsers.size} verified user(s) in auth-service. Syncing profiles...")

        var created = 0
        for (user in verifiedUsers) {
            profileKafkaListener.createProfileIfNotExists(user.id, user.email)
            created++
        }

        log.info("Profile startup sync complete. Processed $created user(s).")
    }
}
