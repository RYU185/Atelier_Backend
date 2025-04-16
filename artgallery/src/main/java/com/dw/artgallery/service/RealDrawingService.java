package com.dw.artgallery.service;

import com.dw.artgallery.DTO.RealDrawingRequestDTO;
import com.dw.artgallery.DTO.RealDrawingResponseDTO;
import com.dw.artgallery.model.RealDrawing;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.RealDrawingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RealDrawingService {
    @Autowired
    RealDrawingRepository realDrawingRepository;

    public RealDrawing saveDrawing(RealDrawingRequestDTO dto, User user) {
        RealDrawing drawing = new RealDrawing();
        drawing.setImageData(dto.getImageData());
        drawing.setIsTemporary(dto.getIsTemporary());
        drawing.setTitle(dto.getTitle());
        drawing.setUser(user);
        return realDrawingRepository.save(drawing);
    }

    public List<RealDrawingResponseDTO> getDrawingsByUser(User user) {
        return realDrawingRepository.findByUser(user).stream()
                .map(d -> new RealDrawingResponseDTO(
                        d.getId(),
                        d.getImageData(),
                        d.getIsTemporary(),
                        d.getTitle(),

                        d.getUpdatedAt(),
                        user.getUsername() // 또는 getId(), email 등
                ))
                .collect(Collectors.toList());
    }

    public RealDrawing getDrawingById(Long id) {
        return realDrawingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 드로잉이 존재하지 않습니다."));
    }


    public RealDrawing updateDrawing(Long id, RealDrawingRequestDTO dto, User user) {
        RealDrawing drawing = realDrawingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("드로잉이 존재하지 않습니다."));


        if (!drawing.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("본인의 드로잉만 수정할 수 있습니다.");
        }

        drawing.setImageData(dto.getImageData());
        drawing.setIsTemporary(dto.getIsTemporary());
        drawing.setTitle(dto.getTitle());


        return realDrawingRepository.save(drawing);
    }
}
