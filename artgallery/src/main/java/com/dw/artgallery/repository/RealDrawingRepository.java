package com.dw.artgallery.repository;

import com.dw.artgallery.model.RealDrawing;

import com.dw.artgallery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface RealDrawingRepository extends JpaRepository<RealDrawing, Long> {
    List<RealDrawing> findByUser(User user);
    List<RealDrawing> findByUserAndIsTemporary(User user, Boolean isTemporary);
    Optional<RealDrawing> findByIdAndUser(Long id, User user);
    Optional<RealDrawing> findByIdAndUserAndIsTemporary(Long id, User user, Boolean isTemporary);


}
