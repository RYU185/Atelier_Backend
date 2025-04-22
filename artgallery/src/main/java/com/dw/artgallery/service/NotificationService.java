package com.dw.artgallery.service;

import com.dw.artgallery.DTO.InquiryNotification;
import com.dw.artgallery.DTO.ReservationNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.MessageBuilder;
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
    private final SimpUserRegistry simpUserRegistry;

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

    @Async
    public void sendReservationReminderAsync(String userId, String galleryTitle) {
        log.info("[RAM] 예약 알림 @Async 트리거 실행됨!");
        sendReservationReminder(userId, galleryTitle);
    }

    public void sendReminderViaProxy(String userId, String title) {
        log.info("프록시 통해 async 진입 시도");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                self.sendReservationReminderAsync(userId, title);
            }
        }, 3000);
    }

    public void sendReservationReminder(String userId, String galleryTitle) {
        log.info("메시지 전송 대상 userId: {}", userId);

        boolean hasUser = simpUserRegistry.getUsers().stream()
                .anyMatch(user -> user.getName().equals(userId));

        if (!hasUser) {
            log.warn("대상 유저 [{}]가 현재 연결되어 있지 않음", userId);
        } else {
            log.info("대상 유저 [{}]가 연결되어 있음", userId);
        }

        // Map 형태의 메시지 그대로 전송
        Map<String, String> payload = Map.of(
                "title", "예약알림",
                "message", String.format("내일 '%s' 전시가 예약되어 있습니다.", galleryTitle)
        );
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", payload);
    }


    public void printActiveUsers() {
        simpUserRegistry.getUsers().forEach(user -> {
            log.info("📡 현재 연결된 사용자: {}", user.getName());
            user.getSessions().forEach(session -> {
                log.info("  └ 세션 ID: {}", session.getId());
            });
        });
    }
}
