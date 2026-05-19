package com.speechkids.repository;

import com.speechkids.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {
    List<Recommendation> findByChildIdOrderByCreatedAtDesc(UUID childId);
}
