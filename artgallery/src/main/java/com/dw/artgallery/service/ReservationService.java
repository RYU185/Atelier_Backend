package com.dw.artgallery.service;

import com.dw.artgallery.DTO.ReservationRequestDTO;
import com.dw.artgallery.DTO.ReservationResponseDTO;
import com.dw.artgallery.enums.ReservationStatus;
import com.dw.artgallery.exception.InvalidRequestException;
import com.dw.artgallery.exception.PermissionDeniedException;
import com.dw.artgallery.exception.ResourceNotFoundException;
import com.dw.artgallery.model.ArtistGallery;
import com.dw.artgallery.model.Reservation;
import com.dw.artgallery.model.ReserveDate;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.ArtistGalleryRepository;
import com.dw.artgallery.repository.ReservationRepository;
import com.dw.artgallery.repository.ReserveDateRepository;
import com.dw.artgallery.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReserveDateRepository reserveDateRepository;
    private final ArtistGalleryRepository artistGalleryRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReservationResponseDTO reserve(ReservationRequestDTO reservationRequestDTO){
        User user = userRepository.findById(reservationRequestDTO.getUserId())
                .orElseThrow(()-> new ResourceNotFoundException("해당 유저를 찾을 수 없습니다"));

        ArtistGallery artistGallery = artistGalleryRepository.findById(reservationRequestDTO.getGalleryId())
                .orElseThrow(()-> new ResourceNotFoundException("전시를 찾을 수 없습니다."));

        ReserveDate reserveDate = reserveDateRepository
                .findByArtistGalleryAndDateWithLock(artistGallery.getId(), reservationRequestDTO.getDate())
                .orElseThrow(()-> new PermissionDeniedException("해당 날짜를 예약하실 수 없습니다."));

        if (!reserveDate.canReserve()){
            throw new InvalidRequestException("정원이 모두 찼습니다.");
        }
        if (reservationRepository.existsByUserAndReserveDate(user, reserveDate)){
            throw new InvalidRequestException("이미 해당 날짜에 예약이 완료되었습니다.");
        }
        reserveDate.reserve();

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setReserveDate(reserveDate);
        reservation.setReservationStatus(ReservationStatus.RESERVED);
        reservation.setCreatedAt(LocalDateTime.now());
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponseDTO.fromEntity(savedReservation);

    }

}
