package com.halfi.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Email к которому привязан код
    @Column(nullable = false)
    private String email;

    // 6-значный код (например "847291")
    @Column(nullable = false, length = 10)
    private String code;

    // Когда истекает — проверяется в EmailCodeService
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // Когда был отправлен последний код — для cooldown (60 сек)
    @Column(nullable = false)
    private LocalDateTime sentAt;

    // Использован ли код (после verify-email-code = true)
    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;

    /*
     * Пример записи в БД:
     * id=1, email="user@mail.com", code="847291",
     * expiresAt=now+10min, sentAt=now, used=false
     *
     * После verify: used=true
     * Старые записи можно чистить шедулером (не обязательно для старта)
     */
}