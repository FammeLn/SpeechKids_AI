package com.halfi.auth.service

import com.halfi.auth.dto.AuthResponse
import com.halfi.auth.dto.RegisterRequest
import com.halfi.auth.entity.EmailAttempt
import com.halfi.auth.entity.User
import com.halfi.auth.repository.EmailAttemptRepository
import com.halfi.auth.repository.UserRepository
import com.halfi.dto.response.LoginResponse
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.Date

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val emailAttemptRepository: EmailAttemptRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val kafkaProducer: com.halfi.auth.kafka.KafkaProducer
) {

    private fun checkAndRecordRateLimit(email: String) {
        val oneHourAgo = Date(System.currentTimeMillis() - 60 * 60 * 1000)
        
        val recentCount = emailAttemptRepository.countByEmailAndRequestedAtAfter(email, oneHourAgo)
        val lastAttempt = emailAttemptRepository.findTopByEmailOrderByRequestedAtDesc(email)
        
        if (lastAttempt != null && recentCount > 0) {
            // Формула: (Количество попыток * 10 секунд). Максимум 60 секунд.
            val requiredWaitTimeSeconds = minOf(recentCount * 10, 60L)
            
            val timeSinceLastRequestMillis = System.currentTimeMillis() - lastAttempt.requestedAt.time
            val timeSinceLastRequestSeconds = timeSinceLastRequestMillis / 1000
            
            if (timeSinceLastRequestSeconds < requiredWaitTimeSeconds) {
                val waitMore = requiredWaitTimeSeconds - timeSinceLastRequestSeconds
                // Используем ResponseStatusException, чтобы возвращать красивый 429 Too Many Requests
                throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Пожалуйста, подождите $waitMore секунд перед следующей отправкой")
            }
        }
        
        emailAttemptRepository.save(EmailAttempt(email = email))
    }

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        val existingUser = userRepository.findByEmail(request.email)
        
        if (existingUser != null) {
            if (existingUser.enabled) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already taken")
            } else {
                // Гениальная схема: пользователь есть, но не подтвержден. Вместо ошибки просто высылаем новый код!
                checkAndRecordRateLimit(request.email)
                
                val code = (1000..9999).random().toString()
                existingUser.password = passwordEncoder.encode(request.password)
                existingUser.verificationCode = code
                existingUser.codeExpiresAt = Date(System.currentTimeMillis() + 15 * 60 * 1000)
                userRepository.save(existingUser)
                
                kafkaProducer.sendVerificationEmail(existingUser.email, code, "REGISTRATION")
                return AuthResponse(
                    id = existingUser.id!!,
                    email = existingUser.email,
                    message = "Verification code resent to unverified email."
                )
            }
        }

        checkAndRecordRateLimit(request.email)

        val code = (1000..9999).random().toString()
        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            verificationCode = code,
            codeExpiresAt = Date(System.currentTimeMillis() + 15 * 60 * 1000), // 15 минут
            enabled = false
        )

        val savedUser = userRepository.save(user)
        kafkaProducer.sendVerificationEmail(savedUser.email, code, "REGISTRATION")

        return AuthResponse(
            id = savedUser.id!!,
            email = savedUser.email,
            message = "User registered successfully. Please check your email for the verification code."
        )
    }

    @Transactional
    fun resendCode(email: String): AuthResponse {
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found")
            
        if (user.enabled) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Account is already verified. You can login.")
        }
        
        checkAndRecordRateLimit(email)
        
        val code = (1000..9999).random().toString()
        user.verificationCode = code
        user.codeExpiresAt = Date(System.currentTimeMillis() + 15 * 60 * 1000)
        userRepository.save(user)
        
        kafkaProducer.sendVerificationEmail(email, code, "REGISTRATION")
        
        return AuthResponse(
            id = user.id!!,
            email = user.email,
            message = "Verification code resent successfully"
        )
    }

    @Transactional
    fun forgotPassword(email: String): AuthResponse {
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found")
            
        checkAndRecordRateLimit(email)
        
        val code = (1000..9999).random().toString()
        user.verificationCode = code
        user.codeExpiresAt = Date(System.currentTimeMillis() + 15 * 60 * 1000)
        userRepository.save(user)
        
        kafkaProducer.sendVerificationEmail(email, code, "RESET_PASSWORD")
        
        return AuthResponse(
            id = user.id!!,
            email = user.email,
            message = "Password reset link sent to email"
        )
    }
    
    @Transactional
    fun resetPassword(email: String, code: String, newPassword: String): AuthResponse {
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found")
            
        if (user.verificationCode != code || user.codeExpiresAt?.after(Date()) != true) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired code")
        }
        
        user.password = passwordEncoder.encode(newPassword)
        user.verificationCode = null
        user.codeExpiresAt = null
        user.enabled = true // Активируем аккаунт
        userRepository.save(user)
        
        return AuthResponse(
            id = user.id!!,
            email = user.email,
            message = "Password reset successfully. You can now login."
        )
    }

    @Transactional
    fun verifyCode(email: String, code: String): Boolean {
        val user = userRepository.findByEmail(email) ?: return false
        if (user.enabled) return true 
        if (user.verificationCode == code && user.codeExpiresAt?.after(Date()) == true) {
            user.enabled = true
            user.verificationCode = null
            user.codeExpiresAt = null
            userRepository.save(user)
            return true
        }
        return false
    }

    fun login(request: RegisterRequest): LoginResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password")
        }

        if (!user.enabled) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not verified. Please check your email.")
        }

        val accessToken = jwtService.generateAccessToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)

        user.refreshToken = refreshToken
        userRepository.save(user)

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = user.id.toString(),
            role = user.role
        )
    }
}