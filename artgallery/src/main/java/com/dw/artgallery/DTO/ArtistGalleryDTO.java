package com.dw.artgallery.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArtistGalleryDTO {
    private Long id;
    private String title;
    private String posterUrl;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate deadline;
}
