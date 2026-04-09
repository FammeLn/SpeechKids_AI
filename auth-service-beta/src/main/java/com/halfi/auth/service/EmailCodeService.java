package com.halfi.auth.service;

import com.halfi.auth.exception.AuthException;
import com.halfi.auth.model.EmailVerification;
import com.halfi.auth.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class EmailCodeService {

    private final EmailVerificationRepository verificationRepository;

    // Читаем из application.yml
    @Value("${email-code.length:6}")
    private int codeLength;

    @Value("${email-code.ttl-minutes:10}")
    private int ttlMinutes;

    @Value("${email-code.cooldown-seconds:60}")
    private int cooldownSeconds;

    // Криптографически безопасный рандом для кодов
    private final SecureRandom random = new SecureRandom();

    /**
     * Генерирует и сохраняет код для email.
     * Возвращает оставшиеся секунды cooldown-а.
     * Бросает AuthException если cooldown ещё не прошёл.
     */
    @Transactional
    public int generateCode(String email) {
        // Проверяем cooldown — не слать коды чаще чем раз в 60 сек
        verificationRepository.findTopByEmailOrderBySentAtDesc(email)
                .ifPresent(last -> {
                    long secondsSinceLast = ChronoUnit.SECONDS.between(last.getSentAt(), LocalDateTime.now());
                    if (secondsSinceLast < cooldownSeconds) {
                        int left = (int) (cooldownSeconds - secondsSinceLast);
                        throw AuthException.codeCooldown(left);
                    }
                });

        // Генерируем код нужной длины (например 6 цифр → "047291")
        String code = generateNumericCode(codeLength);

        // Сохраняем в БД
        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .code(code)
                .sentAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(ttlMinutes))
                .used(false)
                .build();
        verificationRepository.save(verification);

        // TODO: здесь можно отправить код в Kafka → notification-service
        // authEventProducer.sendEmailCode(email, code);
        // Пока для теста просто выводим в лог:
        System.out.println("[DEV] Email code for " + email + ": " + code);

        return cooldownSeconds; // фронт получит { success: true, cooldownSeconds: 60 }
    }

    /**
     * Проверяет код.
     * Бросает AuthException если код неверен или истёк.
     */
    @Transactional
    public void verifyCode(String email, String code) {
        EmailVerification verification = verificationRepository
                .findTopByEmailAndUsedFalseOrderBySentAtDesc(email)
                .orElseThrow(AuthException::codeExpiredOrInvalid);

        // Проверяем срок жизни
        if (LocalDateTime.now().isAfter(verification.getExpiresAt())) {
            throw AuthException.codeExpiredOrInvalid();
        }

        // Проверяем сам код
        if (!verification.getCode().equals(code)) {
            throw AuthException.codeExpiredOrInvalid();
        }

        // Помечаем как использованный
        verification.setUsed(true);
        verificationRepository.save(verification);
    }

    // ── Приватные хелперы ─────────────────────────────────────────────────────

    private String generateNumericCode(int length) {
        // Генерируем N случайных цифр
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // 0..9
        }
        return sb.toString();
    }
}