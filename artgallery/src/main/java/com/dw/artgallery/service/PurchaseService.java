package com.dw.artgallery.service;


import com.dw.artgallery.DTO.*;
import com.dw.artgallery.model.*;
import com.dw.artgallery.repository.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final GoodsRepository goodsRepository;
    private final GoodsCartRepository goodsCartRepository;
    private final PurchaseGoodsRepository purchaseGoodsRepository;


    @Transactional
    public PurchaseResponseDTO buyNow(String userId, BuyNowRequestDTO request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        Goods goods = goodsRepository.findById(request.getGoodsId())
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        if (goods.getStock() < request.getQuantity()) {
            throw new IllegalArgumentException("재고가 부족합니다: " + goods.getName());
        }

        // 재고 감소
        goods.setStock(goods.getStock() - request.getQuantity());

        // PurchaseGoods 생성
        PurchaseGoods purchaseGoods = new PurchaseGoods();
        purchaseGoods.setGoods(goods);
        purchaseGoods.setQuantity(request.getQuantity());
        purchaseGoods.setPrice(goods.getPrice());
        purchaseGoods.setIsDelete(false);

        // Purchase 생성
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setPurchaseDate(LocalDate.now());
        purchase.setIsDelete(false);
        purchase.setTotalPrice(goods.getPrice() * request.getQuantity());
        purchase.setPurchaseGoodsList(List.of(purchaseGoods));

        // 관계 설정
        purchaseGoods.setPurchase(purchase);

        // 저장
        purchaseRepository.save(purchase);

        return PurchaseResponseDTO.fromEntity(purchase);
    }


    @Transactional
    public PurchaseResponseDTO purchaseSelectedCarts(String userId, List<CartPurchaseRequestDTO> cartItems) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        List<Long> cartIds = cartItems.stream()
                .map(CartPurchaseRequestDTO::getCartId)
                .toList();

        List<GoodsCart> carts = goodsCartRepository.findAllById(cartIds);

        Map<Long, Integer> quantityMap = cartItems.stream()
                .collect(Collectors.toMap(CartPurchaseRequestDTO::getCartId, CartPurchaseRequestDTO::getQuantity));

        List<PurchaseGoods> purchaseGoodsList = new ArrayList<>();
        int totalPrice = 0;

        for (GoodsCart cart : carts) {
            if (!cart.getUser().getUserId().equals(user.getUserId())) {
                throw new SecurityException("다른 사용자의 장바구니 항목입니다.");
            }

            int quantity = quantityMap.get(cart.getId());
            Goods goods = cart.getGoods();

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
        goodsCartRepository.deleteAllById(cartIds);

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
    public List<GoodsStatDTO> getGoodsStatsByMonth() {
        List<PurchaseGoods> items = purchaseGoodsRepository.findAll(); // 필요한 경우 `findAllByIsDeleteFalse()`로 대체

        if (items.isEmpty()) return Collections.emptyList();

        int currentYear = LocalDate.now().getYear();

        List<PurchaseGoods> filtered = items.stream()
                .filter(pg -> pg.getPurchase() != null &&
                        pg.getPurchase().getPurchaseDate() != null &&
                        pg.getPurchase().getPurchaseDate().getYear() == currentYear &&
                        (pg.getIsDelete() == null || !pg.getIsDelete()))
                .toList();

        Map<Integer, Long> monthlyMap = new LinkedHashMap<>();
        for (int month = 1; month <= 12; month++) {
            monthlyMap.put(month, 0L);
        }

        for (PurchaseGoods pg : filtered) {
            int month = pg.getPurchase().getPurchaseDate().getMonthValue();
            monthlyMap.put(month, monthlyMap.get(month) + pg.getQuantity());
        }

        List<GoodsStatDTO> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            result.add(new GoodsStatDTO(month + "월", monthlyMap.get(month)));
        }

        return result;
    }


}
