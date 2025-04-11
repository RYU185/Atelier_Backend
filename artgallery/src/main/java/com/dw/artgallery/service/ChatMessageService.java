package com.dw.artgallery.service;

import com.dw.artgallery.DTO.ChatMessageDTO;
import com.dw.artgallery.model.ChatMessage;
import com.dw.artgallery.model.ChatRoom;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.ChatMessageRepository;
import com.dw.artgallery.repository.ChatRoomRepository;
import com.dw.artgallery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public ChatMessage saveMessage(ChatMessageDTO dto, ChatRoom chatRoom) {
        User sender = userRepository.findById(dto.getSender())
                .orElseThrow(() -> new IllegalArgumentException("보낸 사람 없음"));

        ChatMessage message = new ChatMessage();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setText(dto.getContent());
        message.setTimestamp(LocalDateTime.now());

        return chatMessageRepository.save(message);
    }

}
