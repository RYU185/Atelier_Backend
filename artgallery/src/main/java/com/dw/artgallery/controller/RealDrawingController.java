package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.RealDrawingRequestDTO;
import com.dw.artgallery.DTO.RealDrawingResponseDTO;
import com.dw.artgallery.model.RealDrawing;
import com.dw.artgallery.model.User;
import com.dw.artgallery.service.RealDrawingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<RealDrawing> save(@RequestBody RealDrawingRequestDTO dto,
                                            @AuthenticationPrincipal User user) {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 인증되지 않은 경우 401 응답
        }
        RealDrawing savedDrawing = realDrawingService.saveDrawing(dto, user);
        return new ResponseEntity<>(savedDrawing, HttpStatus.CREATED); // 성공적으로 저장된 경우 201 응답
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


