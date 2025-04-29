package com.dw.artgallery.repository;

import com.dw.artgallery.model.ChatRoom;
import com.dw.artgallery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT cr FROM ChatRoom cr JOIN FETCH cr.user JOIN FETCH cr.artist WHERE cr.user = :user AND cr.artist = :artist")
    Optional<ChatRoom> findByUserAndArtist(@Param("user") User user, @Param("artist") User artist);

    List<ChatRoom> findByUser(User user);

    List<ChatRoom> findByArtist(User artist);
}
