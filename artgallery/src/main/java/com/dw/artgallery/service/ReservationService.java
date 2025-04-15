package com.dw.artgallery.service;

import com.dw.artgallery.DTO.*;
import com.dw.artgallery.enums.ReservationStatus;
import com.dw.artgallery.enums.SortOrder;
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
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReserveTimeRepository reserveTimeRepository;
    private final ReserveDateRepository reserveDateRepository;
    private final UserRepository userRepository;
    private final ArtistGalleryRepository artistGalleryRepository;

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


    // 특정시간 실시간 잔여 수량 확인
    public ReserveAvailabilityDTO getAvailability(Long reserveTimeId) {
        return reserveTimeRepository.findAvailability(reserveTimeId);
    }


    // 하루전체 선택가능한 시간 목록조회
    public List<ReserveTimeDTO> getAvailableTimesByDate(LocalDate date) {
        List<ReserveTime> times = reserveTimeRepository.findByReserveDate_Date(date);
        return times.stream()
                .map(ReserveTimeDTO::fromEntity)
                .toList();
    }

    @Transactional
    public void updateReserveDate(Long reserveDateId, ReserveDateUpdateDTO dto){
        ReserveDate reserveDate = reserveDateRepository.findById(reserveDateId)
                .orElseThrow(()-> new ResourceNotFoundException("예약 날짜를 찾을 수 없습니다."));

        int reserved = reserveDate.getCapacity() - reserveDate.getRemaining();
        if (dto.getNewCapacity() < reserved ){
            throw new InvalidRequestException("현재 예약된 인원("+reserved+"명)보다 적은 인원으로 설정할 수 없습니다.");

        }

        reserveDate.setCapacity(dto.getNewCapacity());
        reserveDate.setRemaining(dto.getNewCapacity() - reserved);

        ArtistGallery artistGallery = reserveDate.getArtistGallery();
        artistGallery.setDeadline(dto.getNewDeadline());
    }

    @Transactional
    public void deleteReserveDate(Long id) {
        ReserveDate reserveDate = reserveDateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("예약 날짜를 찾을 수 없습니다."));

        reserveDateRepository.delete(reserveDate);
    }

    // 날짜별 통계
    // TreeMap, Collectors -> groupingBy 사용
    public List<ReservationStatDTO> getStatByDate(){
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream()
                .collect(Collectors.groupingBy(
                        r-> r.getReserveTime().getReserveDate().getDate(),
                        TreeMap::new,
                        Collectors.summingInt(Reservation::getHeadcount)
                ))
                .entrySet().stream()
                .map(e-> new ReservationStatDTO(e.getKey().toString(),e.getValue()))
                .toList();
    }

    // 전시회별 예약자 명단 조회
    @Transactional
    public List<ReservationUserSummaryDTO> getReservationsByGallery(Long galleryId) {
        List<Reservation> reservations = reservationRepository.findAllByGalleryId(galleryId);
        return reservations.stream()
                .map(ReservationUserSummaryDTO::fromEntity)
                .toList();
    }

    // 전시회별 현황(예약포함) 조회
    public List<ExhibitionReservationSummaryDTO> getAllGalleryReservationSummaries() {
        List<ArtistGallery> galleries = artistGalleryRepository.findAll();

        return galleries.stream()
                .map(ExhibitionReservationSummaryDTO::fromEntity)
                .toList();
    }

    // 전시회별 제목 검색 + 누적 예약자수 오름차순/내림차순 정렬
    public List<ExhibitionReservationSummaryDTO> searchAndSortGallerySummary(String title, SortOrder sort) {
        List<ArtistGallery> galleries;

        if (title != null && !title.isBlank()) {
            galleries = artistGalleryRepository.findByTitleContainingIgnoreCase(title);
        } else {
            galleries = artistGalleryRepository.findAll();
        }

        List<ExhibitionReservationSummaryDTO> dtoList = galleries.stream()
                .map(ExhibitionReservationSummaryDTO::fromEntity)
                .toList();

        if (sort == SortOrder.ASC) {
            dtoList.sort(Comparator.comparingInt(ExhibitionReservationSummaryDTO::getTotalReserved));
            // Comparator : 정렬 기준을 숫자로 줄 수 있는 자바 인터페이스
            // Comparator.compare~ : 각 DTO 에서 getTotalReserved 값을 꺼내 정렬해라
        } else if (sort == SortOrder.DESC) {
            dtoList.sort(Comparator.comparingInt(ExhibitionReservationSummaryDTO::getTotalReserved).reversed()); // reversed : 원래 오름차순이면 내림차순으로 반전
        }

        return dtoList;
    }







}
