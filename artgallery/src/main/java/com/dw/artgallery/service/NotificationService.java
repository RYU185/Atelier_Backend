package com.dw.artgallery.service;

import com.dw.artgallery.DTO.InquiryNotification;
import com.dw.artgallery.DTO.ReservationNotificationDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Lazy
    @Autowired
    private NotificationService self;

    public void sendContactNotification(String name, String title) {
        InquiryNotification notification = new InquiryNotification(
                "새로운 문의가 도착했습니다: " + title,
                name
        );
        messagingTemplate.convertAndSend("/topic/inquiry", notification); // 관리자용 채널
    }

    public void sendReservationReminder(String userId, String galleryTitle) {
        log.info("알림 전송 시작 → userId={}, gallery={}", userId, galleryTitle);

        String title = "예약 알림";
        String message = String.format("내일 '%s' 전시가 예약되어 있습니다.", galleryTitle);

        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notifications",
                new ReservationNotificationDTO("예약 알림", "내일 '" + galleryTitle + "' 전시가 예약되어 있습니다.")
        );
    }
}
