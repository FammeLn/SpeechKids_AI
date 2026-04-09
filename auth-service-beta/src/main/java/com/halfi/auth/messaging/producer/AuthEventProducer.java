package com.halfi.auth.messaging.producer;

import com.halfi.auth.messaging.dto.UserRegisteredEvent;
import com.halfi.auth.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthEventProducer {

    // KafkaTemplate<KEY, VALUE> — KEY = userId как строка, VALUE = событие
    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    // Название топика — одно место, легко менять
    private static final String TOPIC_USER_REGISTERED = "user.registered";

    /**
     * Отправляет событие о регистрации нового пользователя.
     * Вызывается из AuthService.register() после сохранения в БД.
     */
    public void sendUserRegistered(User user, String nickName, String verificationCode) {
        UserRegisteredEvent event = new UserRegisteredEvent(
                user.getId(),
                user.getEmail(),
                nickName,
                verificationCode
        );

        // send(topic, key, value)
        // key = userId — гарантирует что все события одного юзера в одной партиции
        kafkaTemplate.send(TOPIC_USER_REGISTERED, user.getId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("[Kafka] Sent user.registered for email={}", user.getEmail());
                    } else {
                        // Не падаем — регистрация уже прошла, просто логируем
                        log.error("[Kafka] Failed to send user.registered: {}", ex.getMessage());
                    }
                });
    }
}