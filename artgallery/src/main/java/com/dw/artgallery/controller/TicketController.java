package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.TicketDTO;
import com.dw.artgallery.model.User;
import com.dw.artgallery.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
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
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    //  특정 날짜의 티켓 조회
    @GetMapping("/date/{date}")
    public ResponseEntity<List<TicketDTO>> getTicketsByPurchaseDate(@PathVariable LocalDate date) {
        return ResponseEntity.ok(ticketService.getTicketsByPurchaseDate(date));
    }

    // 로그인한 회원의 티켓 조회
    @GetMapping("/my")
    public ResponseEntity<List<TicketDTO>> getMyTickets(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ticketService.getTicketsByUserId(user.getUserId()));
    }

    // 티켓 논리적 삭제
    @PostMapping("/delete/{id}")
    public ResponseEntity<String> softDeleteTicket(@PathVariable Long id) {
        ticketService.softDeleteTicket(id);
        return ResponseEntity.ok("티켓이 논리적으로 삭제되었습니다.");
    }






}

