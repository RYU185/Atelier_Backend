package com.dw.artgallery.model;


import com.dw.artgallery.DTO.TicketDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name="ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="count")
    private int count;

    @Column(name = "select_date")
    private LocalDate selectDate;

    @Column(name = "is_delete")
    private Boolean isDeleted;


    @ManyToOne
    @JoinColumn(name = "artist_gallery_id")
    private ArtistGallery artistGallery;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Column(name="purchase_date",nullable = false)
    private LocalDate purchaseDate;




    public TicketDTO toDto() {
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setArtistGallery(this.artistGallery.getTitle());
        ticketDTO.setCount(this.count);
        ticketDTO.setPurchaseDate(this.purchaseDate);
        ticketDTO.setSelectDate(this.selectDate);
        ticketDTO.setTotal(this.count*this.artistGallery.getPrice());
        ticketDTO.setArtistGalleryPoster(this.artistGallery.getPosterUrl());
        return ticketDTO;

    }


}
