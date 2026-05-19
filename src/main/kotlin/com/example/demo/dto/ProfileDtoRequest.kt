package com.example.demo.dto

import com.example.demo.Utilisator
import jakarta.validation.constraints.*

data class ProfileDtoRequest (
    @field:NotBlank val name: String = "",
    @field:NotBlank var surname: String = "",
    @field:NotBlank var fatherName: String = "",
    @field:NotBlank var number: String = "",
    @field:Email var email: String = "",
    @field:Size(min = 8) var password: String = "",

    @field:NotNull                    // ← изменил
    var role: Utilisator = Utilisator.PARENT
)
data class ProfileDtoResponse(
    val token: String,
    val name: String,
    var surname: String,
    var fatherName: String,
    var number: String,
    var email: String,
    var role: Utilisator
)