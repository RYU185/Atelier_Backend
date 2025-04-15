package com.dw.artgallery.service;

import com.dw.artgallery.DTO.*;
import com.dw.artgallery.enums.ReservationStatus;
import com.dw.artgallery.exception.InvalidRequestException;
import com.dw.artgallery.exception.PermissionDeniedException;
import com.dw.artgallery.exception.ResourceNotFoundException;
import com.dw.artgallery.model.*;
import com.dw.artgallery.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

        if (!reserveDate.getDate().isAfter(LocalDate.now())) {
            throw new InvalidRequestException("관람일 하루 전까지 예약 가능합니다.");
        }

        if (reservationRepository.existsByUserAndReserveTime(user, time)) {
            throw new InvalidRequestException("이미 해당 시간에 예약이 완료되었습니다.");
        }

        if (reservationRepository.existsDuplicateReservation(user, reserveDate.getDate(), ReservationStatus.RESERVED)) {
            throw new InvalidRequestException("해당 날짜에 이미 예약이 완료되어 있습니다.");
        }

        int headCount = reservationRequestDTO.getHeadcount();

        try{
            reserveDate.reserve(headCount);
            reserveDateRepository.save(reserveDate);

            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setReserveTime(time);
            reservation.setReservationStatus(ReservationStatus.RESERVED);
            reservation.setCreatedAt(LocalDateTime.now());

            return ReservationResponseDTO.fromEntity(reservationRepository.save(reservation));
        }catch (ObjectOptimisticLockingFailureException e){
            throw new InvalidRequestException("정원이 초과되었습니다. 다시 시도해주세요.");
        }
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

        if (reservationRepository.existsByUserAndReserveTime(user, newReserveTime)) {
            throw new InvalidRequestException("이미 해당 시간에 예약이 되어있습니다.");
        }

        if (reservationRepository.existsDuplicateReservationExceptSelf(
                user, newDate.getDate(), ReservationStatus.RESERVED, reservationId)) {
            throw new InvalidRequestException("해당 날짜에 이미 예약이 완료되어 있습니다.");
        }

        try {
            ReserveDate oldDate = reservation.getReserveTime().getReserveDate();
            int headCount = reservation.getHeadcount();

            oldDate.cancel(headCount);
            newDate.reserve(headCount);

            reserveDateRepository.saveAll(List.of(oldDate, newDate));

            reservation.setReserveTime(newReserveTime);

            return ReservationResponseDTO.fromEntity(reservation);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new InvalidRequestException("정원이 초과되었습니다. 다시 시도해주세요.");
        }
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

        try {
            ReserveDate reserveDate = reservation.getReserveTime().getReserveDate();
            int headcount = reservation.getHeadcount();

            reserveDate.cancel(headcount);
            reserveDateRepository.save(reserveDate);

            return ReservationResponseDTO.fromEntity(reservation);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new InvalidRequestException("정원 정보 갱신에 실패했습니다. 다시 시도해주세요.");
        }
    }


    @Transactional
    public List<ReservationSummaryDTO> getMyReservations(String userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("유저를 찾을 수 없습니다."));

        List<Reservation> reservations = reservationRepository.findByUserOrderByCreatedAtDesc(user);

        return reservations.stream().map(ReservationSummaryDTO::fromEntity)
                .toList();
    }

    public ReserveAvailabilityDTO getAvailability(Long reserveTimeId) {
        return reserveTimeRepository.findAvailability(reserveTimeId);
    }


    public List<ReserveTimeDTO> getAvailableTimesByDate(LocalDate date) {
        List<ReserveTime> times = reserveTimeRepository.findByReserveDate_Date(date);
        return times.stream()
                .map(ReserveTimeDTO::fromEntity)
                .toList();
    }
}
