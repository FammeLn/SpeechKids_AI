package com.speechkids.service;

import com.speechkids.dto.SessionDto;
import com.speechkids.dto.StartSessionRequest;
import com.speechkids.entity.Attempt;
import com.speechkids.entity.Child;
import com.speechkids.entity.Exercise;
import com.speechkids.entity.Session;
import com.speechkids.enums.SessionStatus;
import com.speechkids.exception.NotFoundException;
import com.speechkids.repository.AttemptRepository;
import com.speechkids.repository.ChildRepository;
import com.speechkids.repository.ExerciseRepository;
import com.speechkids.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final ChildRepository childRepository;
    private final ExerciseRepository exerciseRepository;
    private final AttemptRepository attemptRepository;

    public SessionService(SessionRepository sessionRepository,
                          ChildRepository childRepository,
                          ExerciseRepository exerciseRepository,
                          AttemptRepository attemptRepository) {
        this.sessionRepository = sessionRepository;
        this.childRepository = childRepository;
        this.exerciseRepository = exerciseRepository;
        this.attemptRepository = attemptRepository;
    }

    public SessionDto startSession(StartSessionRequest request) {
        Child child = childRepository.findById(request.childId())
                .orElseThrow(() -> new NotFoundException("Child not found"));
        Exercise exercise = exerciseRepository.findById(request.exerciseId())
                .orElseThrow(() -> new NotFoundException("Exercise not found"));
        Session session = new Session();
        session.setChild(child);
        session.setExercise(exercise);
        session.setStatus(SessionStatus.ACTIVE);
        session.setMode(request.mode());
        session.setStartedAt(OffsetDateTime.now(ZoneOffset.UTC));
        session.setTotalScore(0);
        session.setXpEarned(0);
        return toDto(sessionRepository.save(session));
    }

    public SessionDto finishSession(UUID sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session not found"));
        session.setStatus(SessionStatus.FINISHED);
        session.setFinishedAt(OffsetDateTime.now(ZoneOffset.UTC));
        List<Attempt> attempts = attemptRepository.findBySessionId(sessionId);
        int totalScore = 0;
        int xp = 0;
        if (!attempts.isEmpty()) {
            totalScore = (int) Math.round(attempts.stream().mapToInt(Attempt::getScore).average().orElse(0));
            xp = attempts.stream().mapToInt(Attempt::getXpEarned).sum();
        }
        session.setTotalScore(totalScore);
        session.setXpEarned(xp);
        return toDto(sessionRepository.save(session));
    }

    public List<SessionDto> getSessionsByChild(UUID childId) {
        return sessionRepository.findByChildId(childId).stream()
                .map(this::toDto)
                .toList();
    }

    public Session getSession(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session not found"));
    }

    public SessionDto toDto(Session session) {
        return new SessionDto(
                session.getId(),
                session.getChild().getId(),
                session.getExercise().getId(),
                session.getStatus(),
                session.getStartedAt(),
                session.getFinishedAt(),
                session.getTotalScore(),
                session.getXpEarned()
        );
    }
}
