package com.speechkids.dto;

import java.util.List;
import java.util.UUID;

public record ProgressDto(
        UUID childId,
        Integer level,
        Integer xp,
        Integer streakDays,
        Integer totalSessions,
        Integer averageScore,
        List<String> weakPhonemes,
        List<String> strongPhonemes,
        String recommendation
) {
}
