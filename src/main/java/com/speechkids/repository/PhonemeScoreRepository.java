package com.speechkids.repository;

import com.speechkids.entity.PhonemeScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PhonemeScoreRepository extends JpaRepository<PhonemeScore, UUID> {
    @Query("select ps.phoneme, avg(ps.accuracy) from PhonemeScore ps where ps.attempt.child.id = :childId group by ps.phoneme")
    List<Object[]> averageByPhoneme(@Param("childId") UUID childId);
}
