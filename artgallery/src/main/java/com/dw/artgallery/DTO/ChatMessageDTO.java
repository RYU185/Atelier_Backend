package com.dw.artgallery.DTO;

import com.dw.artgallery.enums.MessageType;
import com.dw.artgallery.model.ChatMessage;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDTO {
    private MessageType type;
    private String content;
    private String sender;
    private String receiver;
    private String timestamp;
    private String senderNickname;
    private String tempId;

    public static ChatMessageDTO fromEntity(ChatMessage message) {
        return ChatMessageDTO.builder()
                .sender(message.getSender().getUserId())
                .receiver(
                        message.getChatRoom().getUser().getUserId().equals(message.getSender().getUserId())
                                ? message.getChatRoom().getArtist().getUserId()
                                : message.getChatRoom().getUser().getUserId()
                )
                .content(message.getText())
                .type(MessageType.CHAT)
                .senderNickname(message.getSender().getNickName())
                .timestamp(message.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
                .build();
    }
}