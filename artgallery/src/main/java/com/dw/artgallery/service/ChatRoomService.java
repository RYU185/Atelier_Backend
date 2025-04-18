package com.dw.artgallery.service;

import com.dw.artgallery.DTO.ChatMessageDTO;
import com.dw.artgallery.exception.ResourceNotFoundException;
import com.dw.artgallery.model.ChatMessage;
import com.dw.artgallery.model.ChatRoom;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.ChatMessageRepository;
import com.dw.artgallery.repository.ChatRoomRepository;
import com.dw.artgallery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public List<ChatMessageDTO> getMessagesByRoomId(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("채팅방을 찾을 수 없습니다."));

        List<ChatMessage> messages = chatMessageRepository.findByChatRoomOrderByTimestampAsc(chatRoom);

        return messages.stream().map(ChatMessageDTO::fromEntity).toList();
    }

    public ChatRoom getOrCreateRoom(String senderId, String receiverId){
        User sender = userRepository.findById(senderId)
                .orElseThrow(()-> new ResourceNotFoundException("보낸 유저를 찾을 수 없습니다"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(()-> new ResourceNotFoundException("받는 유저를 찾을 수 없습니다"));

        User user = sender.isArtist() ? receiver : sender;
        User artist = sender.isArtist() ? sender : receiver;

        if (!artist.isArtist()) {
            throw new IllegalArgumentException("상대방은 작가가 아닙니다.");
        }

        return chatRoomRepository.findByUserAndArtist(user, artist)
                .orElseGet(() -> {
                    ChatRoom room = new ChatRoom();
                    room.setUser(user);
                    room.setArtist(artist);
                    return chatRoomRepository.save(room);
                });
    }

    public List<ChatRoom> getMyChatRooms(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("유저를 찾을 수 없습니다."));

        if (user.isArtist()) {
            return chatRoomRepository.findByArtist(user);
        } else {
            return chatRoomRepository.findByUser(user);
        }
    }

}
