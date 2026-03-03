package com.Halfi_core.service;

import com.Halfi_core.model.User;
import com.Halfi_core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Внедряем шифровальщик

    public User registerNewUser(User user) {
        // Шифруем пароль перед сохранением
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public User login(String email, String rawPassword) {
        // 1. Ищем пользователя
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // 2. Сравниваем введенный пароль с хэшем в базе
        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return user; // Пароли совпали
        } else {
            throw new RuntimeException("Неверный пароль");
        }
    }


}