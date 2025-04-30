    package com.dw.artgallery.controller;

    import com.dw.artgallery.DTO.ChatMessageDTO;
    import com.dw.artgallery.model.ChatRoom;
    import com.dw.artgallery.model.ChatMessage;
    import com.dw.artgallery.model.User;
    import com.dw.artgallery.service.ChatRoomService;
    import com.dw.artgallery.service.ChatMessageService;
    import com.dw.artgallery.service.UserService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.messaging.handler.annotation.MessageMapping;
    import org.springframework.messaging.handler.annotation.Payload;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.stereotype.Controller;

    @RequiredArgsConstructor
    @Controller
    public class ChatMessageController {

        private final ChatRoomService chatRoomService;
        private final UserService userService;
        private final ChatMessageService chatMessageService;
        private final SimpMessagingTemplate messagingTemplate;

        @MessageMapping("/chat.send") // 클라이언트 → /app/chat.send 로 전송
        public void sendMessage(@Payload ChatMessageDTO chatMessageDTO) {


            // 1. 채팅방 조회 or 생성
            ChatRoom chatRoom = chatRoomService.getOrCreateRoom(
                    chatMessageDTO.getSender(),   // Long
                    chatMessageDTO.getReceiver()  // Long
            );

            // 2. 메시지 저장
            ChatMessage saved = chatMessageService.saveMessage(chatMessageDTO, chatRoom);

            ChatMessageDTO dtoToSend = ChatMessageDTO.fromEntity(saved);
            dtoToSend.setTempId(chatMessageDTO.getTempId());

            // 3. 상대 유저에게 메시지 전송 (WebSocket 전용 queue)
            User receiver = userService.getUserEntityById(chatMessageDTO.getReceiver());


        }
    }
