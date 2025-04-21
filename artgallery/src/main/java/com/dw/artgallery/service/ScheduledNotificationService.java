package com.dw.artgallery.service;

import com.dw.artgallery.model.Reservation;
import com.dw.artgallery.repository.ReservationRepository;
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
//  @Scheduled(fixedDelay = 10000)
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

    @Scheduled(cron = "*/10 * * * * *") // 10초마다 실행
    @Transactional
    public void sendDummyReminderForTesting() {
        String testUserId = "steve12";
        String galleryTitle = "테스트 전시";

        notificationService.sendReservationReminder(testUserId, galleryTitle); // testUserId가 Principal.getName()과 동일해야 정상 전송됨
        log.info("[테스트 알림] userId={} → galleryTitle={}", testUserId, galleryTitle);
    }


}
