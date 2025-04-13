package com.dw.artgallery.repository;

import com.dw.artgallery.enums.ReservationStatus;
import com.dw.artgallery.model.Reservation;
import com.dw.artgallery.model.ReserveDate;
import com.dw.artgallery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByUserAndReserveDate(User user, ReserveDate reserveDate);

    boolean existByUserAndReserveDate_DateAndReservationStatus(User user, LocalDate date, ReservationStatus status);

    List<Reservation> findByUser(User user);

    List<Reservation> findByUserOrderByCreatedAtDesc(User user);

}
