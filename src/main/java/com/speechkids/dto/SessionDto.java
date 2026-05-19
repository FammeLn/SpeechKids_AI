package com.speechkids.dto;

import com.speechkids.enums.SessionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionDto(
        UUID id,
        UUID childId,
        UUID exerciseId,
        SessionStatus status,
        OffsetDateTime startedAt,
        OffsetDateTime finishedAt,
        Integer totalScore,
        Integer xpEarned
) {
}
