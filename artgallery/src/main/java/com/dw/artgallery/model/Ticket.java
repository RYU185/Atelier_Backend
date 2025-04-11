package com.dw.artgallery.model;

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
    private String count;

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

}
