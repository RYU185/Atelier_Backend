package com.dw.artgallery.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentAddDTO {
    private Long communityId;
    private String text;
    private Long commentId;
    private String userNickname;
    private String userId;
    private LocalDateTime creationDate;
}
