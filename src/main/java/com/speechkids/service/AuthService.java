package com.speechkids.service;

import com.speechkids.dto.AuthResponse;
import com.speechkids.dto.LoginRequest;
import com.speechkids.dto.RefreshRequest;
import com.speechkids.dto.RegisterRequest;
import com.speechkids.dto.UserDto;
import com.speechkids.entity.RefreshToken;
import com.speechkids.entity.User;
import com.speechkids.enums.UserStatus;
import com.speechkids.exception.BadRequestException;
import com.speechkids.exception.UnauthorizedException;
import com.speechkids.repository.RefreshTokenRepository;
import com.speechkids.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already registered");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setRole(request.role());
        user.setStatus(UserStatus.ACTIVE);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        User saved = userRepository.save(user);
        return issueTokens(saved);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        return issueTokens(user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token not found"));
        if (stored.getRevokedAt() != null) {
            throw new UnauthorizedException("Refresh token revoked");
        }
        if (stored.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new UnauthorizedException("Refresh token expired");
        }
        User user = stored.getUser();
        refreshTokenRepository.delete(stored);
        return issueTokens(user);
    }

    public void logout(String refreshToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not found"));
        stored.setRevokedAt(OffsetDateTime.now());
        refreshTokenRepository.save(stored);
    }

    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getFullName(), user.getRole(), user.getStatus());
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(refreshToken);
        token.setExpiresAt(jwtService.refreshTokenExpiry());
        refreshTokenRepository.save(token);
        return new AuthResponse(toUserDto(user), accessToken, refreshToken);
    }
}
