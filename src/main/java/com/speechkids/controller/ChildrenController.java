package com.speechkids.controller;

import com.speechkids.dto.ChildDto;
import com.speechkids.dto.CreateChildRequest;
import com.speechkids.dto.PhonemeScoreDto;
import com.speechkids.dto.ProgressDto;
import com.speechkids.dto.RecommendationDto;
import com.speechkids.dto.SessionDto;
import com.speechkids.service.ChildService;
import com.speechkids.service.SessionService;
import com.speechkids.service.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/children")
public class ChildrenController {
    private final ChildService childService;
    private final SessionService sessionService;

    public ChildrenController(ChildService childService, SessionService sessionService) {
        this.childService = childService;
        this.sessionService = sessionService;
    }

    @PostMapping
    public ChildDto createChild(@AuthenticationPrincipal UserPrincipal principal,
                                @Valid @RequestBody CreateChildRequest request) {
        return childService.createChild(principal.getId(), request);
    }

    @GetMapping
    public List<ChildDto> listChildren(@AuthenticationPrincipal UserPrincipal principal) {
        return childService.getChildren(principal);
    }

    @GetMapping("/{childId}")
    public ChildDto getChild(@PathVariable UUID childId,
                             @AuthenticationPrincipal UserPrincipal principal) {
        return childService.getChild(childId, principal);
    }

    @GetMapping("/{childId}/progress")
    public ProgressDto getProgress(@PathVariable UUID childId,
                                   @AuthenticationPrincipal UserPrincipal principal) {
        childService.getChild(childId, principal);
        return childService.getProgress(childId);
    }

    @GetMapping("/{childId}/phonemes")
    public List<PhonemeScoreDto> getPhonemes(@PathVariable UUID childId,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        childService.getChild(childId, principal);
        return childService.getPhonemeScores(childId);
    }

    @GetMapping("/{childId}/recommendations")
    public List<RecommendationDto> getRecommendations(@PathVariable UUID childId,
                                                      @AuthenticationPrincipal UserPrincipal principal) {
        childService.getChild(childId, principal);
        return childService.getRecommendations(childId);
    }

    @GetMapping("/{childId}/sessions")
    public List<SessionDto> getSessions(@PathVariable UUID childId,
                                        @AuthenticationPrincipal UserPrincipal principal) {
        childService.getChild(childId, principal);
        return sessionService.getSessionsByChild(childId);
    }
}
