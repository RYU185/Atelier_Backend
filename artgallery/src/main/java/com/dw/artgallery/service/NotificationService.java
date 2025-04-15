package com.dw.artgallery.service;

import com.dw.artgallery.DTO.InquiryNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendContactNotification(String name, String title) {
        InquiryNotification notification = new InquiryNotification(
                "새로운 문의가 도착했습니다: " + title,
                name
        );
        messagingTemplate.convertAndSend("/topic/inquiry", notification); // 관리자용 채널
    }
}
