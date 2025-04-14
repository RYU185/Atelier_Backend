package com.dw.artgallery.repository;

import com.dw.artgallery.enums.ReservationStatus;
import com.dw.artgallery.model.Reservation;
import com.dw.artgallery.model.ReserveDate;
import com.dw.artgallery.model.ReserveTime;
import com.dw.artgallery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
    SELECT COUNT(r) > 0
    FROM Reservation r
    WHERE r.user = :user
    AND r.reserveTime.reserveDate.date = :date
    AND r.reservationStatus = :status
    """)
    boolean existsDuplicateReservation(User user, LocalDate date, ReservationStatus status);


    List<Reservation> findByUser(User user);

    List<Reservation> findByUserOrderByCreatedAtDesc(User user);

    boolean existsByUserAndReserveTime(User user, ReserveTime time);
}
