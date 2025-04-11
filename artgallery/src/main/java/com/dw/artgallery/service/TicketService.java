package com.dw.artgallery.service;

import com.dw.artgallery.DTO.TicketDTO;
import com.dw.artgallery.model.Ticket;
import com.dw.artgallery.repository.TicketRepository;
import com.dw.artgallery.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {
    @Autowired
    TicketRepository ticketRepository;


    //  모든 티켓 조회
    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //  ID로 특정 티켓 조회
    public TicketDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID의 티켓을 찾을 수 없습니다: " + id));
        return convertToDTO(ticket);
    }

    //  구매 날짜별 티켓 조회
    public List<TicketDTO> getTicketsByPurchaseDate(LocalDate date) {
        return ticketRepository.findByPurchaseDate(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TicketDTO> getTicketsByUserId(String userId) {
        List<Ticket> tickets = ticketRepository.findAllByUser_UserId(userId);
        return tickets.stream()
                .map(this::convertToDTO)
                .toList();
    }

    //  `Ticket` 엔터티를 `TicketDTO`로 변환
    private TicketDTO convertToDTO(Ticket ticket) {
        return new TicketDTO(
                ticket.getId(),
                Integer.parseInt(ticket.getCount()), // String → int 변환
                ticket.getSelectDate(),
                ticket.getArtistGallery() != null ? ticket.getArtistGallery().getTitle() : "Unknown",
                ticket.getPurchaseDate()
        );
    }
}
