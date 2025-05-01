package com.dw.artgallery.repository;

import com.dw.artgallery.model.Art;
import com.dw.artgallery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ArtRepository extends JpaRepository<Art,Long> {
    List<Art> findByDeletedFalse();

    @Query("SELECT a FROM Art a WHERE a.artist.id = :artistId AND a.deleted = false")
    List<Art> findActiveArtByArtistId(@Param("artistId") Long artistId);

    List<Art> findByArtistId(Long artistId);

    List<Art> findByArtistIdIn(List<Long> artistIds);
}
