package com.dw.artgallery.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class RealDrawingRequestDTO {
    private Long id;
    private String imageData;
    private Boolean isTemporary;
    private String title;
}
