package com.dw.artgallery.DTO;

import com.dw.artgallery.model.Artist;
import com.dw.artgallery.model.ArtistGallery;
import com.dw.artgallery.model.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationTicketDTO {

    private String galleryTitle;
    private LocalDate reserveDate;
    private String reserveTime;
    private List<String> artistNameList;
    private int headcount;

    public static ReservationTicketDTO from(Reservation reservation){
        ArtistGallery artistGallery = reservation.getReserveDate().getArtistGallery();
        List<String> artistNameList = artistGallery.getArtistList()
                .stream().map(Artist::getName).toList();

        return new ReservationTicketDTO(
                artistGallery.getTitle(),
                reservation.getReserveDate().getDate(),
                reservation.getReserveTime().getTime().toString(),
                artistNameList,
                reservation.getHeadcount()
        );
    }
}
