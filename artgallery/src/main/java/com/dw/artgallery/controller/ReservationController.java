package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.*;
import com.dw.artgallery.enums.SortOrder;
import com.dw.artgallery.model.ReserveDate;
import com.dw.artgallery.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 예약 생성
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReservationResponseDTO> reserve(
            @RequestBody ReservationRequestDTO reservationRequestDTO,
            Authentication authentication
            ){
        String userId = authentication.getName();

        return new ResponseEntity<>(
                reservationService.reserve(reservationRequestDTO, userId),
                HttpStatus.OK);
    }

    // 예약 변경
    @PutMapping("/{reservationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReservationResponseDTO> changeReservation(
            @PathVariable Long reservationId,
            @RequestBody ReserveChangeRequestDTO reserveChangeRequestDTO,
            Authentication authentication
        ){
            String userId = authentication.getName();
            return new ResponseEntity<>(
                    reservationService.changeReservation(reservationId, reserveChangeRequestDTO, userId),
                    HttpStatus.OK
            );

    }

    // 예약 취소
    @DeleteMapping("/{reservationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReservationResponseDTO> cancelReservation(
            @PathVariable Long reservationId,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        return new ResponseEntity<>(
                reservationService.cancelReservation(reservationId, userId),
                HttpStatus.OK
        );
    }

    @GetMapping("/reserve-date")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReserveDateDTO>> getReserveDatesByGallery(
            @RequestParam Long galleryId
    ) {
        return new ResponseEntity<>(
                reservationService.getReserveDatesByGalleryId(galleryId),
                HttpStatus.OK
        );
    }

    // 마이페이지 예약 목록 확인
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReservationSummaryDTO>> getMyReservations(
            Authentication authentication
    ){
        String userId = authentication.getName();
        return new ResponseEntity<>(
                reservationService.getMyReservations(userId),
                HttpStatus.OK
        );
    }

    // 예약 가능여부 확인
    @GetMapping("/availability/{reserveTimeId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReserveAvailabilityDTO> getAvailability(@PathVariable Long reserveTimeId) {
        return new ResponseEntity<>(reservationService.getAvailability(reserveTimeId), HttpStatus.OK);
    }

    // 날짜 선택시 해당 예약 가능 시간 조회
    @GetMapping("/available-times")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ReserveTimeDTO>> getAvailableTimes(
            @RequestParam Long galleryId,
            @RequestParam LocalDate date
    ) {
        return new ResponseEntity<>(
                reservationService.getAvailableTimesByGalleryAndDate(galleryId, date),
                HttpStatus.OK
        );
    }
    @PutMapping("/admin/reserve-date/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateReserveDate(
            @PathVariable Long id,
            @RequestBody ReserveDateUpdateDTO dto
    ) {
        reservationService.updateReserveDate(id, dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/admin/reserve-date/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReserveDate(@PathVariable Long id) {
        reservationService.deleteReserveDate(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 전시회별 예약자 명단 조회
    @GetMapping("/admin/users/by-gallery/{galleryId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationUserSummaryDTO>> getReservationsByGallery(@PathVariable Long galleryId) {
        return new ResponseEntity<>(reservationService.getReservationsByGallery(galleryId), HttpStatus.OK);
    }

    //
    @GetMapping("/admin/summary/gallery/all")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ExhibitionReservationSummaryDTO>> getGalleryReservationSummary() {
        return new ResponseEntity<>(reservationService.getAllGalleryReservationSummaries(), HttpStatus.OK);
    }

    @GetMapping("/admin/summary/gallery/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ExhibitionReservationSummaryDTO>> searchAndSortGallerySummary(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) SortOrder sort // required = false : 파라미터를 필수로 넘기지 않아도 된다.
    ) {
        return new ResponseEntity<>(
                reservationService.searchAndSortGallerySummary(title, sort),
                HttpStatus.OK
        );
    }

    @GetMapping("/admin/statistics/count/by-date")
    public ResponseEntity<List<ReservationStatDTO>> getStatByDate() {
        return new ResponseEntity<>(reservationService.getStatByDate(), HttpStatus.OK);
    }

    @GetMapping("/admin/statistics/trend/by-date")
    public ResponseEntity<List<ReservationTrendDTO>> getReservationTrendByDate() {
        return new ResponseEntity<>(
                reservationService.getReservationTrendByDate(),
                HttpStatus.OK
        );
    }

    @GetMapping("/admin/statistics/count/by-month")
    public ResponseEntity<List<ReservationStatDTO>> getMonthlyReservationStats() {
        return ResponseEntity.ok(reservationService.getReservationStatsByMonth());
    }

    @GetMapping("/admin/statistics/trend/by-month")
    public ResponseEntity<List<ReservationTrendDTO>> getTrendByMonth() {
        return new ResponseEntity<>(
                reservationService.getReservationTrendByMonth(),
                HttpStatus.OK
        );
    }

    @GetMapping("/admin/statistics/count/by-weekday")
    public ResponseEntity<List<ReservationStatDTO>> getStatByWeekday() {
        return new ResponseEntity<>(
                reservationService.getReservationStatsByWeekday(),
                HttpStatus.OK
        );
    }
    @GetMapping("/admin/statistics/count/by-week")
    public ResponseEntity<List<ReservationStatDTO>> getStatByWeek() {

        return ResponseEntity.ok(reservationService.getReservationStatsByWeek());
    }
    @GetMapping("/admin/statistics/trend/by-week")

    public ResponseEntity<List<ReservationTrendDTO>> getTrendByWeek() {
        return new ResponseEntity<>(
                reservationService.getReservationTrendByWeek(),
                HttpStatus.OK
        );
    }

    


}
