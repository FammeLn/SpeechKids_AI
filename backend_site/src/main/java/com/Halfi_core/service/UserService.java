package com.Halfi_core.service;

import com.Halfi_core.model.User;
import com.Halfi_core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerNewUser(User user) {
        // Здесь в будущем мы добавим шифрование пароля
        // BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // user.setPassword(encoder.encode(user.getPassword()));

        return userRepository.save(user);
    }
}