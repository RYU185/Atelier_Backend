package com.dw.artgallery.repository;

import com.dw.artgallery.model.Reservation;
import com.dw.artgallery.model.ReserveDate;
import com.dw.artgallery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByUserAndReserveDate(User user, ReserveDate reserveDate);

    List<Reservation> findByUser(User user);

    List<Reservation> findByUserOrderByCreatedAtDesc(User user);
}
