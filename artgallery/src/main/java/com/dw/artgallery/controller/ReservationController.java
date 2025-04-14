package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.ReservationRequestDTO;
import com.dw.artgallery.DTO.ReservationResponseDTO;
import com.dw.artgallery.DTO.ReservationSummaryDTO;
import com.dw.artgallery.DTO.ReserveChangeRequestDTO;
import com.dw.artgallery.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

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
}
