package com.dw.artgallery.repository;

import com.dw.artgallery.DTO.ArtistGalleryDTO;
import com.dw.artgallery.model.ArtistGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface ArtistGalleryRepository extends JpaRepository <ArtistGallery,Long>{
    List<ArtistGallery> findByTitleLike(String title);

    @Query("SELECT a FROM ArtistGallery a WHERE a.startDate <= :today AND a.endDate >= :today")
    List<ArtistGallery> findNowGallery(@Param("today") LocalDate today);

    @Query("SELECT a FROM ArtistGallery a WHERE a.endDate < :today")
    List<ArtistGallery> findPastGallery(@Param("today") LocalDate today);

    @Query("SELECT a FROM ArtistGallery a WHERE a.startDate > :today")
    List<ArtistGallery> findExpectedGallery(@Param("today") LocalDate today);

    List<ArtistGallery> findByTitleContainingIgnoreCase(String title);
}
