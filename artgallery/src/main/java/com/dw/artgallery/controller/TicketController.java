package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.TicketAddDTO;
import com.dw.artgallery.DTO.TicketDTO;
import com.dw.artgallery.DTO.TicketTotalDTO;
import com.dw.artgallery.model.User;
import com.dw.artgallery.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {
    @Autowired
    TicketService ticketService;

    //  특정 ID의 티켓 조회
    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long id) {
        return new ResponseEntity<>(ticketService.getTicketById(id),HttpStatus.OK);
    }

    //  특정 날짜의 티켓 조회
    @GetMapping("/date/{date}")
    public ResponseEntity<List<TicketDTO>> getTicketsByPurchaseDate(@PathVariable LocalDate date) {
        return new ResponseEntity<>(ticketService.getTicketsByPurchaseDate(date), HttpStatus.OK);
    }

    //  로그인한 회원의 티켓 조회
    @GetMapping("/my")
    public ResponseEntity<List<TicketDTO>> getMyTickets(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ticketService.getTicketsByUserId(user.getUserId()));
    }

    // 논리적 삭제
    @PostMapping("/delete/{id}")
    public ResponseEntity<String> softDeleteTicket(@PathVariable Long id, @AuthenticationPrincipal User user) {
        ticketService.softDeleteTicket(id, user);
        return ResponseEntity.ok("티켓이 논리적으로 삭제되었습니다.");
    }

    // artistGalleryId로 티켓 추가
    @PostMapping("/add/{artistGalleryId}")
    public ResponseEntity<String> addTicket(
            @PathVariable Long artistGalleryId,
            @RequestBody TicketAddDTO dto,
            @AuthenticationPrincipal User user) {
        ticketService.addTicket(artistGalleryId, dto, user);
        return ResponseEntity.ok("티켓이 성공적으로 저장되었습니다.");
    }

    // 티켓별 총 누적 판매량
    @GetMapping("/total")
    public ResponseEntity<List<TicketTotalDTO>> getTicketTotals() {
        return new ResponseEntity<>(ticketService.getAllTicketTotals(),HttpStatus.OK);
    }

}

