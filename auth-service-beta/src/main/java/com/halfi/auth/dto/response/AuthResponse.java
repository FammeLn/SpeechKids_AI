package com.halfi.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    // true = успех, false = ошибка
    private boolean ok;

    // Данные пользователя — null при ошибке
    private UserDTO user;

    // Токены — null при ошибке
    private TokensDTO tokens;

    // Причина ошибки — null при успехе
    // Допустимые значения: "credentials", "email", "password", "network"
    private String reason;

    // ── Вложенные DTO ──────────────────────────────────────────────────────────

    @Data
    @Builder
    public static class UserDTO {
        private String id;           // userId как строка
        private String email;
        private String avatarUrl;    // null если нет аватарки
        private Long balance;        // 0 при регистрации
        private String nickName;
    }

    @Data
    @Builder
    public static class TokensDTO {
        private String accessToken;   // короткоживущий, 15 мин
        private String refreshToken;  // долгоживущий, 7 дней
    }

    // ── Фабричные методы для удобства ─────────────────────────────────────────

    /**
     * Успешный ответ.
     * Пример: AuthResponse.success(user, "nickname", accessToken, refreshToken)
     */
    public static AuthResponse success(
            com.halfi.auth.model.User user,
            String nickName,
            String accessToken,
            String refreshToken
    ) {
        return AuthResponse.builder()
                .ok(true)
                .user(UserDTO.builder()
                        .id(user.getId().toString())
                        .email(user.getEmail())
                        .avatarUrl(null)   // у нас пока нет аватарки
                        .balance(0L)       // balance хранится в user-service
                        .nickName(nickName)
                        .build())
                .tokens(TokensDTO.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build())
                .build();
    }

    /**
     * Ошибочный ответ.
     * Пример: AuthResponse.failure("credentials")
     */
    public static AuthResponse failure(String reason) {
        return AuthResponse.builder()
                .ok(false)
                .reason(reason)
                .build();
    }
}