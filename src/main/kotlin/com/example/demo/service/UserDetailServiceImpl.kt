package com.example.demo.service

import com.example.demo.repository.ProfileRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val profileRepository: ProfileRepository  // было userRepository — исправлено
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val user = profileRepository.findByEmail(email)  // исправлено
            ?: throw UsernameNotFoundException("Пользователь не найден: $email")

        return User
            .withUsername(user.email)
            .password(user.password)
            .roles(user.role.name)
            .build()
    }
}