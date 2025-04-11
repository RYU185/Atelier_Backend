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
public class TicketTotalDTO {
    private String artistGalleryPoster;
    private LocalDate startDate;
    private LocalDate endDate;
    private String title;
    private List<String> artist;
    private int totalVisitors;
}
