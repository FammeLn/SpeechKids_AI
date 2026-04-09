package com.halfi.user.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    private final String errorCode;   // маппится в поле "error" ответа
    private final int httpStatus;

    public UserException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode  = errorCode;
        this.httpStatus = httpStatus;
    }

    // ── Фабричные методы ──────────────────────────────────────────

    public static UserException notFound(Long authUserId) {
        return new UserException(
                "user_not_found",
                "Профиль не найден для userId=" + authUserId,
                404
        );
    }

    public static UserException unauthorized() {
        return new UserException("unauthorized", "Требуется авторизация", 401);
    }
}
