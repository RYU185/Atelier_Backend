package com.dw.artgallery.DTO;

import com.dw.artgallery.enums.ReservationStatus;
import com.dw.artgallery.model.Reservation;
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
public class ReservationResponseDTO {
    // 예약 성공/실패화면

    private Long reservationId;

    private String galleryTitle;

    private LocalDate date;

    private ReservationStatus status;

    private LocalDateTime createdAt;

    public static ReservationResponseDTO fromEntity(Reservation reservation) {
        return new ReservationResponseDTO(
                reservation.getId(),
                reservation.getReserveDate().getArtistGallery().getTitle(),
                reservation.getReserveDate().getDate(),
                reservation.getReservationStatus(),
                reservation.getCreatedAt()
        );
    }
}