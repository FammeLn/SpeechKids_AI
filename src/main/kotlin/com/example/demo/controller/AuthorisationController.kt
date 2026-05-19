package com.example.demo.controller

import com.example.demo.dto.ChildProfileDtoRequest
import com.example.demo.dto.ChildProfileDtoResponse
import com.example.demo.dto.LoginDto
import com.example.demo.dto.ProfileDtoRequest
import com.example.demo.dto.ProfileDtoResponse
import com.example.demo.service.AuthentificationService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.core.Authentication
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация")  // ← название группы в Swagger
class AuthorisationController(private val authentificationService: AuthentificationService) {

    @Operation(summary = "Регистрация профиля")
    @PostMapping("/register")
    fun register(@RequestBody @Valid request: ProfileDtoRequest): ResponseEntity<ProfileDtoResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(authentificationService.registerProfile(request))

    @Operation(summary = "Вход в систему")
    @PostMapping("/login")
    fun login(@RequestBody request: LoginDto): ResponseEntity<ProfileDtoResponse> =
        ResponseEntity.ok(authentificationService.login(request))

    @Operation(summary = "Регистрация ребёнка")
    @PostMapping("/child/register")
    fun registerChild(
        @RequestBody request: ChildProfileDtoRequest,
        authentication: Authentication
    ): ResponseEntity<ChildProfileDtoResponse> =
        ResponseEntity.ok(authentificationService.registerChild(request, authentication.name))
}