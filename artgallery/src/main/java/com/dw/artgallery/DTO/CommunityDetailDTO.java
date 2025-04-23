package com.dw.artgallery.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommunityDetailDTO {
    private String text;
    private Long likes;
    private LocalDateTime uploadDate;
    private LocalDateTime modifyDate;
    private String user;
    private List<String> img = new ArrayList<>();
    private List<String> commentUser= new ArrayList<>();
    private List<String> commentText = new ArrayList<>();
    private List<LocalDateTime> creationDateList = new ArrayList<>();
}
