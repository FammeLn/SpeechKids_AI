package com.halfi.auth.entity

import jakarta.persistence.*
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "email_attempts")
class EmailAttempt(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    var email: String = "",

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var requestedAt: Date = Date()
)
