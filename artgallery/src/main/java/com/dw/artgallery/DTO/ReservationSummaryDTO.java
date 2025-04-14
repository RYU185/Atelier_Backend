package com.dw.artgallery.DTO;

import com.dw.artgallery.enums.ReservationStatus;
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

public class ReservationSummaryDTO {
    // 마이페이지용
    private Long reservationId;
    private String galleryTitle;
    private LocalDate date;
    private LocalTime time;
    private ReservationStatus status;

    public static ReservationSummaryDTO fromEntity(Reservation reservation){
        return new ReservationSummaryDTO(
                reservation.getId(),
                reservation.getReserveTime().getReserveDate().getArtistGallery().getTitle(),
                reservation.getReserveTime().getReserveDate().getDate(),
                reservation.getReserveTime().getTime(),
                reservation.getReservationStatus()
        );
    }
}
