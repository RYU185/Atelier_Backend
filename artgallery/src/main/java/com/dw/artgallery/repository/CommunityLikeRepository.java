package com.dw.artgallery.repository;



import com.dw.artgallery.model.Community;
import com.dw.artgallery.model.CommunityLike;
import com.dw.artgallery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {
    Optional<CommunityLike> findByUserAndCommunity(User user, Community community);
    int countByCommunity(Community community);

}