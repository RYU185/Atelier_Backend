package com.dw.artgallery.DTO;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtCreateDTO {
    private String title;
    private String description;
    private LocalDate completionDate;
    private LocalDate uploadDate;
    private Long artistId;
    private MultipartFile image;
    private String imgUrl; // 서버에서 설정
}
