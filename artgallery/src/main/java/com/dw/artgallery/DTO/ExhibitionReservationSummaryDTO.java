package com.dw.artgallery.DTO;

import com.dw.artgallery.model.ArtistGallery;
import com.dw.artgallery.model.ReserveDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExhibitionReservationSummaryDTO {
    private Long galleryId;
    private String posterUrl;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalReserved;
    private int todayRemaining;
    private int todayReserved;
    private LocalDate deadline;
    private int capacity;

    public static ExhibitionReservationSummaryDTO fromEntity(ArtistGallery artistGallery){
        LocalDate today = LocalDate.now();

        // 오늘날짜 예약일정
        Optional<ReserveDate> todayDate = artistGallery.getReserveDates().stream()
                .filter(r -> r.getDate().equals(today))
                .findFirst();

        int todayCapacity = todayDate.map(ReserveDate::getCapacity).orElse(0);
        int todayReserved = todayDate.map(ReserveDate::getReservedCount).orElse(0);
        int todayRemaining = todayCapacity - todayReserved;

        int totalReserved = artistGallery.getReserveDates().stream()
                .mapToInt(ReserveDate::getReservedCount)
                .sum();

        return new ExhibitionReservationSummaryDTO(
                artistGallery.getId(),
                artistGallery.getPosterUrl(),
                artistGallery.getTitle(),
                artistGallery.getStartDate(),
                artistGallery.getEndDate(),
                totalReserved,
                todayRemaining,
                todayReserved,
                artistGallery.getDeadline(),
                artistGallery.getReserveDates().stream()
                        .mapToInt(ReserveDate::getCapacity)
                        .sum()
        );
    }
}
