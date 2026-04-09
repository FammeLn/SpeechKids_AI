package com.halfi.auth.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(unique = true, nullable = false)
    var email: String = "",

    @JsonIgnore
    @Column(nullable = false)
    var password: String = "",

    @Column(nullable = false)
    var role: String = "ROLE_USER",

    @Column
    var refreshToken: String? = null,

    @Column
    var verificationCode: String? = null,

    @Column
    var codeExpiresAt: java.util.Date? = null,

    @Column(nullable = false)
    var enabled: Boolean = false
)