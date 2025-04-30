package com.dw.artgallery.service;

import com.dw.artgallery.DTO.InquiryNotification;
import com.dw.artgallery.DTO.ReservationNotificationDTO;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.UserRepository;
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

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @Lazy
    @Autowired
    private NotificationService self;


    public void sendContactNotification(String name, String title) {
        InquiryNotification notification = new InquiryNotification(
                "새로운 문의가 도착했습니다: " + title,
                name
        );

        List<User> admins = userRepository.findAllByAuthority_AuthorityName("ROLE_ADMIN");
        for (User admin : admins) {
            messagingTemplate.convertAndSendToUser(
                    admin.getUserId(),          // 개별 사용자 식별자
                    "/queue/inquiry",           // 개인 큐 채널
                    notification
            );
        }
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
