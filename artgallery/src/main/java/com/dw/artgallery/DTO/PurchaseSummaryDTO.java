package com.dw.artgallery.DTO;

import com.dw.artgallery.model.PurchaseGoods;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseSummaryDTO {
    private Long purchaseId;
    private String goodsName;
    private String thumbnailUrl;
    private int quantity;
    private int price;
    private LocalDate purchaseDate;
    private Long goodsId;

    public static PurchaseSummaryDTO fromEntity(PurchaseGoods purchaseGoods) {
        PurchaseSummaryDTO purchaseSummaryDTO = new PurchaseSummaryDTO();
        purchaseSummaryDTO.setPurchaseId(purchaseGoods.getPurchase().getId());
        purchaseSummaryDTO.setGoodsName(purchaseGoods.getGoods().getName());
        purchaseSummaryDTO.setThumbnailUrl(purchaseGoods.getGoods().getThumbnailUrl());
        purchaseSummaryDTO.setQuantity(purchaseGoods.getQuantity());
        purchaseSummaryDTO.setPrice(purchaseGoods.getPrice());
        purchaseSummaryDTO.setPurchaseDate(purchaseGoods.getPurchase().getPurchaseDate());
        purchaseSummaryDTO.setGoodsId(purchaseGoods.getGoods().getId());
        return purchaseSummaryDTO;
    }
}
