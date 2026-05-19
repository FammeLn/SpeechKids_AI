package com.speechkids.service;

import com.speechkids.config.JwtProperties;
import com.speechkids.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    public String generateAccessToken(User user) {
        return generateToken(user, properties.getAccessTokenMinutes() * 60L);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, properties.getRefreshTokenDays() * 24L * 60L * 60L);
    }

    public OffsetDateTime refreshTokenExpiry() {
        return OffsetDateTime.now(ZoneOffset.UTC).plusDays(properties.getRefreshTokenDays());
    }

    public String extractUsername(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (Exception ex) {
            return null;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private String generateToken(User user, long expiresInSeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getEmail())
                .addClaims(Map.of(
                        "role", user.getRole().name(),
                        "userId", user.getId().toString()
                ))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expiresInSeconds)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
