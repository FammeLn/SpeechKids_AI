package com.speechkids.dto;

import com.speechkids.enums.Role;
import com.speechkids.enums.UserStatus;

import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        String fullName,
        Role role,
        UserStatus status
) {
}
