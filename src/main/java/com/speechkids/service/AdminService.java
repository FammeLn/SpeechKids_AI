package com.speechkids.service;

import com.speechkids.dto.SystemHealthDto;
import com.speechkids.dto.UserDto;
import com.speechkids.entity.User;
import com.speechkids.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final AuthService authService;

    public AdminService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(authService::toUserDto)
                .toList();
    }

    public SystemHealthDto getSystemHealth() {
        return new SystemHealthDto(
                "faster-whisper-large-v3",
                "whisper.cpp base-q5_1",
                "UNKNOWN",
                0L,
                false
        );
    }
}
