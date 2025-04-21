package com.dw.artgallery.service;

import com.dw.artgallery.model.Reservation;
import com.dw.artgallery.repository.ReservationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // 매일 오전 9시 실행!!!
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void sendReservationReminder(){
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        log.info("[D-1알림] {} 기준 예약자 조회", tomorrow);

        List<Reservation> reservations = reservationRepository.findReservedByReserveDate(tomorrow);

        for (Reservation reservation : reservations){
            String userId = reservation.getUser().getUserId();
            String galleryTitle = reservation.getReserveDate().getArtistGallery().getTitle();

            notificationService.sendReservationReminder(userId, galleryTitle);
            log.info("예약 D-1 알림 전송 → userId={}, gallery={}" , userId, galleryTitle);
        }
        log.info("[D-1 예약 알림] 총 {}건 전송 완료", reservations.size());
    }

    @PostConstruct
    public void testManualTrigger() {
        log.info("🔧 테스트용 예약 알림 수동 실행 시작");
        sendReservationReminder();
    }

}
