package com.speechkids.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RecommendationDto(
        UUID id,
        UUID childId,
        String text,
        OffsetDateTime createdAt
) {
}
