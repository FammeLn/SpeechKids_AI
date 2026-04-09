package com.halfi.auth.service;

import com.halfi.auth.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // Читаем из application.yml: jwt.secret
    @Value("${jwt.secret}")
    private String secret;

    // Читаем из application.yml: jwt.access-expiry-ms (900000 = 15 мин)
    @Value("${jwt.access-expiry-ms}")
    private long accessExpiryMs;

    // Читаем из application.yml: jwt.refresh-expiry-ms (604800000 = 7 дней)
    @Value("${jwt.refresh-expiry-ms}")
    private long refreshExpiryMs;

    // ── Генерация ─────────────────────────────────────────────────────────────

    /**
     * Создаёт короткоживущий accessToken (15 мин).
     * Payload: sub=userId, email=user@mail.com, type=access
     */
    public String generateAccessToken(User user) {
        return buildToken(user, accessExpiryMs, "access");
    }

    /**
     * Создаёт долгоживущий refreshToken (7 дней).
     * Payload: sub=userId, type=refresh
     */
    public String generateRefreshToken(User user) {
        return buildToken(user, refreshExpiryMs, "refresh");
    }

    private String buildToken(User user, long expiryMs, String type) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expiryMs);

        return Jwts.builder()
                .subject(user.getId().toString())          // sub = userId
                .claim("email", user.getEmail())           // доп. клейм
                .claim("type", type)                       // access | refresh
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSignKey())                    // HS256 подпись
                .compact();
    }

    // ── Валидация ─────────────────────────────────────────────────────────────

    /**
     * Парсит и валидирует токен.
     * Бросает JwtException если токен невалиден или просрочен.
     */
    public Claims validateToken(String token) {
        // parseSignedClaims — в jjwt 0.12.x (раньше было parseClaimsJws)
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Возвращает userId из токена без проброса исключения.
     * Возвращает null если токен невалиден.
     */
    public String extractUserId(String token) {
        try {
            return validateToken(token).getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * Возвращает email из токена.
     */
    public String extractEmail(String token) {
        try {
            return validateToken(token).get("email", String.class);
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * Проверяет что токен валиден (не бросает исключение).
     */
    public boolean isTokenValid(String token) {
        try {
            validateToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // ── Приватный хелпер ──────────────────────────────────────────────────────

    private SecretKey getSignKey() {
        // Keys.hmacShaKeyFor требует минимум 32 байта для HS256
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}