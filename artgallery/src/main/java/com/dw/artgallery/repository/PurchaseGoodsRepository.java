package com.dw.artgallery.repository;

import com.dw.artgallery.model.PurchaseGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseGoodsRepository extends JpaRepository<PurchaseGoods, Long> {
    List<PurchaseGoods> findByPurchase_User_UserId(String userId);
    @Query("""
    SELECT CASE WHEN COUNT(pg) > 0 THEN true ELSE false END
    FROM PurchaseGoods pg
    WHERE pg.goods.id = :goodsId
      AND pg.purchase.user.userId = :userId
""")
    boolean hasUserPurchasedGoods(@Param("goodsId") Long goodsId,
                                  @Param("userId") String userId);

    @Query("SELECT COALESCE(SUM(pg.quantity), 0) FROM PurchaseGoods pg WHERE pg.goods.id = :goodsId")
    Integer getTotalSalesByGoodsId(Long goodsId);


        List<PurchaseGoods> findAll();
    }

