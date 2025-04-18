package com.dw.artgallery.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class GoodsCreateDTO {
    private String name;
    private String description;
    private int price;
    private int stock;
    private List<MultipartFile> images; // 대표 이미지
}
