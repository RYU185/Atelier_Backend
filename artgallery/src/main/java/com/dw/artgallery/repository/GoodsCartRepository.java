package com.dw.artgallery.repository;

import com.dw.artgallery.model.Goods;
import com.dw.artgallery.model.GoodsCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsCartRepository extends JpaRepository<GoodsCart,Long> {
    public List<GoodsCart> findByUser_UserId(String userId);

}
