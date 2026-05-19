package com.speechkids.controller;

import com.speechkids.dto.SessionDto;
import com.speechkids.dto.StartSessionRequest;
import com.speechkids.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
public class SessionsController {
    private final SessionService sessionService;

    public SessionsController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/start")
    public SessionDto startSession(@Valid @RequestBody StartSessionRequest request) {
        return sessionService.startSession(request);
    }

    @PostMapping("/{sessionId}/finish")
    public SessionDto finishSession(@PathVariable UUID sessionId) {
        return sessionService.finishSession(sessionId);
    }

    @GetMapping("/{sessionId}")
    public SessionDto getSession(@PathVariable UUID sessionId) {
        return sessionService.toDto(sessionService.getSession(sessionId));
    }
}
