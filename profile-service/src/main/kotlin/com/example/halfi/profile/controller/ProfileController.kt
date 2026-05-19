package com.example.halfi.profile.controller

import com.example.halfi.profile.repository.ProfileRepository
import com.example.halfi.profile.dto.ProfileDto
import com.example.halfi.profile.dto.ProfileUpdateRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/profile")
class ProfileController(
    private val profileRepository: ProfileRepository
) {

    @GetMapping
    fun getAllProfiles(): ResponseEntity<List<ProfileDto>> {
        val profiles = profileRepository.findAll()
        val dtos = profiles.map { profile ->
            ProfileDto(
                userId = profile.userId,
                email = profile.email,
                nickname = profile.nickname,
                phoneNumber = profile.phoneNumber,
                avatarUrl = profile.avatarUrl,
                balance = profile.balance
            )
        }
        return ResponseEntity.ok(dtos)
    }


    @GetMapping("/{userId}")
    fun getProfile(@PathVariable userId: String): ResponseEntity<ProfileDto> {
        val profile = profileRepository.findByUserId(userId)
            ?: return ResponseEntity.notFound().build()
            
        val dto = ProfileDto(
            userId = profile.userId,
            email = profile.email,
            nickname = profile.nickname,
            phoneNumber = profile.phoneNumber,
            avatarUrl = profile.avatarUrl,
            balance = profile.balance
        )
        return ResponseEntity.ok(dto)
    }

    @PutMapping("/{userId}")
    fun updateProfile(
        @PathVariable userId: String,
        @RequestBody request: ProfileUpdateRequest
    ): ResponseEntity<ProfileDto> {
        val profile = profileRepository.findByUserId(userId)
            ?: return ResponseEntity.notFound().build()
            
        request.nickname?.let { profile.nickname = it }
        request.phoneNumber?.let { profile.phoneNumber = it }
        request.avatarUrl?.let { profile.avatarUrl = it }
        
        profileRepository.save(profile)
        
        val dto = ProfileDto(
            userId = profile.userId,
            email = profile.email,
            nickname = profile.nickname,
            phoneNumber = profile.phoneNumber,
            avatarUrl = profile.avatarUrl,
            balance = profile.balance
        )
        return ResponseEntity.ok(dto)
    }
}
