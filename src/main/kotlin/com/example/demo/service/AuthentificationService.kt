package com.example.demo.service

import com.example.demo.Utilisator
import com.example.demo.config.JwtTokenProvider
import com.example.demo.dto.*
import com.example.demo.entity.*
import com.example.demo.repository.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthentificationService(
    private val profileRepository: ProfileRepository,
    private val childRepository: ChildProfileRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,          // добавить
) {

    fun registerProfile(request: ProfileDtoRequest): ProfileDtoResponse {
        if (profileRepository.existsByEmail(request.email))
            throw IllegalArgumentException("Email уже зарегистрирован: ${request.email}")

        if (profileRepository.existsByNumber(request.number))  // исправлена опечатка
            throw IllegalArgumentException("Номер уже зарегистрирован: ${request.number}")

        val user = Profile(
            name = request.name,
            surname = request.surname,
            fatherName = request.fatherName,
            number = request.number,
            email = request.email,
            password = passwordEncoder.encode(request.password),  // хэшируем пароль!
            role = request.role
        )

        profileRepository.save(user)
        val token = jwtTokenProvider.generateToken(user.email)
        return toResponse(user, token)
    }

    fun login(request: LoginDto): ProfileDtoResponse {
        val user = profileRepository.findByEmail(request.emailOrNumber)
            ?: profileRepository.findByNumber(request.emailOrNumber)
            ?: throw UsernameNotFoundException("Пользователь не найден")
        if (!passwordEncoder.matches(request.password, user.password))
            throw BadCredentialsException("Неверный пароль")
        val token = jwtTokenProvider.generateToken(user.email)
        return toResponse(user, token)
    }
    fun registerChild(request: ChildProfileDtoRequest, parentEmail: String): ChildProfileDtoResponse {
        val parent = profileRepository.findByEmail(parentEmail)
            ?: throw UsernameNotFoundException("Родитель не найден")
        if (parent.role != Utilisator.PARENT)
            throw AccessDeniedException("Только родитель может регистрировать ребёнка")
        val child = ChildProfile(
            name = request.name,
            surname = request.surname,
            diagnose = request.diagnose,
            language = request.language,
            parent = parent  // ← автоматом прикрепляем родителя
        )

        childRepository.save(child)

        return ChildProfileDtoResponse(
            id = child.id,
            name = child.name,
            surname = child.surname,
            parentName = "${parent.name} ${parent.surname}",  // ← имя родителя
            diagnose = child.diagnose,
            language = child.language
        )
    }
    private fun toResponse(user: Profile, token: String) = ProfileDtoResponse(
        token = token,
        name = user.name,
        surname = user.surname,
        fatherName = user.fatherName,
        number = user.number,
        email = user.email,
        role = user.role
    )
}