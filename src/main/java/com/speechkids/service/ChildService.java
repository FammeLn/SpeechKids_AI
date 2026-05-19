package com.speechkids.service;

import com.speechkids.dto.ChildDto;
import com.speechkids.dto.CreateChildRequest;
import com.speechkids.dto.PhonemeScoreDto;
import com.speechkids.dto.ProgressDto;
import com.speechkids.dto.RecommendationDto;
import com.speechkids.entity.Child;
import com.speechkids.entity.Session;
import com.speechkids.entity.User;
import com.speechkids.enums.ModelProfileStatus;
import com.speechkids.enums.Role;
import com.speechkids.exception.NotFoundException;
import com.speechkids.exception.UnauthorizedException;
import com.speechkids.repository.AttemptRepository;
import com.speechkids.repository.ChildRepository;
import com.speechkids.repository.PhonemeScoreRepository;
import com.speechkids.repository.RecommendationRepository;
import com.speechkids.repository.SessionRepository;
import com.speechkids.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ChildService {
    private final ChildRepository childRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final AttemptRepository attemptRepository;
    private final PhonemeScoreRepository phonemeScoreRepository;
    private final RecommendationRepository recommendationRepository;

    public ChildService(ChildRepository childRepository,
                        UserRepository userRepository,
                        SessionRepository sessionRepository,
                        AttemptRepository attemptRepository,
                        PhonemeScoreRepository phonemeScoreRepository,
                        RecommendationRepository recommendationRepository) {
        this.childRepository = childRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.attemptRepository = attemptRepository;
        this.phonemeScoreRepository = phonemeScoreRepository;
        this.recommendationRepository = recommendationRepository;
    }

    public ChildDto createChild(UUID parentId, CreateChildRequest request) {
        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Parent not found"));
        Child child = new Child();
        child.setParent(parent);
        child.setName(request.name());
        child.setAge(request.age());
        child.setBirthDate(request.birthDate());
        child.setGender(request.gender());
        child.setNativeLanguage(request.nativeLanguage());
        child.setSpeechGoal(request.speechGoal());
        child.setModelProfileStatus(ModelProfileStatus.COLLECTING);
        return toDto(childRepository.save(child));
    }

    public List<ChildDto> getChildren(UserPrincipal principal) {
        if (principal.getRole() == Role.SUPER_ADMIN || principal.getRole() == Role.THERAPIST) {
            return childRepository.findAll().stream().map(this::toDto).toList();
        }
        return childRepository.findByParentId(principal.getId()).stream().map(this::toDto).toList();
    }

    public ChildDto getChild(UUID childId, UserPrincipal principal) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new NotFoundException("Child not found"));
        if (principal.getRole() == Role.PARENT && !child.getParent().getId().equals(principal.getId())) {
            throw new UnauthorizedException("Access denied");
        }
        return toDto(child);
    }

    public ProgressDto getProgress(UUID childId) {
        List<Session> sessions = sessionRepository.findByChildId(childId);
        int totalSessions = sessions.size();
        int xp = sessions.stream().mapToInt(Session::getXpEarned).sum();
        int level = Math.max(1, xp / 100 + 1);
        Double avgScore = attemptRepository.averageScoreByChild(childId);
        int averageScore = avgScore == null ? 0 : (int) Math.round(avgScore);
        int streakDays = calculateStreakDays(sessions);

        List<PhonemeScoreDto> phonemeScores = getPhonemeScores(childId);
        List<String> strong = phonemeScores.stream()
                .filter(score -> score.accuracy() >= 80)
                .map(PhonemeScoreDto::phoneme)
                .toList();
        List<String> weak = phonemeScores.stream()
                .filter(score -> score.accuracy() < 50)
                .map(PhonemeScoreDto::phoneme)
                .toList();

        String recommendation = recommendationRepository.findByChildIdOrderByCreatedAtDesc(childId).stream()
                .findFirst()
                .map(r -> r.getText())
                .orElse(null);

        return new ProgressDto(childId, level, xp, streakDays, totalSessions, averageScore, weak, strong, recommendation);
    }

    public List<PhonemeScoreDto> getPhonemeScores(UUID childId) {
        List<Object[]> rows = phonemeScoreRepository.averageByPhoneme(childId);
        List<PhonemeScoreDto> results = new ArrayList<>();
        for (Object[] row : rows) {
            String phoneme = (String) row[0];
            Double avg = (Double) row[1];
            results.add(new PhonemeScoreDto(phoneme, avg == null ? 0 : (int) Math.round(avg)));
        }
        return results;
    }

    public List<RecommendationDto> getRecommendations(UUID childId) {
        return recommendationRepository.findByChildIdOrderByCreatedAtDesc(childId).stream()
                .map(r -> new RecommendationDto(r.getId(), r.getChild().getId(), r.getText(), r.getCreatedAt()))
                .toList();
    }

    private int calculateStreakDays(List<Session> sessions) {
        List<LocalDate> dates = sessions.stream()
                .map(session -> (session.getFinishedAt() != null ? session.getFinishedAt() : session.getStartedAt())
                        .atZoneSameInstant(ZoneOffset.UTC)
                        .toLocalDate())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        if (dates.isEmpty()) {
            return 0;
        }

        LocalDate current = LocalDate.now(ZoneOffset.UTC);
        int streak = 0;
        while (dates.contains(current)) {
            streak++;
            current = current.minusDays(1);
        }
        return streak;
    }

    public ChildDto toDto(Child child) {
        return new ChildDto(
                child.getId(),
                child.getParent().getId(),
                child.getName(),
                child.getAge(),
                child.getBirthDate(),
                child.getGender(),
                child.getNativeLanguage(),
                child.getSpeechGoal(),
                child.getModelProfileStatus()
        );
    }
}
