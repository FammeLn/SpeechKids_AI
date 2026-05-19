package com.speechkids.dto;

public record AuthResponse(
        UserDto user,
        String accessToken,
        String refreshToken
) {
}
