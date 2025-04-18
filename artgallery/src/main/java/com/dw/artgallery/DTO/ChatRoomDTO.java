package com.dw.artgallery.DTO;

import com.dw.artgallery.model.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public static ChatRoomDTO fromEntity(ChatRoom room) {
        return new ChatRoomDTO(
                room.getId(),
                room.getUser().getUserId(),
                room.getUser().getNickName(),
                room.getArtist().getUserId(),
                room.getArtist().getNickName(),
                null,       // 이후 마지막 메시지에서 시작
                null,
                0
        );
    }
}
