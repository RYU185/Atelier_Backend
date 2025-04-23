package com.dw.artgallery.model;

import com.dw.artgallery.DTO.DrawingDTO;
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
@Table(name="drawing")
public class Drawing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="img_url")
    private String imgUrl;

    @Column(name="title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name="completion_date")
    private LocalDate completionDate;

    @Column(name = "is_complete")
    private Boolean isComplete;

    @Column(name = "is_delete")
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;




    @ManyToOne
    @JoinColumn(name = "user_gallery_id")
    private UserGallery userGallery;



    public DrawingDTO toDto(){
        DrawingDTO drawingDTO = new DrawingDTO();
        drawingDTO.setImgUrl(this.imgUrl);
        drawingDTO.setTitle(this.title);
        drawingDTO.setCompletionDate(this.completionDate);
        return drawingDTO;
    }


}
