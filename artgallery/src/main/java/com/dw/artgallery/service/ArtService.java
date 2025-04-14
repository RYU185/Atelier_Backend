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

@RequiredArgsConstructor
@Service
public class ArtService {

    private final ArtRepository artRepository;
    private final ArtistRepository artistRepository;

    // 전체 작품 조회
    public List<ArtDTO> getAllArt() {
        return artRepository.findByDeletedFalse()
                .stream()
                .map(this::convertToDTO)
                .toList(); // 👈 deleted = false 인 항목만 조회
    }


    // ID로 작품 조회 후 DTO 변환
    public ArtDTO findByIdArtId(Long id) {
        Art art = artRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Art not found with id: " + id));

        return convertToDTO(art);
    }

    // 작품 수정 (관리자)
    public ArtDTO updateArt(Long id, ArtUpdateDTO artUpdateDTO) {
        Art art = artRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Art not found with id: " + id));

        // 변경할 값 적용
        art.setTitle(artUpdateDTO.getTitle());
        art.setImgUrl(artUpdateDTO.getImgUrl());
        art.setDescription(artUpdateDTO.getDescription());
        art.setCompletionDate(artUpdateDTO.getCompletionDate());
        art.setUploadDate(artUpdateDTO.getUploadDate());

        // 작가 변경이 있을 경우
        if (artUpdateDTO.getArtistId() != null) {
            Artist artist = artistRepository.findById(artUpdateDTO.getArtistId())
                    .orElseThrow(() -> new ResourceNotFoundException("Artist not found with id: " + artUpdateDTO.getArtistId()));
            art.setArtist(artist);
        }

        // 수정된 데이터 저장
        Art updatedArt = artRepository.save(art);
        return convertToDTO(updatedArt);
    }

    // 작품 삭제 (관리자)
    @Transactional
    public void deleteArtById(Long id) {
        Art art = artRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 작품을 찾을 수 없습니다."));
        art.setDeleted(true);
    }

    // 작품 추가 (관리자)
    @Transactional
    public ArtDTO createArt(ArtCreateDTO artCreateDTO) {
        Art art = new Art();
        art.setTitle(artCreateDTO.getTitle());
        art.setImgUrl(artCreateDTO.getImgUrl());
        art.setDescription(artCreateDTO.getDescription());
        art.setDeleted(false);

        // ✅ NULL 방지: 기본값 설정
        art.setCompletionDate(artCreateDTO.getCompletionDate() != null ?
                artCreateDTO.getCompletionDate() : LocalDate.now());

        art.setUploadDate(artCreateDTO.getUploadDate() != null ?
                artCreateDTO.getUploadDate() : LocalDate.now());

        // 작가 설정
        Artist artist = artistRepository.findById(artCreateDTO.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 작가를 찾을 수 없습니다."));
        art.setArtist(artist);

        Art savedArt = artRepository.save(art);
        return convertToDTO(savedArt);
    }





    // 순환참조 방지 DTO
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
