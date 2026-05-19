package com.speechkids.dto;

import com.speechkids.enums.SessionMode;

import java.util.List;
import java.util.UUID;

public record AnalyzeResultDto(
        UUID attemptId,
        UUID sessionId,
        UUID childId,
        UUID exerciseItemId,
        String targetWord,
        String recognizedText,
        boolean isCorrect,
        Integer score,
        Integer xpEarned,
        List<String> problemPhonemes,
        List<PhonemeScoreDto> phonemeScores,
        String message,
        String recommendation,
        String audioUrl,
        Long processingTimeMs,
        SessionMode mode
) {
}
