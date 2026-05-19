package com.example.demo.dto

import com.example.demo.Language
import jakarta.validation.constraints.*

data class ChildProfileDtoRequest (
    @field:NotBlank var name: String = "",
    @field:NotBlank var surname: String = "",
    @field:NotBlank var diagnose: String = "",
    @field:NotBlank var language: Language = Language.RUSSIAN
)
data class ChildProfileDtoResponse(
    val id: Long,
    var name: String,
    var surname: String,
    var parentName: String,
    var diagnose: String,
    var language: Language
)