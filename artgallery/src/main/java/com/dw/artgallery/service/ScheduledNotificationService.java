package com.dw.artgallery.service;

import com.dw.artgallery.DTO.ReservationNotificationDTO;
import com.dw.artgallery.model.Reservation;
import com.dw.artgallery.repository.ReservationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledNotificationService {
    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry simpUserRegistry;

    // 매일 오전 9시 실행!!!
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void sendReservationReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        log.info("🔔 [예약 알림] {} 기준 예약자 조회", tomorrow);

        List<Reservation> reservations = reservationRepository.findReservedByReserveDate(tomorrow);

        for (Reservation reservation : reservations) {
            String userId = reservation.getUser().getUserId();
            String galleryTitle = reservation.getReserveDate().getArtistGallery().getTitle();

            String title = "예약 알림";
            String message = String.format("내일 '%s' 전시가 예약되어 있습니다.", galleryTitle);
            ReservationNotificationDTO dto = new ReservationNotificationDTO(title, message);

            SimpUser user = simpUserRegistry.getUser(userId);
            if (user != null) {
                for (SimpSession session : user.getSessions()) {
                    messagingTemplate.convertAndSendToUser(
                            session.getUser().getName(),
                            "/queue/notifications",
                            dto
                    );
                    log.info("알림 전송 → session={}, userId={}", session.getId(), userId);
                }
            } else {
                log.warn("WebSocket 연결 안됨 → userId={}", userId);
            }
        }

        log.info("총 {}건의 예약 알림 전송 완료", reservations.size());
    }
}