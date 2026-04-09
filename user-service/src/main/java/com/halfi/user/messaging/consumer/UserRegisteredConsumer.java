package com.halfi.user.messaging.consumer;

import com.halfi.user.messaging.dto.UserRegisteredEvent;
import com.halfi.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredConsumer {

    private final UserProfileService userProfileService;

    @KafkaListener(topics = "user.registered", groupId = "user-service")
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("[Kafka] user.registered: userId={}, email={}",
                event.getUserId(), event.getEmail());
        try {
            userProfileService.createProfile(
                    event.getUserId(),
                    event.getEmail(),
                    event.getNickName()
            );
        } catch (Exception e) {
            // Не перебрасываем — иначе Kafka будет бесконечно retry
            log.error("[Kafka] Failed to create profile: {}", e.getMessage());
        }
    }
}
