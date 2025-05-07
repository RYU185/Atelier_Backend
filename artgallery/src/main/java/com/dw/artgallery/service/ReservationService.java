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
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReserveTimeRepository reserveTimeRepository;
    private final ReserveDateRepository reserveDateRepository;
    private final UserRepository userRepository;
    private final ArtistGalleryRepository artistGalleryRepository;
    private final NotificationService notificationService;
    private final ScheduledNotificationService scheduledNotificationService;


    // 예약
    @Transactional
    public ReservationResponseDTO reserve(ReservationRequestDTO reservationRequestDTO, String userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("해당 유저를 찾을 수 없습니다"));

        ReserveTime time = reserveTimeRepository.findByIdWithFullJoin(reservationRequestDTO.getReserveTimeId())
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 예약 시간입니다."));

        ReserveDate reserveDate = time.getReserveDate();
        ArtistGallery gallery = reserveDate.getArtistGallery();
        LocalDate date = reserveDate.getDate();

        if (date.isBefore(gallery.getStartDate()) || date.isAfter(gallery.getEndDate())) {
            throw new InvalidRequestException("전시 기간 외의 날짜는 예약할 수 없습니다.");
        }

        log.info("예약 검증용 로그 - today: {}, 관람일: {}", LocalDate.now(), date);
        if (!LocalDate.now().isBefore(date)) {
            throw new InvalidRequestException("관람일 하루 전까지 예약 가능합니다.");
        }

        if (reservationRepository.existsReservedByUserAndTime(user, time)) {
            throw new InvalidRequestException("이미 해당 시간에 예약이 완료되었습니다.");
        }

        if (reservationRepository.existsDuplicateReservation(user, date, ReservationStatus.RESERVED)) {
            throw new InvalidRequestException("해당 날짜에 이미 예약이 완료되어 있습니다.");
        }

        int headCount = reservationRequestDTO.getHeadcount();

        try {
            reserveDate.reserve(headCount);
            reserveDateRepository.save(reserveDate);

            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setReserveTime(time);
            reservation.setReservationStatus(ReservationStatus.RESERVED);
            reservation.setCreatedAt(LocalDateTime.now());
            reservation.setHeadcount(headCount);

            Reservation saved = reservationRepository.save(reservation);
            scheduledNotificationService.sendImmediateReminder(saved);
            return ReservationResponseDTO.fromEntity(saved);

        } catch (ObjectOptimisticLockingFailureException e) {
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

        if (reservationRepository.existsReservedByUserAndTime(user, newReserveTime)) {
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

            reservation.setReservationStatus(ReservationStatus.CANCELED);
            reservationRepository.save(reservation);

            return ReservationResponseDTO.fromEntity(reservation);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new InvalidRequestException("정원 정보 갱신에 실패했습니다. 다시 시도해주세요.");
        }

    }

    public List<ReserveDateDTO> getReserveDatesByGalleryId(Long galleryId) {
        List<ReserveDate> reserveDates = reserveDateRepository.findByArtistGallery_Id(galleryId);
        return reserveDates.stream()
                .map(ReserveDateDTO::fromEntity)
                .collect(Collectors.toList());
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

    public List<ReserveTimeDTO> getAvailableTimesByGalleryAndDate(Long galleryId, LocalDate date) {
        List<ReserveTime> times = reserveTimeRepository
                .findByReserveDate_ArtistGallery_IdAndReserveDate_Date(galleryId, date);
        return times.stream()
                .map(ReserveTimeDTO::fromEntity)
                .toList();
    }

    @Transactional
    public void updateReserveDate(Long reserveDateId, ReserveDateUpdateDTO dto){
        ReserveDate reserveDate = reserveDateRepository.findById(reserveDateId)
                .orElseThrow(()-> new ResourceNotFoundException("예약 날짜를 찾을 수 없습니다."));

        int reserved = reserveDate.getReservedCount();
        if (dto.getNewCapacity() < reserved ){
            throw new InvalidRequestException("현재 예약된 인원("+reserved+"명)보다 적은 인원으로 설정할 수 없습니다.");

        }

        reserveDate.setCapacity(dto.getNewCapacity());
        reserveDate.setRemaining(dto.getNewCapacity() - reserved);

        ArtistGallery artistGallery = reserveDate.getArtistGallery();
        artistGallery.setDeadline(dto.getNewDeadline());

        artistGalleryRepository.save(artistGallery);
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
        List<Reservation> reservations = reservationRepository.findAllReserved();

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

    // 날짜별 예약 증감 계산
    @Transactional(readOnly = true)
    public List<ReservationTrendDTO> getReservationTrendByDate() {
        List<Reservation> reservations = reservationRepository.findAllReserved();

        // 날짜별 headcount 합산 (오름차순 TreeMap)
        Map<LocalDate, Integer> dailyMap = reservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getReserveTime().getReserveDate().getDate(),
                        TreeMap::new,
                        Collectors.summingInt(Reservation::getHeadcount)
                ));

        List<ReservationTrendDTO> result = new ArrayList<>();
        int cumulative = 0; // 누적 예약자 수 초기화
        Integer prev = null; // 전날 예약 수

        // Entry<Key, Value> entryName : dailyMap.entrySet() : 날짜를 한 줄 씩 순서대로 처리
        for (Map.Entry<LocalDate, Integer> entry : dailyMap.entrySet()) {
            int today = entry.getValue(); // 오늘 예약자수에
            cumulative += today; // 누적을 더함
            int diff = (prev != null) ? today - prev : 0; // 전날과 비교해서 증감 수치를 계산

            result.add(new ReservationTrendDTO(
                    entry.getKey().toString(),  // 날짜를 label
                    cumulative,
                    diff
            ));

            prev = today; // diff 계산후 업데이트
        }

        return result;
    }


    @Transactional(readOnly = true)
    public List<ReservationStatDTO> getReservationStatsByMonth() {
        List<Reservation> reservations = reservationRepository.findAllReserved();

        if (reservations.isEmpty()) return Collections.emptyList();

        // ✅ 연도는 현재 연도 기준으로 가져옴
        LocalDate now = LocalDate.now();
        int targetYear = now.getYear();

        // 🔍 해당 연도의 예약만 필터링
        List<Reservation> filtered = reservations.stream()
                .filter(r -> {
                    LocalDate date = r.getReserveDate().getDate();
                    return date.getYear() == targetYear;
                })
                .toList();

        // 월별 합계 초기화 (1~12월)
        Map<Integer, Long> monthlyMap = new LinkedHashMap<>();
        for (int month = 1; month <= 12; month++) {
            monthlyMap.put(month, 0L); // 처음엔 0으로 초기화
        }

        // 실제 데이터 합산
        for (Reservation res : filtered) {
            LocalDate date = res.getReserveDate().getDate();
            int month = date.getMonthValue();
            monthlyMap.put(month, monthlyMap.get(month) + res.getHeadcount());
        }

        // 결과 DTO 변환
        List<ReservationStatDTO> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            String label = month + "월";
            long total = monthlyMap.get(month);
            result.add(new ReservationStatDTO(label, total));
        }

        return result;
    }


    public List<ReservationTrendDTO> getReservationTrendByMonth() {
        List<Reservation> reservations = reservationRepository.findAllReserved();

        Map<String, Integer> monthlyMap = reservations.stream()
                .collect(Collectors.groupingBy(
                        r -> {
                            LocalDate date = r.getReserveTime().getReserveDate().getDate();
                            return date.getYear() + "." + String.format("%02d", date.getMonthValue());
                        },
                        TreeMap::new,
                        Collectors.summingInt(Reservation::getHeadcount)
                ));

        List<ReservationTrendDTO> result = new ArrayList<>();
        int cumulative = 0;
        Integer prev = null;

        for (Map.Entry<String, Integer> entry : monthlyMap.entrySet()) {
            int thisMonth = entry.getValue();
            cumulative += thisMonth;
            int diff = (prev != null) ? thisMonth - prev : 0;

            result.add(new ReservationTrendDTO(
                    entry.getKey(),  // "2025.04"
                    cumulative,
                    diff
            ));

            prev = thisMonth;
        }

        return result;
    }

    public List<ReservationStatDTO> getReservationStatsByWeekday() {
        List<Reservation> reservations = reservationRepository.findAllReserved();

        Map<DayOfWeek, Integer> weekdayMap = reservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getReserveTime().getReserveDate().getDate().getDayOfWeek(),
                        () -> new EnumMap<>(DayOfWeek.class),
                        // EnumMap: 요일 기준 정렬
                        Collectors.summingInt(Reservation::getHeadcount)
                ));

        // 일요일부터 고정 정렬
        List<DayOfWeek> weekdayOrder = List.of(
                DayOfWeek.SUNDAY,
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY
        );

        return weekdayOrder.stream()
                .filter(weekdayMap::containsKey) // 실제 예약 존재하는 요일만
                .map(d -> new ReservationStatDTO(d.name(), weekdayMap.get(d)))
                .toList();
    }


    public List<ReservationStatDTO> getReservationStatsByWeek() {
        List<Reservation> reservations = reservationRepository.findAllReserved();

        if (reservations.isEmpty()) return Collections.emptyList();

        // ✅ 현재 달을 자동으로 가져오기
        LocalDate now = LocalDate.now();
        int targetMonth = now.getMonthValue();
        int targetYear = now.getYear();

        // 🔍 해당 월의 예약만 필터링
        List<Reservation> filtered = reservations.stream()
                .filter(r -> {
                    LocalDate date = r.getReserveDate().getDate();
                    return date.getYear() == targetYear && date.getMonthValue() == targetMonth;
                })
                .toList();

        System.out.println("▶ [" + targetYear + "년 " + targetMonth + "월] 예약 수: " + filtered.size());

        Map<String, Long> weekMap = new LinkedHashMap<>();

        // ✅ 기준 날짜를 해당 월의 1일로 세팅
        LocalDate base = LocalDate.of(targetYear, targetMonth, 1);
        for (int i = 0; i < 4; i++) {
            LocalDate weekStart = base.plusWeeks(i);
            LocalDate weekEnd = weekStart.plusDays(6);
            String label = targetMonth + "월/" + (i + 1) + "주차";

            long total = filtered.stream()
                    .filter(res -> {
                        LocalDate date = res.getReserveDate().getDate();
                        return !date.isBefore(weekStart) && !date.isAfter(weekEnd);
                    })
                    .mapToLong(Reservation::getHeadcount)
                    .sum();

            weekMap.put(label, total);
        }

        return weekMap.entrySet().stream()
                .map(e -> new ReservationStatDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<ReservationTrendDTO> getReservationTrendByWeek() {
        List<Reservation> reservations = reservationRepository.findAllReserved();

        if (reservations.isEmpty()) return Collections.emptyList();

        // 🔍 주차 단위로 집계 (label 형식을 프론트와 동일하게 맞춤)
        Map<String, Integer> weeklyMap = reservations.stream()
                .collect(Collectors.groupingBy(
                        r -> {
                            LocalDate date = r.getReserveDate().getDate();
                            int weekOfMonth = ((date.getDayOfMonth() - 1) / 7) + 1;
                            return date.getMonthValue() + "월/" + weekOfMonth + "주차";
                        },
                        TreeMap::new,
                        Collectors.summingInt(Reservation::getHeadcount)
                ));

        List<ReservationTrendDTO> result = new ArrayList<>();
        int cumulative = 0;
        Integer prev = null;

        for (Map.Entry<String, Integer> entry : weeklyMap.entrySet()) {
            int thisWeek = entry.getValue();
            cumulative += thisWeek;
            int diff = (prev != null) ? thisWeek - prev : 0;

            result.add(new ReservationTrendDTO(
                    entry.getKey(),      // ex) "4월/4주차"
                    cumulative,
                    diff
            ));

            prev = thisWeek;
        }

        return result;
    }

}
