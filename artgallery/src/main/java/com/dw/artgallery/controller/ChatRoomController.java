package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.ChatMessageDTO;
import com.dw.artgallery.model.ChatRoom;
import com.dw.artgallery.service.ChatMessageService;
import com.dw.artgallery.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-room")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessagesByRoomId(@PathVariable Long roomId, Authentication authentication){
        String userId = authentication.getName();
        return new ResponseEntity<>(chatRoomService.getMessagesByRoomId(roomId), HttpStatus.OK);
    }
}