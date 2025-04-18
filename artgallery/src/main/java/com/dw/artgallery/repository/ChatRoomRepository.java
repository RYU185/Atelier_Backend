package com.dw.artgallery.repository;

import com.dw.artgallery.model.ChatRoom;
import com.dw.artgallery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByUserAndArtist(User user, User artist);

    List<ChatRoom> findByUser(User user);

    List<ChatRoom> findByArtist(User artist);
}
