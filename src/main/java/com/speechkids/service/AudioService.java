package com.speechkids.service;

import com.speechkids.config.AppProperties;
import com.speechkids.dto.AnalyzeResultDto;
import com.speechkids.dto.PhonemeScoreDto;
import com.speechkids.entity.Attempt;
import com.speechkids.entity.Child;
import com.speechkids.entity.ExerciseItem;
import com.speechkids.entity.PhonemeScore;
import com.speechkids.entity.Recommendation;
import com.speechkids.entity.Session;
import com.speechkids.enums.SessionMode;
import com.speechkids.exception.BadRequestException;
import com.speechkids.exception.NotFoundException;
import com.speechkids.repository.AttemptRepository;
import com.speechkids.repository.ChildRepository;
import com.speechkids.repository.ExerciseItemRepository;
import com.speechkids.repository.PhonemeScoreRepository;
import com.speechkids.repository.RecommendationRepository;
import com.speechkids.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AudioService {
    private final AppProperties appProperties;
    private final ChildRepository childRepository;
    private final SessionRepository sessionRepository;
    private final ExerciseItemRepository exerciseItemRepository;
    private final AttemptRepository attemptRepository;
    private final PhonemeScoreRepository phonemeScoreRepository;
    private final RecommendationRepository recommendationRepository;

    public AudioService(AppProperties appProperties,
                        ChildRepository childRepository,
                        SessionRepository sessionRepository,
                        ExerciseItemRepository exerciseItemRepository,
                        AttemptRepository attemptRepository,
                        PhonemeScoreRepository phonemeScoreRepository,
                        RecommendationRepository recommendationRepository) {
        this.appProperties = appProperties;
        this.childRepository = childRepository;
        this.sessionRepository = sessionRepository;
        this.exerciseItemRepository = exerciseItemRepository;
        this.attemptRepository = attemptRepository;
        this.phonemeScoreRepository = phonemeScoreRepository;
        this.recommendationRepository = recommendationRepository;
    }

    public AnalyzeResultDto analyze(MultipartFile audio,
                                    UUID childId,
                                    UUID sessionId,
                                    UUID exerciseItemId,
                                    String targetWord,
                                    SessionMode mode) {
        if (audio == null || audio.isEmpty()) {
            throw new BadRequestException("Audio file is required");
        }
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new NotFoundException("Child not found"));
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session not found"));
        if (!session.getChild().getId().equals(child.getId())) {
            throw new BadRequestException("Session does not belong to child");
        }
        ExerciseItem item = exerciseItemRepository.findById(exerciseItemId)
                .orElseThrow(() -> new NotFoundException("Exercise item not found"));

        String recognizedText = targetWord;
        boolean isCorrect = recognizedText.equalsIgnoreCase(targetWord);
        int score = isCorrect ? 90 : 60;
        int xpEarned = isCorrect ? 10 : 5;
        List<String> problemPhonemes = new ArrayList<>();
        List<PhonemeScoreDto> phonemeScores = buildPhonemeScores(item.getTargetPhonemes());

        Attempt attempt = new Attempt();
        attempt.setSession(session);
        attempt.setChild(child);
        attempt.setExerciseItem(item);
        attempt.setTargetWord(targetWord);
        attempt.setRecognizedText(recognizedText);
        attempt.setCorrect(isCorrect);
        attempt.setScore(score);
        attempt.setXpEarned(xpEarned);
        attempt.setProblemPhonemes(String.join(",", problemPhonemes));
        attempt.setRecommendation(isCorrect
                ? "Продолжайте тренировать звук " + firstPhoneme(item.getTargetPhonemes())
                : "Попробуйте ещё раз");
        attempt.setProcessingTimeMs(1200L);
        attempt.setMode(mode);
        Attempt savedAttempt = attemptRepository.save(attempt);

        String audioUrl = storeAudio(savedAttempt.getId(), audio);
        savedAttempt.setAudioUrl(audioUrl);
        attemptRepository.save(savedAttempt);

        for (PhonemeScoreDto scoreDto : phonemeScores) {
            PhonemeScore phonemeScore = new PhonemeScore();
            phonemeScore.setAttempt(savedAttempt);
            phonemeScore.setPhoneme(scoreDto.phoneme());
            phonemeScore.setAccuracy(scoreDto.accuracy());
            phonemeScoreRepository.save(phonemeScore);
        }

        if (isCorrect) {
            Recommendation recommendation = new Recommendation();
            recommendation.setChild(child);
            recommendation.setText("Продолжайте тренировать звук " + firstPhoneme(item.getTargetPhonemes()));
            recommendationRepository.save(recommendation);
        }

        String message = isCorrect ? "Молодец!" : "Попробуй ещё!";
        return new AnalyzeResultDto(
                savedAttempt.getId(),
                sessionId,
                childId,
                exerciseItemId,
                targetWord,
                recognizedText,
                isCorrect,
                score,
                xpEarned,
                problemPhonemes,
                phonemeScores,
                message,
                savedAttempt.getRecommendation(),
                audioUrl,
                savedAttempt.getProcessingTimeMs(),
                mode
        );
    }

    private String storeAudio(UUID attemptId, MultipartFile audio) {
        try {
            Path dir = Path.of(appProperties.getAudioStorageDir());
            Files.createDirectories(dir);
            Path target = dir.resolve(attemptId + ".wav");
            Files.copy(audio.getInputStream(), target);
            return "/api/audio/" + attemptId;
        } catch (IOException ex) {
            throw new BadRequestException("Failed to store audio");
        }
    }

    private List<PhonemeScoreDto> buildPhonemeScores(String rawPhonemes) {
        List<PhonemeScoreDto> results = new ArrayList<>();
        if (rawPhonemes == null || rawPhonemes.isBlank()) {
            return results;
        }
        String[] parts = rawPhonemes.split(",");
        for (String part : parts) {
            String phoneme = part.trim();
            if (!phoneme.isBlank()) {
                results.add(new PhonemeScoreDto(phoneme, 86));
            }
        }
        return results;
    }

    private String firstPhoneme(String rawPhonemes) {
        if (rawPhonemes == null || rawPhonemes.isBlank()) {
            return "";
        }
        String[] parts = rawPhonemes.split(",");
        return parts.length > 0 ? parts[0].trim() : "";
    }
}
