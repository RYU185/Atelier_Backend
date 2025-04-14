package com.dw.artgallery.repository;

import com.dw.artgallery.model.ReserveTime;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReserveTimeRepository extends JpaRepository<ReserveTime, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT rt FROM ReserveTime rt JOIN FETCH rt.reserveDate WHERE rt.id = :id")
    Optional<ReserveTime> findByIdWithLock(Long id);
}
