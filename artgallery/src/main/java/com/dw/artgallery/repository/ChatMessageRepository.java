package com.dw.artgallery.repository;

import com.dw.artgallery.model.ChatMessage;
import com.dw.artgallery.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
    List<ChatMessage> findByChatRoomOrderByTimestampAsc(ChatRoom chatRoom);
    Optional<ChatMessage> findTopByChatRoomIdOrderByTimestampDesc(Long chatRoomId);
}
