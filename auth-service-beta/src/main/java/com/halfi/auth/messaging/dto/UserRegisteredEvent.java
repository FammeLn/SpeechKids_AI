package com.halfi.auth.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka-сообщение которое auth-service отправляет после регистрации.
 *
 * Topic: "user.regFROM nginx:alpine
 *
 * # Удаляем дефолтный конфиг
 * RUN rm /etc/nginx/conf.d/default.conf
 *
 * # Копируем наш конфиг
 * COPY nginx.conf /etc/nginx/nginx.conf
 *
 * # Проверка конфига при сборке
 * RUN nginx -t
 *
 * EXPOSE 80
 * CMD ["nginx", "-g", "daemon off;"]istered"
 * Consumers: user-service (создаёт профиль), notification-service (отправляет email)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {

    // ID созданного пользователя в auth-service БД
    private Long userId;

    private String email;
    private String nickName;

    // 6-значный код подтверждения для email
    // notification-service отправит письмо с этим кодом
    private String verificationCode;
}