package com.halfi.user.controller;

import com.halfi.user.dto.response.UserProfileResponse;
import com.halfi.user.exception.UserException;
import com.halfi.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

// НЕТ @CrossOrigin — CORS глобально в SecurityConfig
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;

    // GET /users/me  — требует JWT в заголовке Authorization: Bearer <token>
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMe(Authentication authentication) {
        if (authentication == null) throw UserException.unauthorized();

        // principal = userId (String) — установлен JwtAuthFilter
        Long authUserId = Long.parseLong((String) authentication.getPrincipal());
        return ResponseEntity.ok(userProfileService.getProfileByAuthUserId(authUserId));
    }

    // GET /users/health — публичный, для docker health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "user-service"));
    }
}
