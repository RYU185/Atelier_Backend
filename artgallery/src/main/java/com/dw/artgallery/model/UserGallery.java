package com.dw.artgallery.model;

import com.dw.artgallery.DTO.UserGalleryDTO;
import com.dw.artgallery.DTO.UserGalleryDetailDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name="user_gallery")
public class UserGallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title",nullable = false)
    private String title;

    @Column(name="poster_url",nullable = false)
    private String posterUrl;

    @Column(name="price")
    private double price; //가격은 0원으로 동일 하지만 예약과 티켓 관리를 위해 컬럼 정의

    @Column(name = "description",nullable = false)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @ManyToMany
    @JoinTable(name = "user_gallery_user",
            joinColumns = @JoinColumn(name = "user_gallery_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> userList = new ArrayList<>();

    @OneToMany(mappedBy = "userGallery")
    private List<Drawing> drawingList = new ArrayList<>();


    public UserGalleryDetailDTO toDto(){
        UserGalleryDetailDTO userGalleryDetailDTO =  new UserGalleryDetailDTO();
        userGalleryDetailDTO.setTitle(this.title);
        userGalleryDetailDTO.setPosterUrl(this.posterUrl);
        userGalleryDetailDTO.setDescription(this.description);
        userGalleryDetailDTO.setStartDate(this.startDate);
        userGalleryDetailDTO.setEndDate(this.endDate);
        userGalleryDetailDTO.setPrice(this.price == 0 ? "무료" : "책정 없음");
        List<String> users = new ArrayList<>();
        for (User data : userList) {
            users.add(data.getNickName());
        }
        userGalleryDetailDTO.setUserList(users);
        List<String> drawings = new ArrayList<>();
        for (Drawing data : drawingList) {
            drawings.add(data.getImgUrl());
        }
        userGalleryDetailDTO.setDrawingImg(drawings);
        return userGalleryDetailDTO;
    }

    public UserGalleryDTO ToDTO(){
        UserGalleryDTO userGalleryDTO = new UserGalleryDTO();
        userGalleryDTO.setId(this.id);
        userGalleryDTO.setTitle(this.title);
        userGalleryDTO.setDescription(this.description);
        userGalleryDTO.setPosterUrl(this.posterUrl);
        userGalleryDTO.setStartDate(this.startDate);
        userGalleryDTO.setEndDate(this.endDate);
        return userGalleryDTO;

    }

}
