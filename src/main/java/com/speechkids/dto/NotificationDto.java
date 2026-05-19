package com.speechkids.dto;

import com.speechkids.enums.NotificationType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        String title,
        String message,
        NotificationType type,
        boolean isRead,
        OffsetDateTime createdAt
) {
}
