package com.example.demo.repository

import com.example.demo.entity.ChildProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChildProfileRepository : JpaRepository<ChildProfile, Long> {  // ← ChildProfile, не ChildProfileDtoRequest
    fun findAllByParentEmail(email: String): List<ChildProfile>
    fun findByIdAndParentEmail(id: Long, email: String): ChildProfile?
}