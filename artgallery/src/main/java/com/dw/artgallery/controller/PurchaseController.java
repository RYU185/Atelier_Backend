package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.*;
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
    @PostMapping("/buy-now")
    public ResponseEntity<PurchaseResponseDTO> buyNow(

            @RequestBody BuyNowRequestDTO request,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        PurchaseResponseDTO response = purchaseService.buyNow(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
    public ResponseEntity<PurchaseResponseDTO> purchaseSelectedCarts(
            @RequestBody List<CartPurchaseRequestDTO> cartItems,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        PurchaseResponseDTO response = purchaseService.purchaseSelectedCarts(userId, cartItems);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/view")
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

    @GetMapping("/admin/statistics/goods-count/by-month")
    public ResponseEntity<List<GoodsStatDTO>> getMonthlyGoodsStats() {
        return ResponseEntity.ok(purchaseService.getGoodsStatsByMonth());
    }




}
