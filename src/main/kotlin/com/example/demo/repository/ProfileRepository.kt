package com.example.demo.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import com.example.demo.entity.Profile

@Repository
interface ProfileRepository: JpaRepository<Profile, Long>{
    fun findByEmail(email: String): Profile?
    fun findByNumber(number: String): Profile?
    fun existsByNumber(number: String): Boolean
    fun existsByEmail(email:String): Boolean
}