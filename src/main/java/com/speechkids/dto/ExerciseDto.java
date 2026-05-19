package com.speechkids.dto;

import com.speechkids.enums.ExerciseType;

import java.util.UUID;

public record ExerciseDto(
        UUID id,
        String title,
        ExerciseType type,
        Integer ageMin,
        Integer ageMax,
        String description,
        boolean isActive
) {
}
