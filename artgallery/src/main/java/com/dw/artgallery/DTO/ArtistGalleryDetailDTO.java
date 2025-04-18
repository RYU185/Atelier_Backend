package com.dw.artgallery.DTO;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArtistGalleryDetailDTO {

    private String title;
    private String posterUrl;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private double price;
    private LocalDate deadline;

    private List<String> artistList = new ArrayList<>();

    private List<String> artPoster = new ArrayList<>();
    private  List<String> artTitle = new ArrayList<>();
    private List<String> artistName = new ArrayList<>();
    private List<LocalDate> completionDate =new ArrayList<>();

}
