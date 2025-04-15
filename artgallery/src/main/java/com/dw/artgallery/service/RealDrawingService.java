package com.dw.artgallery.service;

import com.dw.artgallery.DTO.RealDrawingRequestDTO;
import com.dw.artgallery.model.RealDrawing;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.RealDrawingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RealDrawingService {
    @Autowired
    RealDrawingRepository realDrawingRepository;

    public RealDrawing saveDrawing(RealDrawingRequestDTO dto, User user) {
        RealDrawing drawing = new RealDrawing();
        drawing.setImageData(dto.getImageData());
        drawing.setIsTemporary(dto.getIsTemporary());
        drawing.setTitle(dto.getTitle());
        drawing.setDescription(dto.getDescription());
        drawing.setUser(user);

        return realDrawingRepository.save(drawing);
    }
}
