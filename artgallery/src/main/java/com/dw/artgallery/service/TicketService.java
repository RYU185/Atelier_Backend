package com.dw.artgallery.service;

import com.dw.artgallery.DTO.TicketAddDTO;
import com.dw.artgallery.DTO.TicketDTO;
import com.dw.artgallery.model.ArtistGallery;
import com.dw.artgallery.model.Ticket;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.ArtistGalleryRepository;
import com.dw.artgallery.repository.TicketRepository;
import com.dw.artgallery.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    ArtistGalleryRepository artistGalleryRepository;


    //  ID로 티켓 조회
    public TicketDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("티켓을 찾을 수 없습니다: " + id));
        return ticket.toDto();
    }

    //  날짜로 조회
    public List<TicketDTO> getTicketsByPurchaseDate(LocalDate date) {
        return ticketRepository.findByPurchaseDateAndIsDeletedFalse(date).stream()
                .map(Ticket::toDto)
                .toList();
    }

    //  유저 ID로 조회
    public List<TicketDTO> getTicketsByUserId(String userId) {
        return ticketRepository.findAllByUser_UserIdAndIsDeletedFalse(userId).stream()
                .map(Ticket::toDto)
                .toList();
    }


    public void softDeleteTicket(Long ticketId, User user) {
        Ticket ticket = ticketRepository.findByIdAndIsDeletedFalse(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID의 티켓을 찾을 수 없습니다: " + ticketId));
        if (!ticket.getUser().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 티켓만 삭제할 수 있습니다.");
        }
        ticket.setIsDeleted(true);
        ticketRepository.save(ticket);
    }

    public void addTicket(Long artistGalleryId, TicketAddDTO dto, User user) {
        ArtistGallery gallery = artistGalleryRepository.findById(artistGalleryId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 전시를 찾을 수 없습니다: " + artistGalleryId));

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setArtistGallery(gallery);
        ticket.setCount(dto.getCount());
        ticket.setSelectDate(dto.getSelectDate());
        ticket.setPurchaseDate(LocalDate.now());
        ticket.setIsDeleted(false);

        ticketRepository.save(ticket);
    }











}
