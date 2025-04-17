package com.dw.artgallery.repository;

import com.dw.artgallery.model.Goods;
import com.dw.artgallery.model.GoodsCart;
import com.dw.artgallery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsCartRepository extends JpaRepository<GoodsCart,Long> {
    public List<GoodsCart> findByUser_UserId(String userId);

    void deleteAllByIdIn(List<Long> cartIdList);
}
