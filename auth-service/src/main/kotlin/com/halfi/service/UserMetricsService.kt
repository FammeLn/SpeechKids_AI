package com.halfi.auth.service

import com.halfi.auth.repository.UserRepository
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserMetricsService(
    private val userRepository: UserRepository,
    meterRegistry: MeterRegistry
) {
    init {
        // Общее количество зарегистрированных пользователей
        Gauge.builder("halfi_users_total_count", userRepository) { it.count().toDouble() }
            .description("Total number of registered users")
            .register(meterRegistry)

        // Количество пользователей онлайн (активных за последние 5 минут)
        Gauge.builder("halfi_users_online_count", userRepository) { 
            it.countByLastActivityAtAfter(LocalDateTime.now().minusMinutes(5)).toDouble() 
        }
            .description("Number of users active in the last 5 minutes")
            .register(meterRegistry)
    }
}
