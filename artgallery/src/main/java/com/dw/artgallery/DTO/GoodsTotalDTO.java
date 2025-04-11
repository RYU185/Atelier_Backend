package com.dw.artgallery.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoodsTotalDTO {
    private Long id;
    private String name;
    private List<String> imgUrlList;
    private int stock;
    private int totalSales;
}
