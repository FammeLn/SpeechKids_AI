package com.speechkids.dto;

import com.speechkids.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CreateChildRequest(
        @NotBlank String name,
        LocalDate birthDate,
        @NotNull @Positive Integer age,
        @NotNull Gender gender,
        @NotBlank String nativeLanguage,
        @NotBlank String speechGoal
) {
}
