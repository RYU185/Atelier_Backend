package com.dw.artgallery.DTO;

import com.dw.artgallery.model.Art;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ArtDTO {
    private Long id;
    private String title;
    private String imgUrl;
    private String description;
    private LocalDate completionDate;
    private LocalDate uploadDate;
    private String artistName;  // Artist의 이름만 포함
    private Boolean isDeleted;

    public ArtDTO(Long id, String title, String imgUrl, String description, LocalDate completionDate, LocalDate uploadDate, Boolean isDeleted) {
        this.id = id;
        this.title = title;
        this.imgUrl = imgUrl;
        this.description = description;
        this.completionDate = completionDate;
        this.uploadDate = uploadDate;
        this.isDeleted = isDeleted;

    }

    public static ArtDTO fromEntity(Art art) {
        return new ArtDTO(
                art.getId(),
                art.getTitle(),
                art.getImgUrl(),
                art.getDescription(),
                art.getCompletionDate(),
                art.getUploadDate(),
                art.getArtist() != null ? art.getArtist().getName() : null,
                art.getDeleted()
        );
    }


}