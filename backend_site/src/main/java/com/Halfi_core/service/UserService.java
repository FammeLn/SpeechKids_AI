package com.Halfi_core.service;

import com.Halfi_core.dto.request.RegisterRequest;
import com.Halfi_core.messaging.dto.UserRegisteredEvent;
import com.Halfi_core.model.User;
import com.Halfi_core.model.UserProfile;
import com.Halfi_core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Внедряем шифровальщик

    @Transactional
    public void register(RegisterRequest request) {
        // 1. Проверка, не занят ли email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email уже занят");
        }

        // 2. Создаем пользователя
        User user = new User();
        user.setEmail(request.getEmail());
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);


      //  user.setVerificationCode(java.util.UUID.randomUUID().toString());

        // 3. Создаем профиль
        UserProfile profile = new UserProfile();
        profile.setNickname(request.getNickname());

        // Связываем их (двусторонняя связь)
        profile.setUser(user);
        user.setProfile(profile);

        // 4. Сохраняем (UserProfile сохранится автоматически из-за CascadeType.ALL)
        userRepository.save(user);

//         5. Подготовка данных для Kafka
//        UserRegisteredEvent event = new UserRegisteredEvent(
//                user.getEmail(),
//                profile.getNickname(),
//                user.getVerificationCode()
//        );
//
//         Здесь будет вызов kafkaTemplate.send(...)
//        System.out.println("Событие отправлено в Kafka для: " + event.getEmail());
    }



    public User login(String email, String rawPassword) {
        // 1. Ищем пользователя по email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // 2. Проверяем зашифрованный пароль
        // matches(обычный_пароль_с_фронта, зашифрованный_пароль_из_базы)
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }

        // 3. Проверяем, подтвержден ли аккаунт
        if (!user.isEnabled()) {
            throw new RuntimeException("Аккаунт не подтвержден");
        }

        return user;
    }
}