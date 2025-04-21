package com.dw.artgallery.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name="ChatMessage")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.EAGER) // ✅ 즉시 로딩
    @JoinColumn(name="user_id")
    private User sender;

    @Column(name="text",nullable = false)
    private String text;

    @Column(name="img")
    private String img;

    @Column(name="timestamp",nullable = false)
    private LocalDateTime timestamp;

}
