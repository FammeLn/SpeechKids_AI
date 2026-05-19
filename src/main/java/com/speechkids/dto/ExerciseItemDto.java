package com.speechkids.dto;

import com.speechkids.enums.Difficulty;

import java.util.List;
import java.util.UUID;

public record ExerciseItemDto(
        UUID id,
        UUID exerciseId,
        String imageUrl,
        String targetWord,
        List<String> targetPhonemes,
        Difficulty difficulty
) {
}
