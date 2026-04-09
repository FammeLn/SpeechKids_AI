package com.halfi.auth.repository

import com.halfi.auth.entity.EmailAttempt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Date
import java.util.UUID

@Repository
interface EmailAttemptRepository : JpaRepository<EmailAttempt, UUID> {
    fun countByEmailAndRequestedAtAfter(email: String, date: Date): Long
    fun findTopByEmailOrderByRequestedAtDesc(email: String): EmailAttempt?
}
