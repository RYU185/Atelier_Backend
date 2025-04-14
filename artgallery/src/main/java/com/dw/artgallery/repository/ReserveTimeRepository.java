package com.dw.artgallery.repository;

import com.dw.artgallery.model.ReserveTime;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReserveTimeRepository extends JpaRepository<ReserveTime, Long> {

    @Query("""
        SELECT rt FROM ReserveTime rt
        JOIN FETCH rt.reserveDate rd
        JOIN FETCH rd.artistGallery
        WHERE rt.id = :id
    """)
    Optional<ReserveTime> findByIdWithFullJoin(@Param("id") Long id);

    @Query("""
    SELECT rt FROM ReserveTime rt
    JOIN FETCH rt.reserveDate rd
    WHERE rd.date = :date
    ORDER BY rt.time ASC
""")
    List<ReserveTime> findByReserveDate_Date(@Param("date") LocalDate date);
}

