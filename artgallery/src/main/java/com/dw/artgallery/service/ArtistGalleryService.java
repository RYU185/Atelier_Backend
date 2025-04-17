package com.dw.artgallery.service;

import com.dw.artgallery.DTO.ArtistGalleryAddDTO;
import com.dw.artgallery.DTO.ArtistGalleryDTO;
import com.dw.artgallery.DTO.ArtistGalleryDetailDTO;
import com.dw.artgallery.DTO.DeadlineDTO;
import com.dw.artgallery.model.*;
import com.dw.artgallery.repository.*;
import com.dw.artgallery.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ArtistGalleryService {

    private final ArtistGalleryRepository artistGalleryRepository;
    private final ArtistRepository artistRepository;
    private final ArtRepository artRepository;
    private final ReserveDateRepository reserveDateRepository;
    private final ReserveTimeRepository reserveTimeRepository;

    public List<ArtistGalleryDTO> getAllArtistGallery () {
        return artistGalleryRepository.findAll().stream().map(ArtistGallery::toDto).toList();

    }

    public ArtistGalleryDetailDTO getIdArtistGallery(Long id) {
        return artistGalleryRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("해당 ID를 가진 ArtistGallery 가 존재하지 않습니다.")).TODTO();
    }

    public List<ArtistGalleryDTO> getTitleArtistGallery(String title) {
        String keyword = "%" + title + "%";
        List<ArtistGallery> result = artistGalleryRepository.findByTitleLike(keyword);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("해당 제목의 전시회를 찾을 수 없습니다.");
        }
        return result.stream().map(ArtistGallery::toDto).collect(Collectors.toList());
    }

    public List<ArtistGalleryDTO> getNowArtistGallery() {
        LocalDate today = LocalDate.now();
         return artistGalleryRepository.findNowGallery(today).stream().map(ArtistGallery::toDto).toList();
    }

    public List<ArtistGalleryDTO> getPastArtistGallery() {
        LocalDate today = LocalDate.now();
        return artistGalleryRepository.findPastGallery(today).stream().map(ArtistGallery::toDto).toList();
    }

    public List<ArtistGalleryDTO> getExpectedArtistGallery() {
        LocalDate today = LocalDate.now();
        return artistGalleryRepository.findExpectedGallery(today).stream().map(ArtistGallery::toDto).toList();
    }


    @Transactional
    public ArtistGallery createGallery(ArtistGalleryAddDTO dto) {

        ArtistGallery gallery = ArtistGallery.fromAddDto(dto);

        List<Artist> artists = artistRepository.findAllById(dto.getArtistIdList());
        gallery.setArtistList(artists);

        List<Long> validArtistIds = artists.stream()
                .map(Artist::getId)
                .toList();

        List<Art> validArts = artRepository.findAllById(dto.getArtIdList()).stream()
                .filter(art -> art.getArtist() != null &&
                        validArtistIds.contains(art.getArtist().getId()))
                .toList();

        gallery.setArtList(validArts);

        gallery.setDeadline(gallery.getEndDate().minusDays(1));


        ArtistGallery savedGallery = artistGalleryRepository.save(gallery);

        LocalDate today = LocalDate.now().plusDays(1);
        LocalDate start = gallery.getStartDate().isAfter(today) ? gallery.getStartDate(): today;

        for (LocalDate date = start; !date.isAfter(gallery.getEndDate()); date = date.plusDays(1)) {
            ReserveDate reserveDate = new ReserveDate();
            reserveDate.setArtistGallery(savedGallery);
            reserveDate.setDate(date);
            reserveDate.setCapacity(100); // 기본 정원
            reserveDate.setRemaining(100);
            reserveDateRepository.save(reserveDate);

            // 기본 시간 설정 (10~17시)
            List<LocalTime> timeSlots = List.of(
                    LocalTime.of(10, 0), LocalTime.of(11, 0), LocalTime.of(12, 0),
                    LocalTime.of(13, 0), LocalTime.of(14, 0), LocalTime.of(15, 0),
                    LocalTime.of(16, 0), LocalTime.of(17, 0)
            );

            List<ReserveTime> reserveTimes = timeSlots.stream()
                    .map(time -> {
                        ReserveTime rt = new ReserveTime();
                        rt.setTime(time);
                        rt.setReserveDate(reserveDate);
                        return rt;
                    })
                    .toList();

            reserveTimeRepository.saveAll(reserveTimes);
        }

        return savedGallery;
    }


    public String updateDeadline(Long id, DeadlineDTO dto) {
        ArtistGallery gallery = artistGalleryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 갤러리를 찾을 수 없습니다."));

        gallery.setDeadline(dto.getDeadline());
        artistGalleryRepository.save(gallery);

        return "마감일이 " + dto.getDeadline() + "로 성공적으로 수정되었습니다.";
    }

}
