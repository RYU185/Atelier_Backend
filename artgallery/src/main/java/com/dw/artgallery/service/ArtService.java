package com.dw.artgallery.service;

import com.dw.artgallery.DTO.ArtCreateDTO;
import com.dw.artgallery.DTO.ArtDTO;
import com.dw.artgallery.DTO.ArtUpdateDTO;
import com.dw.artgallery.model.Art;
import com.dw.artgallery.model.Artist;
import com.dw.artgallery.repository.ArtRepository;
import com.dw.artgallery.repository.ArtistRepository;
import com.dw.artgallery.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ArtService {

    private final ArtRepository artRepository;
    private final ArtistRepository artistRepository;

    public List<ArtDTO> getAllArt() {
        return artRepository.findByDeletedFalse().stream()
                .map(ArtDTO::fromEntity)
                .collect(Collectors.toList());
    }


    // ID로 작품 조회
    public ArtDTO findByIdArtId(Long id) {
        Art art = artRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Art not found with id: " + id));
        return convertToDTO(art);
    }

    public List<ArtDTO> getArtByArtistId(Long artistId) {
        return artRepository.findActiveArtByArtistId(artistId).stream()
                .map(ArtDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ArtDTO> getArtsByArtistIds(List<Long> artistIds) {
        return artRepository.findByArtistIdIn(artistIds).stream()
                .map(ArtDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ArtDTO> getArtByUserId(String userId){

        Artist artist = artistRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 작가를 찾을 수 없습니다."));

        List<Art> arts = artRepository.findByArtistId(artist.getId());

        // 3. DTO 변환
        return arts.stream()
                .map(ArtDTO::fromEntity)
                .collect(Collectors.toList());
    };



    // 작품 수정
    public ArtDTO updateArt(Long id, ArtUpdateDTO artUpdateDTO) {
        Art art = artRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Art not found with id: " + id));

        art.setTitle(artUpdateDTO.getTitle());
        art.setImgUrl(artUpdateDTO.getImgUrl());
        art.setDescription(artUpdateDTO.getDescription());
        art.setCompletionDate(artUpdateDTO.getCompletionDate());
        art.setUploadDate(artUpdateDTO.getUploadDate());

        if (artUpdateDTO.getArtistId() != null) {
            Artist artist = artistRepository.findById(artUpdateDTO.getArtistId())
                    .orElseThrow(() -> new ResourceNotFoundException("Artist not found with id: " + artUpdateDTO.getArtistId()));
            art.setArtist(artist);
        }

        Art updatedArt = artRepository.save(art);
        return convertToDTO(updatedArt);
    }

    // 작품 삭제 (soft delete)
    @Transactional
    public void deleteArtById(Long id) {
        Art art = artRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 작품을 찾을 수 없습니다."));
        art.setDeleted(true);
    }

    // 작품 등록
    @Transactional
    public ArtDTO createArt(ArtCreateDTO dto) {
        Art art = new Art();
        art.setTitle(dto.getTitle());
        art.setImgUrl(dto.getImgUrl());
        art.setDescription(dto.getDescription());
        art.setDeleted(false);

        art.setCompletionDate(dto.getCompletionDate() != null ? dto.getCompletionDate() : LocalDate.now());
        art.setUploadDate(dto.getUploadDate() != null ? dto.getUploadDate() : LocalDate.now());

        Artist artist = artistRepository.findById(dto.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 작가를 찾을 수 없습니다."));
        art.setArtist(artist);

        return convertToDTO(artRepository.save(art));
    }

    // Entity → DTO
    private ArtDTO convertToDTO(Art art) {
        return new ArtDTO(
                art.getId(),
                art.getTitle(),
                art.getImgUrl(),
                art.getDescription(),
                art.getCompletionDate(),
                art.getUploadDate(),
                art.getArtist() != null ? art.getArtist().getName() : "Unknown",
                art.getDeleted()
        );
    }
}
