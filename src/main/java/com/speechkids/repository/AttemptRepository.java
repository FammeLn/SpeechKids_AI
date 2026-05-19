package com.speechkids.repository;

import com.speechkids.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AttemptRepository extends JpaRepository<Attempt, UUID> {
    List<Attempt> findBySessionId(UUID sessionId);

    List<Attempt> findByChildId(UUID childId);

    @Query("select avg(a.score) from Attempt a where a.child.id = :childId")
    Double averageScoreByChild(@Param("childId") UUID childId);
}
