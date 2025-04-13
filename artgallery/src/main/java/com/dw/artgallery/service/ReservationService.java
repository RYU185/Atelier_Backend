package com.dw.artgallery.service;

import com.dw.artgallery.DTO.ReservationRequestDTO;
import com.dw.artgallery.DTO.ReservationResponseDTO;
import com.dw.artgallery.DTO.ReservationSummaryDTO;
import com.dw.artgallery.DTO.ReserveChangeRequestDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReserveDateRepository reserveDateRepository;
    private final ArtistGalleryRepository artistGalleryRepository;
    private final UserRepository userRepository;

    // 예약
    @Transactional
    public ReservationResponseDTO reserve(ReservationRequestDTO reservationRequestDTO, String userId){
        User user = userRepository.findById(userId)
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

    // 예약 변경
    @Transactional
    public ReservationResponseDTO changeReservation(Long reservationId,
                                                    ReserveChangeRequestDTO reserveChangeRequestDTO,
                                                    String userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("유저를 찾을 수 없습니다"));
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(()-> new ResourceNotFoundException("해당 예약을 찾을 수 없습니다"));

        if (!reservation.getUser().getUserId().equals(userId)){
            throw new PermissionDeniedException("본인의 예약만 변경할 수 있습니다.");
        }

        if (!reservation.isCancelable()){
            throw new InvalidRequestException("예약일이 지나 변경이 불가능합니다.");
        }

        ReserveDate newDate = reserveDateRepository
                .findByArtistGalleryAndDateWithLock(
                        reservation.getReserveDate().getArtistGallery().getId(),
                        reserveChangeRequestDTO.getNewDate()
                ).orElseThrow(()-> new ResourceNotFoundException("선택한 날짜에 예약할 수 없습니다."));

        if (!newDate.canReserve()){
            throw new InvalidRequestException("해당일은 정원초과로 예약할 수 없습니다.");
        }

        reservation.getReserveDate().cancel();
        newDate.reserve();
        reservation.setReserveDate(newDate);

        return ReservationResponseDTO.fromEntity(reservation);
    }

    // 예약 취소
    @Transactional
    public ReservationResponseDTO cancelReservation(Long reservationId, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("유저를 찾을 수 없습니다."));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("예약 정보를 찾을 수 없습니다."));

        if (!reservation.getUser().getUserId().equals(userId)) {
            throw new PermissionDeniedException("본인의 예약만 취소할 수 있습니다.");
        }

        if (!reservation.isCancelable()) {
            throw new InvalidRequestException("예약일이 지나 취소할 수 없습니다.");
        }

        reservation.cancel();
        reservation.getReserveDate().cancel();

        return ReservationResponseDTO.fromEntity(reservation);
    }

    @Transactional
    public List<ReservationSummaryDTO> getMyReservations(String userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("유저를 찾을 수 없습니다."));

        List<Reservation> reservations = reservationRepository.findByUserOrderByCreatedAtDesc(user);

        return reservations.stream().map(ReservationSummaryDTO::fromEntity)
                .toList();
    }
}
