package com.dw.artgallery.model;

import com.dw.artgallery.DTO.ReviewDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name="review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false, length = 200)
    private String text;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "goods_id")
    private Goods goods;

    @Column(name = "created_at") // 작성일자
    private LocalDate createdAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    public  ReviewDTO toDto() {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setText(this.text);
        reviewDTO.setNickname(this.user.getNickName());
        reviewDTO.setCreatedAt(this.getCreatedAt());
        return reviewDTO;
    }


}
