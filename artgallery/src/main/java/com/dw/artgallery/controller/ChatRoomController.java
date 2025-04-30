package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.ChatMessageDTO;
import com.dw.artgallery.DTO.ChatRoomDTO;
import com.dw.artgallery.model.ChatRoom;
import com.dw.artgallery.service.ChatMessageService;
import com.dw.artgallery.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-room")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @PostMapping("/{artistId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChatRoomDTO> getOrCreateRoom(
            @PathVariable String artistId,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        ChatRoom room = chatRoomService.getOrCreateRoom(userId, artistId);
        return ResponseEntity.ok(ChatRoomDTO.fromEntity(room));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ChatRoomDTO>> getMyChatRooms(Authentication authentication) {
        String userId = authentication.getName();
        List<ChatRoom> rooms = chatRoomService.getMyChatRooms(userId);

        List<ChatRoomDTO> result = rooms.stream().map(room -> {
            var last = chatMessageService.getLastMessage(room.getId());
            return ChatRoomDTO.fromEntity(room, last);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{roomId}/messages")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ChatMessageDTO>> getMessagesByRoomId(
            @PathVariable Long roomId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(chatRoomService.getMessagesByRoomId(roomId));
    }
}
