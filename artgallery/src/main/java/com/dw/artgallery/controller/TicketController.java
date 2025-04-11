package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.TicketDTO;
import com.dw.artgallery.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {
    @Autowired
    TicketService ticketService;

    //  모든 티켓 조회
    @GetMapping
    public ResponseEntity<List<TicketDTO>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

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

    // 로그인한 회원의 티켓조회, 논리적 삭제, 
}

