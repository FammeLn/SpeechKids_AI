package com.halfi.auth.repository;

import com.halfi.auth.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    // Находим последний НЕиспользованный код для данного email
    Optional<EmailVerification> findTopByEmailAndUsedFalseOrderBySentAtDesc(String email);

    // Для проверки cooldown — последний код по email (неважно used или нет)
    Optional<EmailVerification> findTopByEmailOrderBySentAtDesc(String email);

    // Чистка старых просроченных кодов (опционально, для шедулера)
    @Modifying
    @Transactional
    @Query("DELETE FROM EmailVerification e WHERE e.expiresAt < :now")
    void deleteExpiredCodes(LocalDateTime now);
}