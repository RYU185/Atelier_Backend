package com.dw.artgallery.repository;

import com.dw.artgallery.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    Optional<Ticket> findByIdAndIsDeletedFalse(Long id);
    List<Ticket> findByPurchaseDateAndIsDeletedFalse(LocalDate date);
    List<Ticket> findAllByUser_UserIdAndIsDeletedFalse(String userId);
}
