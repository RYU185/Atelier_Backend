package com.dw.artgallery.DTO;

import com.dw.artgallery.model.Goods;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GoodsDTO {
    private Long id;

    private String name;

    private List<String> imgUrlList;

    private String description;

    private int price;

    private int stock;


    public static GoodsDTO fromEntity(Goods goods){
        return new GoodsDTO(
                goods.getId(),
                goods.getName(),
                goods.getImgUrlList(),
                goods.getDescription(),
                goods.getPrice(),
                goods.getStock()
        );
    }
}
