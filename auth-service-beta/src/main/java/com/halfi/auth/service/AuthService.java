package com.halfi.auth.service;

import com.halfi.auth.dto.request.LoginRequest;
import com.halfi.auth.dto.request.RegisterRequest;
import com.halfi.auth.dto.response.AuthResponse;
import com.halfi.auth.exception.AuthException;
import com.halfi.auth.messaging.producer.AuthEventProducer;
import com.halfi.auth.model.User;
import com.halfi.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor  // генерирует конструктор для всех final полей
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthEventProducer authEventProducer;

    // ── Login ──────────────────────────────────────────────────────────────────

    /**
     * Авторизация пользователя.
     * Возвращает AuthResponse с токенами или throws AuthException.
     */
    public AuthResponse login(LoginRequest request) {
        // 1. Ищем пользователя — если нет, бросаем "email" reason
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(AuthException::emailNotFound);

        // 2. Проверяем пароль — BCrypt сравнение
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw AuthException.wrongPassword();
        }

        // 3. Проверяем подтверждён ли аккаунт
        if (!user.isEnabled()) {
            throw AuthException.accountNotEnabled();
        }

        // 4. Генерируем токены
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // 5. nickname берём из user-service через Kafka или пока возвращаем email-часть
        // TODO: когда будет feign/grpc к user-service — запрашивать реальный nickname
        String nickName = extractNicknameFromEmail(user.getEmail());

        return AuthResponse.success(user, nickName, accessToken, refreshToken);
    }

    // ── Register ───────────────────────────────────────────────────────────────

    /**
     * Регистрация нового пользователя.
     * Сохраняет в БД, отправляет событие в Kafka, возвращает токены.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Проверка дублей email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw AuthException.emailAlreadyExists();
        }

        // 2. Генерируем код верификации (пока автоматически enabled=true для простоты)
        // Когда подключим email — enabled=false, код через Kafka → notification-service
        String verificationCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // 3. Создаём пользователя
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)          // TODO: поменять на false когда подключим email-верификацию
                .verificationCode(verificationCode)
                .build();

        User savedUser = userRepository.save(user);

        // 4. Отправляем событие в Kafka → user-service создаст профиль с nickname
        authEventProducer.sendUserRegistered(savedUser, request.getNickName(), verificationCode);

        // 5. Генерируем токены сразу (если enabled=true)
        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return AuthResponse.success(savedUser, request.getNickName(), accessToken, refreshToken);
    }

    // ── Forgot Password ────────────────────────────────────────────────────────

    /**
     * Восстановление пароля.
     * ВАЖНО: всегда возвращает OK — не раскрываем существование email.
     */
    public void forgotPassword(String email) {
        // Проверяем тихо — не бросаем исключение если email не найден
        if (userRepository.existsByEmail(email)) {
            // TODO: отправить письмо через Kafka → notification-service
            // String resetToken = UUID.randomUUID().toString();
            // authEventProducer.sendPasswordReset(email, resetToken);
            System.out.println("[DEV] Password reset requested for: " + email);
        }
        // Если email не найден — ничего не делаем, ответ всегда одинаковый
    }

    // ── Хелперы ───────────────────────────────────────────────────────────────

    // Временный хелпер пока нет связи с user-service
    private String extractNicknameFromEmail(String email) {
        return email.split("@")[0];
    }
}