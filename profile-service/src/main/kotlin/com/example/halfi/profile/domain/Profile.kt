package com.example.halfi.profile.domain

import jakarta.persistence.*

@Entity
@Table(name = "profiles")
class Profile(
    @Id
    @Column(name = "user_id", unique = true, nullable = false)
    var userId: String, // Может быть UUID строкой или Long в зависимости от вашего user-service

    @Column(name = "email", nullable = false)
    var email: String,

    @Column(name = "nickname")
    var nickname: String? = null,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @Column(name = "avatar_url")
    var avatarUrl: String? = null,

    @Column(name = "balance")
    var balance: Long = 0 // Локальная кэшированная копия баланса (или заглушка), реальный в payment-service
) {
    // Пустой конструктор для JPA
    constructor() : this("", "")
}
