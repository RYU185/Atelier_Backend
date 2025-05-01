package com.dw.artgallery.DTO;



import com.dw.artgallery.model.Art;
import com.dw.artgallery.model.Artist;
import com.dw.artgallery.model.ArtistGallery;
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

    public static ArtistGalleryDetailDTO fromEntity(ArtistGallery artistGallery) {
        ArtistGalleryDetailDTO dto = new ArtistGalleryDetailDTO();

        dto.setTitle(artistGallery.getTitle());
        dto.setDescription(artistGallery.getDescription());
        dto.setStartDate(artistGallery.getStartDate());
        dto.setEndDate(artistGallery.getEndDate());
        dto.setDeadline(artistGallery.getDeadline());
        dto.setPrice(artistGallery.getPrice());
        dto.setPosterUrl(artistGallery.getPosterUrl());

        if (artistGallery.getArtistList() != null) {
            dto.setArtistList(
                    artistGallery.getArtistList().stream()
                            .map(Artist::getName)
                            .toList()
            );
        }

        if (artistGallery.getArtList() != null) {
            dto.setArtPoster(
                    artistGallery.getArtList().stream()
                            .map(Art::getImgUrl)
                            .toList()
            );

            dto.setArtTitle(
                    artistGallery.getArtList().stream()
                            .map(Art::getTitle)
                            .toList()
            );

            dto.setArtistName(
                    artistGallery.getArtList().stream()
                            .map(art -> art.getArtist() != null ? art.getArtist().getName() : "알 수 없음")
                            .toList()
            );

            dto.setCompletionDate(
                    artistGallery.getArtList().stream()
                            .map(Art::getCompletionDate)
                            .toList()
            );
        }
        return dto;
    }
}
