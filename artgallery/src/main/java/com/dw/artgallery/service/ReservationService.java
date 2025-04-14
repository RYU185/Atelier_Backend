package com.dw.artgallery.service;

import com.dw.artgallery.DTO.ReservationRequestDTO;
import com.dw.artgallery.DTO.ReservationResponseDTO;
import com.dw.artgallery.DTO.ReservationSummaryDTO;
import com.dw.artgallery.DTO.ReserveChangeRequestDTO;
import com.dw.artgallery.enums.ReservationStatus;
import com.dw.artgallery.exception.InvalidRequestException;
import com.dw.artgallery.exception.PermissionDeniedException;
import com.dw.artgallery.exception.ResourceNotFoundException;
import com.dw.artgallery.model.*;
import com.dw.artgallery.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReserveTimeRepository reserveTimeRepository;
    private final ReserveDateRepository reserveDateRepository;
    private final UserRepository userRepository;

    // 예약
    @Transactional
    public ReservationResponseDTO reserve(ReservationRequestDTO reservationRequestDTO, String userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("해당 유저를 찾을 수 없습니다"));

        ReserveTime time = reserveTimeRepository.findByIdWithFullJoin(reservationRequestDTO.getReserveTimeId())
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 예약 시간입니다."));

        ReserveDate reserveDate = time.getReserveDate();
        ArtistGallery gallery = reserveDate.getArtistGallery();

        if (reserveDate.getDate().isBefore(gallery.getStartDate()) || reserveDate.getDate().isAfter(gallery.getEndDate())) {
            throw new InvalidRequestException("전시 기간 외의 날짜는 예약할 수 없습니다.");
        }

        if (reserveDate.isFull()) {
            throw new InvalidRequestException("정원이 초과되었습니다.");
        }

        if (reservationRepository.existsByUserAndReserveTime(user, time)) {
            throw new InvalidRequestException("이미 해당 시간에 예약이 완료되었습니다.");
        }

        if (reservationRepository.existsDuplicateReservation(user, reserveDate.getDate(), ReservationStatus.RESERVED)) {
            throw new InvalidRequestException("해당 날짜에 이미 예약이 완료되어 있습니다.");
        }

        reserveDate.reserve();

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setReserveTime(time);
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

        ReserveTime newReserveTime = reserveTimeRepository.findByIdWithFullJoin(reserveChangeRequestDTO.getNewReserveTimeId())
                .orElseThrow(() -> new InvalidRequestException("선택한 시간은 예약할 수 없습니다."));

        ReserveDate newDate = newReserveTime.getReserveDate();
        ArtistGallery gallery = newDate.getArtistGallery();

        if (newDate.getDate().isBefore(gallery.getStartDate())
                || newDate.getDate().isAfter(gallery.getEndDate())) {
            throw new InvalidRequestException("전시 기간 외의 날짜는 예약할 수 없습니다.");
        }
        if (newDate.isFull()) {
            throw new InvalidRequestException("해당 날짜는 정원이 초과되었습니다.");
        }

        if (reservationRepository.existsByUserAndReserveTime(user, newReserveTime)) {
            throw new InvalidRequestException("이미 해당 시간에 예약이 되어있습니다.");
        }

        if (reservationRepository.existsDuplicateReservation(user, newDate.getDate(), ReservationStatus.RESERVED)) {
            throw new InvalidRequestException("해당 날짜에 이미 예약이 완료되어 있습니다.");
        }

        reservation.getReserveTime().getReserveDate().cancel();
        newDate.reserve();

        reservation.setReserveTime(newReserveTime);

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
        reservation.getReserveTime().getReserveDate().cancel();

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
