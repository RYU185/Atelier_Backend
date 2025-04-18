package com.dw.artgallery.controller;


import com.dw.artgallery.DTO.RealDrawingRequestDTO;
import com.dw.artgallery.DTO.RealDrawingResponseDTO;
import com.dw.artgallery.service.RealDrawingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/realdrawing")
public class RealDrawingController {
    @Autowired
    RealDrawingService realDrawingService;


    @PostMapping("/save")
    public RealDrawingResponseDTO saveDrawing(@RequestBody RealDrawingRequestDTO dto,
                                              Authentication authentication
    ) {

        String userId = authentication.getName();
        return realDrawingService.saveDrawing(dto,userId);
    }


    @GetMapping("/my")
    public List<RealDrawingResponseDTO> getUserDrawings(Authentication authentication) {
        String userId = authentication.getName();
        return realDrawingService.getUserDrawings(userId);
    }



    @GetMapping("/temporary/{id}")
    public RealDrawingResponseDTO getTemporaryDrawingById(@PathVariable Long id,
                                                          Authentication authentication) {
        String userId = authentication.getName();
        return realDrawingService.getTemporaryDrawingById(id, userId);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTemporaryDrawing(@PathVariable Long id, Authentication authentication) {
        String userId = authentication.getName();
        realDrawingService.deleteTemporaryDrawing(id, userId);
        return ResponseEntity.ok("Deleted successfully");
    }

}


