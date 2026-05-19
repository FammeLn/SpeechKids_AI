package com.speechkids.repository;

import com.speechkids.entity.ExerciseItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExerciseItemRepository extends JpaRepository<ExerciseItem, UUID> {
    List<ExerciseItem> findByExerciseId(UUID exerciseId);
}
