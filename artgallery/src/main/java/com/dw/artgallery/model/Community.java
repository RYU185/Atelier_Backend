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
    @JoinTable(name = "community_uploadimg",
            joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "uploadimg_id"))
    private List<UploadIMG> communityIMGS = new ArrayList<>();


    public CommunityDTO toDto(CommunityLikeRepository communityLikeRepository){
        CommunityDTO communityDTO =  new CommunityDTO();
        communityDTO.setId(this.id);
        communityDTO.setText(this.text);
        communityDTO.setUploadDate(this.uploadDate);
        communityDTO.setModifyDate(this.modifyDate);
        communityDTO.setUserNickname(this.user.getNickName());

        List<String> imgs = new ArrayList<>();
        for(UploadIMG data : communityIMGS){
            imgs.add(data.getImgUrl());
        }
        communityDTO.setImg(imgs);

        long likesCount = communityLikeRepository.countByCommunity(this);
        communityDTO.setLikes(likesCount);


        int commentCount = (int) this.commentList.stream()
                .filter(comment -> !Boolean.TRUE.equals(comment.getIsDeleted()))
                .count();
        communityDTO.setCommentCount(commentCount);

        return communityDTO;
    }


    public CommunityDetailDTO ToDto(CommunityLikeRepository communityLikeRepository) {
        CommunityDetailDTO communityDetailDTO = new CommunityDetailDTO();
        communityDetailDTO.setId(this.id);
        communityDetailDTO.setText(this.text);
        communityDetailDTO.setUploadDate(this.uploadDate);
        communityDetailDTO.setModifyDate(this.modifyDate);
        communityDetailDTO.setUser(this.user.getNickName());

        long likesCount = communityLikeRepository.countByCommunity(this);
        communityDetailDTO.setLikes(likesCount);

        List<String> imgs = new ArrayList<>();
        for(UploadIMG data : communityIMGS){
            imgs.add(data.getImgUrl());
        }
        communityDetailDTO.setImg(imgs);


        List<Long> commentId = new ArrayList<>();
        List<String> commentUser1 = new ArrayList<>();
        List<String> commentText1 = new ArrayList<>();
        List<LocalDateTime> creationDateList1 = new ArrayList<>();
        for (Comment data : commentList) {
            if (Boolean.TRUE.equals(data.getIsDeleted())) continue;
            commentId.add(data.getId());
            commentUser1.add(data.getUser().getNickName());
            commentText1.add(data.getText());
            creationDateList1.add(data.getCreationDate());
        }

        communityDetailDTO.setCommentId(commentId);
        communityDetailDTO.setCommentUser(commentUser1);
        communityDetailDTO.setCommentText(commentText1);
        communityDetailDTO.setCreationDateList(creationDateList1);

        return communityDetailDTO;
    }


}
