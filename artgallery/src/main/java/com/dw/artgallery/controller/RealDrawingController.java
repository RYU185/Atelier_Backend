package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.RealDrawingRequestDTO;
import com.dw.artgallery.DTO.RealDrawingResponseDTO;
import com.dw.artgallery.model.RealDrawing;
import com.dw.artgallery.model.User;
import com.dw.artgallery.service.RealDrawingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/RealDrawing")
public class RealDrawingController {
    @Autowired
    RealDrawingService realDrawingService;

    @PostMapping("/save")
    public RealDrawing save(@RequestBody RealDrawingRequestDTO dto,
                            @AuthenticationPrincipal User user) {
        return realDrawingService.saveDrawing(dto, user);
    }

    @GetMapping("/my")
    public List<RealDrawingResponseDTO> getMyDrawings(@AuthenticationPrincipal User user) {
        return realDrawingService.getDrawingsByUser(user);
    }

    @GetMapping("/{id}")
    public RealDrawing getDrawing(@PathVariable Long id) {
        return realDrawingService.getDrawingById(id);
    }


    @PutMapping("/{id}")
    public RealDrawing updateDrawing(@PathVariable Long id,
                                     @RequestBody RealDrawingRequestDTO dto,
                                     @AuthenticationPrincipal User user) {
        return realDrawingService.updateDrawing(id, dto, user);
    }
}


