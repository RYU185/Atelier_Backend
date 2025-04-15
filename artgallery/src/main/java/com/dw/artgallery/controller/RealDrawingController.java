package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.RealDrawingRequestDTO;
import com.dw.artgallery.model.RealDrawing;
import com.dw.artgallery.model.User;
import com.dw.artgallery.service.RealDrawingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
