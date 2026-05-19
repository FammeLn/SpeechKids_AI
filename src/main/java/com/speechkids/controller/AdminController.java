package com.speechkids.controller;

import com.speechkids.dto.SystemHealthDto;
import com.speechkids.dto.UserDto;
import com.speechkids.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/system-health")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public SystemHealthDto getSystemHealth() {
        return adminService.getSystemHealth();
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<UserDto> getUsers() {
        return adminService.getUsers();
    }
}
