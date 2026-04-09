package com.halfi.auth.exception;

import lombok.Getter;

/**
 * Бросается при ошибках аутентификации.
 * reason — код для фронтенда: "credentials", "email", "password"
 */
@Getter
public class AuthException extends RuntimeException {

    // Код ошибки — фронтенд маппит его на UI
    private final String reason;

    // HTTP статус который вернёт GlobalExceptionHandler
    private final int httpStatus;

    public AuthException(String reason, String message) {
        super(message);
        this.reason = reason;
        this.httpStatus = 401; // по умолчанию Unauthorized
    }

    public AuthException(String reason, String message, int httpStatus) {
        super(message);
        this.reason = reason;
        this.httpStatus = httpStatus;
    }

    // ── Фабричные методы ──────────────────────────────────────────────────────

    public static AuthException emailNotFound() {
        return new AuthException("email", "Пользователь с таким email не найден");
    }

    public static AuthException wrongPassword() {
        return new AuthException("password", "Неверный пароль");
    }

    public static AuthException invalidCredentials() {
        return new AuthException("credentials", "Неверный email или пароль");
    }

    public static AuthException accountNotEnabled() {
        return new AuthException("email", "Аккаунт не подтверждён. Проверьте почту", 403);
    }

    public static AuthException emailAlreadyExists() {
        return new AuthException("email", "Email уже занят", 409);
    }

    public static AuthException nicknameAlreadyExists() {
        return new AuthException("nickname", "Никнейм уже занят", 409);
    }

    public static AuthException codeCooldown(int secondsLeft) {
        return new AuthException(
                "cooldown",
                "Подождите " + secondsLeft + " секунд перед повторной отправкой",
                429
        );
    }

    public static AuthException codeExpiredOrInvalid() {
        return new AuthException("code", "Код неверен или истёк", 400);
    }
}