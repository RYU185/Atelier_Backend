package com.dw.artgallery.DTO;

import com.dw.artgallery.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ReservationSummaryDTO {
    // 마이페이지용
    private Long reservationId;
    private String galleryTitle;
    private LocalDate date;
    private ReservationStatus status;

}
