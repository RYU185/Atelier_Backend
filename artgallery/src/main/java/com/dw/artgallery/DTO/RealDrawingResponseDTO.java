package com.dw.artgallery.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class RealDrawingResponseDTO {
    private Long id;
    private String imageData;
    private Boolean isTemporary;
    private String title;
    private LocalDateTime updatedAt;
    private String userId;
}
