package com.halfi.auth.controller;

import com.halfi.auth.dto.request.EmailCodeRequest;
import com.halfi.auth.dto.request.LoginRequest;
import com.halfi.auth.dto.request.RegisterRequest;
import com.halfi.auth.dto.response.AuthResponse;
import com.halfi.auth.service.AuthService;
import com.halfi.auth.service.EmailCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Все endpoints публичны (настроено в SecurityConfig).
 * НЕТ @CrossOrigin — CORS настроен глобально в SecurityConfig.corsConfigurationSource()
 */
@RestController
@RequestMapping("/auth")   // маршруты: /auth/login, /auth/register, ...
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailCodeService emailCodeService;

    // ── POST /auth/login ──────────────────────────────────────────────────────
    /**
     * Фронт ожидает:
     * { ok: true, user: { id, email, avatarUrl, balance, nickName }, tokens: { accessToken, refreshToken } }
     * или { ok: false, reason: "credentials" | "email" | "password" }
     *
     * GlobalExceptionHandler перехватит AuthException и вернёт правильный формат.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // ── POST /auth/register ───────────────────────────────────────────────────
    /**
     * Фронт ожидает тот же формат что и login (user + tokens).
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    // ── POST /auth/send-email-code ────────────────────────────────────────────
    /**
     * Фронт ожидает: { success: true, cooldownSeconds: 60 }
     * Тело запроса: { email: "..." }
     */
    @PostMapping("/send-email-code")
    public ResponseEntity<Map<String, Object>> sendEmailCode(
            @Valid @RequestBody EmailCodeRequest request
    ) {
        int cooldown = emailCodeService.generateCode(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "cooldownSeconds", cooldown
        ));
    }

    // ── POST /auth/verify-email-code ──────────────────────────────────────────
    /**
     * Фронт ожидает: { success: true }
     * Тело запроса: { email: "...", code: "123456" }
     */
    @PostMapping("/verify-email-code")
    public ResponseEntity<Map<String, Boolean>> verifyEmailCode(
            @Valid @RequestBody EmailCodeRequest request
    ) {
        emailCodeService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── POST /auth/forgot-password ────────────────────────────────────────────
    /**
     * Фронт ожидает: всегда OK, не важно существует ли email.
     * Тело запроса: { email: "..." }
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Boolean>> forgotPassword(
            @RequestBody EmailCodeRequest request
    ) {
        authService.forgotPassword(request.getEmail());
        // Всегда возвращаем OK — не раскрываем существование email
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // ── GET /auth/health ──────────────────────────────────────────────────────
    /**
     * Простая проверка что сервис жив.
     * Удобно для docker health check и отладки.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "auth-service"));
    }
}