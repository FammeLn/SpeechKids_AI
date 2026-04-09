package com.halfi.auth.service

import com.halfi.auth.entity.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService {
    // В будущем вынесем в application.yml (и он должен быть минимум 256 бит)
    private val secret = "your-very-secure-secret-key-that-must-be-very-long" 
    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateAccessToken(user: User): String {
        return Jwts.builder()
            .setSubject(user.email)
            .claim("userId", user.id.toString())
            .claim("role", user.role)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 минут
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun generateRefreshToken(user: User): String {
        return Jwts.builder()
            .setSubject(user.email)
            .claim("userId", user.id.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30)) // 30 дней
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }
}