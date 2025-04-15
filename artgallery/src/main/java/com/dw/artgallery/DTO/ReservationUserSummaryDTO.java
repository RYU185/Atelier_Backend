package com.dw.artgallery.DTO;

import com.dw.artgallery.model.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationUserSummaryDTO {
    private Long reservationId;
    private String username;
    private String email;
    private LocalDate reserveDate;
    private LocalTime reserveTime;
    private int headcount;
    private String status;

    public static ReservationUserSummaryDTO fromEntity(Reservation reservation) {
        return new ReservationUserSummaryDTO(
                reservation.getId(),
                reservation.getUser().getNickName(),
                reservation.getUser().getEmail(),
                reservation.getReserveTime().getReserveDate().getDate(),
                reservation.getReserveTime().getTime(),
                reservation.getHeadcount(),
                reservation.getReservationStatus().name()
        );
    }
}