package com.speechkids.service;

import com.speechkids.dto.ExerciseDto;
import com.speechkids.dto.ExerciseItemDto;
import com.speechkids.entity.Exercise;
import com.speechkids.entity.ExerciseItem;
import com.speechkids.exception.NotFoundException;
import com.speechkids.repository.ExerciseItemRepository;
import com.speechkids.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseItemRepository exerciseItemRepository;

    public ExerciseService(ExerciseRepository exerciseRepository, ExerciseItemRepository exerciseItemRepository) {
        this.exerciseRepository = exerciseRepository;
        this.exerciseItemRepository = exerciseItemRepository;
    }

    public List<ExerciseDto> getExercises() {
        return exerciseRepository.findAll().stream().map(this::toDto).toList();
    }

    public ExerciseDto getExercise(UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException("Exercise not found"));
        return toDto(exercise);
    }

    public List<ExerciseItemDto> getItems(UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException("Exercise not found"));
        return exerciseItemRepository.findByExerciseId(exercise.getId()).stream().map(this::toItemDto).toList();
    }

    private ExerciseDto toDto(Exercise exercise) {
        return new ExerciseDto(
                exercise.getId(),
                exercise.getTitle(),
                exercise.getType(),
                exercise.getAgeMin(),
                exercise.getAgeMax(),
                exercise.getDescription(),
                exercise.isActive()
        );
    }

    private ExerciseItemDto toItemDto(ExerciseItem item) {
        return new ExerciseItemDto(
                item.getId(),
                item.getExercise().getId(),
                item.getImageUrl(),
                item.getTargetWord(),
                splitPhonemes(item.getTargetPhonemes()),
                item.getDifficulty()
        );
    }

    private List<String> splitPhonemes(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(v -> !v.isBlank())
                .toList();
    }
}
