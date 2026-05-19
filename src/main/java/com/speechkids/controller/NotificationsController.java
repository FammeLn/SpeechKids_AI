package com.speechkids.controller;

import com.speechkids.dto.NotificationDto;
import com.speechkids.service.NotificationService;
import com.speechkids.service.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {
    private final NotificationService notificationService;

    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationDto> getNotifications(@AuthenticationPrincipal UserPrincipal principal) {
        return notificationService.getNotifications(principal.getId());
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationDto markRead(@PathVariable UUID notificationId,
                                    @AuthenticationPrincipal UserPrincipal principal) {
        return notificationService.markRead(notificationId, principal.getId());
    }
}
