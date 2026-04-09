package com.halfi.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // email — уникален, не может быть null
    @Column(unique = true, nullable = false)
    private String email;

    // Пароль всегда скрыт от сериализации в JSON
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    // true = аккаунт подтверждён через email-код
    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = false;

    // UUID-код подтверждения почты (потом удаляется или хранится в EmailVerification)
    // Оставляем для совместимости, основная логика — в EmailVerification
    private String verificationCode;

    /*
     * ВАЖНО: UserProfile больше НЕ здесь.
     * В микросервисной архитектуре профиль (nickname, avatarUrl, balance)
     * хранится в user-service со своей БД.
     * auth-service знает только: id, email, password, enabled.
     */
}