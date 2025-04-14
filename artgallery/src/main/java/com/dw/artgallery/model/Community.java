package com.dw.artgallery.model;

import com.dw.artgallery.DTO.CommunityDTO;
import com.dw.artgallery.DTO.CommunityDetailDTO;
import com.dw.artgallery.repository.CommunityLikeRepository;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "community")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text")
    private String text;


    @Column(name = "upload_date")
    private LocalDateTime uploadDate= LocalDateTime.now();

    @Column(name="modify_date",nullable = false)
    private LocalDateTime modifyDate;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name ="user_id")
    private User user;

    @OneToMany(mappedBy = "community")
    private List<Comment> commentList = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "community_drawing",
            joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "drawing_id"))
    private List<Drawing> drawingList = new ArrayList<>();


    public CommunityDTO toDto(CommunityLikeRepository communityLikeRepository){
        CommunityDTO communityDTO =  new CommunityDTO();
        communityDTO.setText(this.text);
        communityDTO.setUploadDate(this.uploadDate);
        communityDTO.setModifyDate(this.modifyDate);
        communityDTO.setUser(this.user.getNickName());

        List<String> drawingList1 = new ArrayList<>();
        for(Drawing data : drawingList){
            drawingList1.add(data.getImgUrl());
        }
        communityDTO.setDrawingList(drawingList1);

        long likesCount = communityLikeRepository.countByCommunity(this);
        communityDTO.setLikes(likesCount);

        return communityDTO;
    }


    public CommunityDetailDTO ToDto(CommunityLikeRepository communityLikeRepository) {
        CommunityDetailDTO communityDetailDTO = new CommunityDetailDTO();
        communityDetailDTO.setText(this.text);
        communityDetailDTO.setUploadDate(this.uploadDate);
        communityDetailDTO.setModifyDate(this.modifyDate);
        communityDetailDTO.setUser(this.user.getNickName());

        long likesCount = communityLikeRepository.countByCommunity(this);
        communityDetailDTO.setLikes(likesCount);

        List<String> drawingList1 = new ArrayList<>();
        for (Drawing data : drawingList) {
            drawingList1.add(data.getImgUrl());
        }
        communityDetailDTO.setDrawingList(drawingList1);

        List<String> commentUser1 = new ArrayList<>();
        List<String> commentText1 = new ArrayList<>();
        List<LocalDateTime> creationDateList1 = new ArrayList<>();
        for (Comment data : commentList) {
            if (Boolean.TRUE.equals(data.getIsDeleted())) continue;
            commentUser1.add(data.getUser().getNickName());
            commentText1.add(data.getText());
            creationDateList1.add(data.getCreationDate());
        }

        communityDetailDTO.setCommentUser(commentUser1);
        communityDetailDTO.setCommentText(commentText1);
        communityDetailDTO.setCreationDateList(creationDateList1);

        return communityDetailDTO;
    }


}
