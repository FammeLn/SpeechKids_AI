package com.halfi.auth.controller

import com.halfi.auth.dto.AuthResponse
import com.halfi.auth.dto.RegisterRequest
import com.halfi.auth.service.AuthService
import com.halfi.dto.response.LoginResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.HttpServletResponse

data class EmailRequest(val email: String)
data class ResetPasswordRequest(val email: String, val code: String, val newPassword: String)

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): AuthResponse {
        return authService.register(request)
    }

    @PostMapping("/resend-code")
    fun resendCode(@RequestBody request: EmailRequest): AuthResponse {
        return authService.resendCode(request.email)
    }

    @PostMapping("/forgot-password")
    fun forgotPassword(@RequestBody request: EmailRequest): AuthResponse {
        return authService.forgotPassword(request.email)
    }

    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody request: ResetPasswordRequest): AuthResponse {
        return authService.resetPassword(request.email, request.code, request.newPassword)
    }

    @PostMapping("/verify")
    fun verify(@RequestParam email: String, @RequestParam code: String): ResponseEntity<String> {
        val isVerified = authService.verifyCode(email, code)
        return if (isVerified) {
            ResponseEntity.ok("Email verified successfully. You can now login.")
        } else {
            ResponseEntity.badRequest().body("Invalid or expired code")
        }
    }

    @GetMapping("/test")
    fun test() = "Auth Service is up and running!"

    @PostMapping("/login")
    fun login(
        @RequestBody request: RegisterRequest, 
        response: HttpServletResponse
    ): LoginResponse {
        val loginResponse = authService.login(request)
        
        val cookie = ResponseCookie.from("refreshToken", loginResponse.refreshToken)
            .httpOnly(true)
            .secure(false) // В production нужно исправить на true для HTTPS
            .path("/")
            .maxAge(30L * 24 * 60 * 60) // 30 дней
            .build()
            
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
        
        return loginResponse
    }
}