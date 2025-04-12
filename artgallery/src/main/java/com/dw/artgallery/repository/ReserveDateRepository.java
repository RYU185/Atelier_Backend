package com.dw.artgallery.repository;

import com.dw.artgallery.model.ReserveDate;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface ReserveDateRepository extends JpaRepository<ReserveDate, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM ReserveDate r WHERE r.artistGallery.id = :galleryId AND r.date = :date")
    Optional<ReserveDate> findByArtistGalleryAndDateWithLock(Long galleryId, LocalDate date);

    // @Lock(LockModeType.PESSIMISTIC_WRITE)
    // 비관적 락
    // 이 데이터가 사용중이라면 다른 애들은 기다려야됨

}
