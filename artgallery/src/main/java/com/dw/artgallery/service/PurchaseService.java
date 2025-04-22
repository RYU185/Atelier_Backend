package com.dw.artgallery.service;


import com.dw.artgallery.DTO.GoodsStatDTO;
import com.dw.artgallery.DTO.PurchaseResponseDTO;
import com.dw.artgallery.DTO.PurchaseSummaryDTO;
import com.dw.artgallery.model.*;
import com.dw.artgallery.repository.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final GoodsRepository goodsRepository;
    private final GoodsCartRepository goodsCartRepository;
    private final PurchaseGoodsRepository purchaseGoodsRepository;


    @Transactional
    public PurchaseResponseDTO purchaseSelectedCarts(String userId, List<Long> cartIdList) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));


        List<GoodsCart> carts = goodsCartRepository.findAllById(cartIdList);

        if (carts.isEmpty()) {
            throw new IllegalArgumentException("선택된 장바구니 항목이 존재하지 않습니다.");
        }


        for (GoodsCart cart : carts) {
            if (!cart.getUser().getUserId().equals(user.getUserId())) {
                throw new SecurityException("다른 사용자의 장바구니 항목을 구매할 수 없습니다.");
            }
        }

        List<PurchaseGoods> purchaseGoodsList = new ArrayList<>();
        int totalPrice = 0;

        for (GoodsCart cart : carts) {
            Goods goods = cart.getGoods();
            int quantity = cart.getAmount();

            if (goods.getStock() < quantity) {
                throw new IllegalArgumentException("재고 부족: " + goods.getName());
            }


            goods.setStock(goods.getStock() - quantity);

            PurchaseGoods purchaseGoods = new PurchaseGoods();
            purchaseGoods.setGoods(goods);
            purchaseGoods.setQuantity(quantity);
            purchaseGoods.setPrice(goods.getPrice());
            purchaseGoods.setIsDelete(false);

            purchaseGoodsList.add(purchaseGoods);
            totalPrice += goods.getPrice() * quantity;
        }


        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setTotalPrice(totalPrice);
        purchase.setPurchaseDate(LocalDate.now());
        purchase.setIsDelete(false);
        purchase.setPurchaseGoodsList(purchaseGoodsList);

        for (PurchaseGoods pg : purchaseGoodsList) {
            pg.setPurchase(purchase);
        }

        purchaseRepository.save(purchase);

        goodsCartRepository.deleteAllByIdIn(cartIdList);

        return PurchaseResponseDTO.fromEntity(purchase);
    }

    public List<PurchaseSummaryDTO> getMyPurchaseHistory(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        List<Purchase> purchases = purchaseRepository.findByUserAndIsDeleteFalse(user);

        return purchases.stream()
                .flatMap(purchase -> purchase.getPurchaseGoodsList().stream()
                        .filter(pg -> !Boolean.TRUE.equals(pg.getIsDelete())) // 삭제 안 된 것만
                        .map(PurchaseSummaryDTO::fromEntity))
                .toList();
    }

    @Transactional
    public void logicallyDeletePurchase(String userId, Long purchaseId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new EntityNotFoundException("구매내역 없음"));

        if (!purchase.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("본인의 구매내역만 삭제할 수 있습니다.");
        }

        purchase.setIsDelete(true);
        purchase.getPurchaseGoodsList().forEach(pg -> pg.setIsDelete(true));
    }

    @Transactional(readOnly = true)
    public List<GoodsStatDTO> getMonthlyGoodsSalesStats() {
        List<PurchaseGoods> goodsList = purchaseGoodsRepository.findAll();

        // 필터링: 삭제되지 않은 구매 && 올해 구매한 건만
        int year = LocalDate.now().getYear();
        List<PurchaseGoods> filtered = goodsList.stream()
                .filter(pg -> pg.getIsDelete() == null || !pg.getIsDelete())
                .filter(pg -> {
                    LocalDate date = pg.getPurchase().getPurchaseDate();
                    return date != null && date.getYear() == year;
                })
                .toList();

        // 1~12월 미리 0으로 초기화
        Map<Integer, Long> monthlyMap = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) monthlyMap.put(i, 0L);

        // 월별 수량 합산
        for (PurchaseGoods pg : filtered) {
            int month = pg.getPurchase().getPurchaseDate().getMonthValue();
            monthlyMap.put(month, monthlyMap.get(month) + pg.getQuantity());
        }

        // DTO로 변환
        List<GoodsStatDTO> result = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String label = i + "월";
            result.add(new GoodsStatDTO(label, monthlyMap.get(i)));
        }

        return result;
    }
}
