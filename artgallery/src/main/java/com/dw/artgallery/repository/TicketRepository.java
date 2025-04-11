package com.dw.artgallery.repository;

import com.dw.artgallery.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    List<Ticket> findByPurchaseDate(LocalDate purchaseDate);
    List<Ticket> findAllByUser_UserId(String userId);
}
