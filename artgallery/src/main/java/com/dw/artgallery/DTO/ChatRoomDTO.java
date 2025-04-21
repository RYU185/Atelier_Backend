package com.dw.artgallery.DTO;

import com.dw.artgallery.model.ChatMessage;
import com.dw.artgallery.model.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {

    private Long id;                // 채팅방 ID
    private String userId;          // 유저 ID
    private String userName;        // 유저 닉네임
    private String artistId;        // 작가 ID
    private String artistName;      // 작가 닉네임
    private String lastMessage;     // 마지막 메시지 프리뷰용
    private String lastMessageTime; // 마지막 메시지 시간 문자열
    private int unreadCount;        // 안 읽은 메시지 수

    // ✅ 기존 fromEntity 그대로 유지
    public static ChatRoomDTO fromEntity(ChatRoom room) {
        String userNick = room.getUser().getNickName();
        String artistNick = room.getArtist().getNickName();

        System.out.println("✅ userNickName: " + userNick);
        System.out.println("✅ artistNickName: " + artistNick);

        return new ChatRoomDTO(
                room.getId(),
                room.getUser().getUserId(),
                userNick,
                room.getArtist().getUserId(),
                artistNick,
                null,
                null,
                0
        );
    }

    // ✅ 오버로딩된 fromEntity with last message info
    public static ChatRoomDTO fromEntity(ChatRoom room, ChatMessage lastMessage) {
        String userNick = room.getUser().getNickName();
        String artistNick = room.getArtist().getNickName();

        String lastMessageText = lastMessage != null ? lastMessage.getText() : null;
        String lastMessageTime = lastMessage != null
                ? lastMessage.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                : null;

        return new ChatRoomDTO(
                room.getId(),
                room.getUser().getUserId(),
                userNick,
                room.getArtist().getUserId(),
                artistNick,
                lastMessageText,
                lastMessageTime,
                0
        );
    }
}
