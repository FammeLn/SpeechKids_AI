package com.example.halfi.profile.repository

import com.example.halfi.profile.domain.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfileRepository : JpaRepository<Profile, String> {
    fun findByUserId(userId: String): Profile?
}
