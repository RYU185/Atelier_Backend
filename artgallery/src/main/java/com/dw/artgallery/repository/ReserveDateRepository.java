package com.dw.artgallery.repository;

import com.dw.artgallery.DTO.ReservationStatDTO;
import com.dw.artgallery.model.ReserveDate;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReserveDateRepository extends JpaRepository<ReserveDate, Long> {
    @Query("""
    SELECT r FROM ReserveDate r
    JOIN FETCH r.artistGallery
    WHERE r.id = :id
""")
    Optional<ReserveDate> findByIdWithGalleryLock(@Param("id") Long id);

    List<ReserveDate> findByArtistGallery_Id(Long galleryId);

    // @Lock(LockModeType.PESSIMISTIC_WRITE)
    // 비관적 락
    // 이 데이터가 사용중이라면 다른 애들은 기다려야됨

    // 낙관적 락
    // @Version + OptimisticLockingFailureException
    // 비관적 락보다 성능이 우수하고 동시 요청처리에 매우 유리하다
    // 예외 발생 시 재시도 로직 코드가 필요!!
}
