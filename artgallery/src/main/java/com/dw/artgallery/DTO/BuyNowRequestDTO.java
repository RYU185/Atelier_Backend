package com.dw.artgallery.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyNowRequestDTO {
    private Long goodsId;
    private int quantity;
}
