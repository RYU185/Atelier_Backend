package com.dw.artgallery.controller;

import com.dw.artgallery.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/noti")
    public ResponseEntity<Void> testNotification() {
        notificationService.sendReservationReminder("steve12", "테스트 전시");
        return ResponseEntity.ok().build();
    }
}
