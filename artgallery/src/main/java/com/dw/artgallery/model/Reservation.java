package com.dw.artgallery.model;

import com.dw.artgallery.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "gallery_date_slot_id"})
})
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "reserve_time_id", nullable = false)
    private ReserveTime reserveTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus reservationStatus = ReservationStatus.RESERVED;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime canceledAt;

    @Column(name = "headcount", nullable = false)
    private int headcount;

    public void cancel() {
        this.reservationStatus = ReservationStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
    }

    public ReserveDate getReserveDate() {
        return reserveTime.getReserveDate();
    }

    // 캡슐화 - 모델 객체가 자신의 상태를 직접 바꾸도록 함
    public boolean isCancelable() {
        return LocalDate.now().isBefore(reserveTime.getReserveDate().getDate());
        // 예약 날짜 이전까지만 취소 가능함
        // 당일 취소 불가능
    }

}
