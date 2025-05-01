package com.dw.artgallery.repository;

import com.dw.artgallery.enums.ReservationStatus;
import com.dw.artgallery.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // 새로운 예약 생성 시에만 사용
    @Query
    ("""
    SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
    FROM Reservation r
    WHERE r.user = :user
    AND r.reserveTime.reserveDate.date = :date
    AND r.reservationStatus = :status
    """)
    boolean existsDuplicateReservation(@Param("user") User user, @Param("date") LocalDate date, @Param("status") ReservationStatus status);

    // 예약 변경시에만 사용
    @Query
    ("""
    SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
    FROM Reservation r
    WHERE r.user = :user
    AND r.reserveTime.reserveDate.date = :date
    AND r.reservationStatus = :status
    AND r.id != :excludeId
    """)
    boolean existsDuplicateReservationExceptSelf(@Param("user") User user,
                                                 @Param("date") LocalDate date,
                                                 @Param("status") ReservationStatus status,
                                                 @Param("excludeId") Long excludeId);

    List<Reservation> findByUser(User user);

    List<Reservation> findByUserOrderByCreatedAtDesc(User user);

    @Query("""
    SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
    FROM Reservation r
    WHERE r.user = :user AND r.reserveTime = :time AND r.reservationStatus = 'RESERVED'
    """)
    boolean existsReservedByUserAndTime(@Param("user") User user, @Param("time") ReserveTime time);


    @Query
    ("""
    SELECT r FROM Reservation r
    WHERE r.reserveTime.reserveDate.artistGallery.id = :galleryId
    ORDER BY r.reserveTime.reserveDate.date ASC, r.reserveTime.time ASC
    """)
    List<Reservation> findAllByGalleryId(@Param("galleryId") Long galleryId);


    @Query("""
    SELECT r FROM Reservation r
    WHERE r.reservationStatus = 'RESERVED'
    """)
    List<Reservation> findAllReserved();

    @Query("""
    SELECT r FROM Reservation r
    WHERE r.reserveTime.reserveDate.date = :targetDate
    AND r.reservationStatus = 'RESERVED'
    """)
    List<Reservation> findReservedByReserveDate(@Param("targetDate") LocalDate targetDate);



}


