package com.dw.artgallery.repository;

import com.dw.artgallery.model.Purchase;
import com.dw.artgallery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase,Long> {
    List<Purchase> findByUserAndIsDeleteFalse(User user);
}
