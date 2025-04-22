package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.GoodsCartDTO;
import com.dw.artgallery.DTO.GoodsStatDTO;
import com.dw.artgallery.DTO.PurchaseResponseDTO;
import com.dw.artgallery.DTO.PurchaseSummaryDTO;
import com.dw.artgallery.service.GoodsService;
import com.dw.artgallery.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final GoodsService goodsService;
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<PurchaseResponseDTO> purchaseSelectedCarts(
            @RequestBody List<Long> cartIdList,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        PurchaseResponseDTO response = purchaseService.purchaseSelectedCarts(userId, cartIdList);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<PurchaseSummaryDTO>> getMyPurchaseHistory(Authentication authentication){
        String userId = authentication.getName();
        return new ResponseEntity<>(purchaseService.getMyPurchaseHistory(userId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/delete/{purchaseId}")
    public ResponseEntity<Void> logicallyDeletePurchase(@PathVariable Long purchaseId, Authentication authentication) {
        String userId = authentication.getName();
        purchaseService.logicallyDeletePurchase(userId, purchaseId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/goods/admin/statistics/monthly")
    public ResponseEntity<List<GoodsStatDTO>> getMonthlyGoodsStats() {
        return ResponseEntity.ok(purchaseService.getMonthlyGoodsSalesStats());
    }




}
