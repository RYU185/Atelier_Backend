package com.dw.artgallery.DTO;

import com.dw.artgallery.model.Artist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArtistDTO {

    private Long id;
    private String name;
    private String profile_img;
    private String description;
    private boolean isDeleted;
    private List<BiographyDTO> biographyList;


    public static ArtistDTO fromEntity(Artist artist) {
        return new ArtistDTO(
                artist.getId(),
                artist.getName(),
                artist.getProfile_img(),
                artist.getDescription(),
                artist.isDeleted(),
                artist.getBiographyList().stream()
                .map(BiographyDTO::fromEntity)
                .toList()
        );
    }

    public Artist toEntity() {
        Artist artist = new Artist();
        artist.setId(this.id);
        artist.setName(this.name);
        artist.setProfile_img(this.profile_img);
        artist.setDescription(this.description);
        artist.setDeleted(this.isDeleted);
        return artist;
    }
}