package com.speechkids.controller;

import com.speechkids.dto.ExerciseDto;
import com.speechkids.dto.ExerciseItemDto;
import com.speechkids.service.ExerciseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exercises")
public class ExercisesController {
    private final ExerciseService exerciseService;

    public ExercisesController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    public List<ExerciseDto> getExercises() {
        return exerciseService.getExercises();
    }

    @GetMapping("/{exerciseId}")
    public ExerciseDto getExercise(@PathVariable UUID exerciseId) {
        return exerciseService.getExercise(exerciseId);
    }

    @GetMapping("/{exerciseId}/items")
    public List<ExerciseItemDto> getItems(@PathVariable UUID exerciseId) {
        return exerciseService.getItems(exerciseId);
    }
}
