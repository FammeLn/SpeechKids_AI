package com.speechkids.dto;

import com.speechkids.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email @NotBlank String email,
        @Size(min = 6, max = 72) @NotBlank String password,
        @NotBlank String fullName,
        @NotNull Role role
) {
}
