package com.dw.artgallery.service;

import com.dw.artgallery.DTO.ChatMessageDTO;
import com.dw.artgallery.model.ChatMessage;
import com.dw.artgallery.model.ChatRoom;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.ChatMessageRepository;
import com.dw.artgallery.repository.ChatRoomRepository;
import com.dw.artgallery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    @Transactional
    public ChatMessage saveMessage(ChatMessageDTO dto, ChatRoom chatRoom) {
        User sender = userRepository.findById(dto.getSender())
                .orElseThrow(() -> new IllegalArgumentException("보낸 사람 없음"));

        User receiver = userRepository.findById(dto.getReceiver())
                .orElseThrow(() -> new IllegalArgumentException("받는 사람 없음"));

        // 메시지 엔티티 생성
        ChatMessage message = new ChatMessage();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setText(dto.getContent());
        message.setTimestamp(LocalDateTime.now());

        // DB 저장
        ChatMessage savedMessage = chatMessageRepository.save(message);

        // DTO 재생성 (닉네임, 타임스탬프 포함)
        ChatMessageDTO dtoToSend = ChatMessageDTO.fromEntity(savedMessage);
        dtoToSend.setTempId(dto.getTempId());

        // 실시간 WebSocket 메시지 전송
        messagingTemplate.convertAndSendToUser(
                receiver.getUserId(),
                "/queue/messages",
                dtoToSend
        );

        return savedMessage;
    }

    public ChatMessage getLastMessage(Long roomId) {
        return chatMessageRepository.findTopByChatRoomIdOrderByTimestampDesc(roomId)
                .orElse(null);
    }
}

