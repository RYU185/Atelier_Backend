package com.dw.artgallery.model;

import com.dw.artgallery.DTO.GoodsDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name="goods")
public class  Goods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name",nullable = false)
    private String name;

    @ElementCollection
    @CollectionTable(name = "goods_images", joinColumns = @JoinColumn(name = "goods_id"))
    private List<String> imgUrlList;

    @Column(name = "description",nullable = false)
    private String description;

    @Column(name="price",nullable = false)
    private int price;

    @Column(name="stock",nullable = false)
    private int stock;

    public String getThumbnailUrl() {
        if (imgUrlList != null && !imgUrlList.isEmpty()) {
            return imgUrlList.getFirst(); // 대표 이미지
        }
        return null;
    }


}
