package com.dw.artgallery.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TicketAddDTO {
    private Long artistGalleryId;
    private int count;
    private LocalDate selectDate;
}
