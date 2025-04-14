package com.dw.artgallery.repository;

import com.dw.artgallery.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community,Long> {

    @Query("SELECT c FROM Community c WHERE c.isDeleted = false")
    List<Community> findAllNotDeleted();

    @Query("SELECT c FROM Community c WHERE c.user.userId = :userId AND c.isDeleted = false")
    List<Community> findByUserIdNotDeleted(@Param("userId") String userId);

    @Query("SELECT c FROM Community c WHERE c.id = :id AND c.isDeleted = false")
    Optional<Community> findByIdNotDeleted(@Param("id") Long id);

    @Query("SELECT c FROM Community c WHERE c.isDeleted = false ORDER BY c.uploadDate DESC")
    List<Community> findRecentCommunities();

    @Query("SELECT c FROM Community c WHERE c.isDeleted = false ORDER BY c.uploadDate ASC")
    List<Community> findOldestCommunities();


    @Query("SELECT c FROM Community c WHERE c.isDeleted = false")
    List<Community> findAllWithLikes();
}
