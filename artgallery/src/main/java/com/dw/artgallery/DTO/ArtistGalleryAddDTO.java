package com.dw.artgallery.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArtistGalleryAddDTO {
    private String title;
    private String poster;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate deadline;
    private double price;
    private List<Long> artistIdList;
    private List<Long> artIdList;

}
