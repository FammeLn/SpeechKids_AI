package com.Halfi_core.controller;

import com.Halfi_core.model.User;
import com.Halfi_core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173") // Разрешаем фронтенду доступ
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        System.out.println("Регистрация: " + user.getEmail());
        return userService.registerNewUser(user);
    }
}