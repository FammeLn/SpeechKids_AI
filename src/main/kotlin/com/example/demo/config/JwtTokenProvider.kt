package com.example.demo.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date   // ← именно этот, не java.sql.Date
@Component
class JwtTokenProvider {

    @Value("\${jwt.secret}")
    private lateinit var jwtSecret: String

    @Value("\${jwt.expiration:86400000}") // 24 часа
    private var jwtExpiration: Long = 86400000

    fun generateToken(email: String): String {
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()), SignatureAlgorithm.HS256)
            .compact()
    }

    fun getEmailFromToken(token: String): String =
        Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body.subject

    fun validateToken(token: String): Boolean = runCatching {
        Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .build()
            .parseClaimsJws(token)
        true
    }.getOrDefault(false)
}