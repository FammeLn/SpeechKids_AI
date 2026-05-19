package com.speechkids.dto;

import com.speechkids.enums.Gender;
import com.speechkids.enums.ModelProfileStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ChildDto(
        UUID id,
        UUID parentId,
        String name,
        Integer age,
        LocalDate birthDate,
        Gender gender,
        String nativeLanguage,
        String speechGoal,
        ModelProfileStatus modelProfileStatus
) {
}
