package com.dw.artgallery.repository;

import com.dw.artgallery.model.RealDrawing;
import com.dw.artgallery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RealDrawingRepository extends JpaRepository<RealDrawing, Long> {
    List<RealDrawing> findByUser(User user);

}
