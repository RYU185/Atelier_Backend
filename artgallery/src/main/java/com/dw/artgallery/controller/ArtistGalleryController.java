package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.ArtistGalleryAddDTO;
import com.dw.artgallery.DTO.ArtistGalleryDTO;
import com.dw.artgallery.DTO.ArtistGalleryDetailDTO;
import com.dw.artgallery.model.ArtistGallery;
import com.dw.artgallery.model.User;
import com.dw.artgallery.service.ArtistGalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artistgallery")
public class ArtistGalleryController {
    @Autowired
    ArtistGalleryService artistGalleryService;

    // ArtistGallery 전체 조회
    @GetMapping
    public ResponseEntity<List<ArtistGalleryDTO>> getAllArtistGallery (){
        return new ResponseEntity<>(artistGalleryService.getAllArtistGallery(), HttpStatus.OK);
    }

    // ArtistGallery id로 디테일 조회
    @GetMapping("/id/{id}")
    public ResponseEntity<ArtistGalleryDetailDTO> getIdArtistGallery (@PathVariable Long id){
        return new ResponseEntity<>(artistGalleryService.getIdArtistGallery(id), HttpStatus.OK);
    }

    // ArtistGallery 제목으로 리스트 조회
    @GetMapping("/title/{title}")
    public ResponseEntity<List<ArtistGalleryDTO>> getTitleArtistGallery (@PathVariable String title) {
        return new ResponseEntity<>(artistGalleryService.getTitleArtistGallery(title),HttpStatus.OK);
    }

    // ArtistGallery 현재 전시 조회
    @GetMapping("/now")
    public ResponseEntity<List<ArtistGalleryDTO>> getNowArtistGallery () {
        return new ResponseEntity<>(artistGalleryService.getNowArtistGallery(), HttpStatus.OK);
    }

    // ArtistGallery 과거 전시 조회
    @GetMapping("/past")
    public ResponseEntity<List<ArtistGalleryDTO>> getPastArtistGallery () {
        return new ResponseEntity<>(artistGalleryService.getPastArtistGallery(), HttpStatus.OK);
    }


    // ArtistGallery 예정 전시 조회
    @GetMapping("/expected")
    public ResponseEntity<List<ArtistGalleryDTO>> getExpectedArtistGallery () {
        return new ResponseEntity<>(artistGalleryService.getExpectedArtistGallery(), HttpStatus.OK);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArtistGalleryDetailDTO> createGallery(
            @RequestBody ArtistGalleryAddDTO dto,
            @AuthenticationPrincipal User user
    ) {
        System.out.println("🎨 등록 요청 관리자 ID: " + user.getUserId());

        ArtistGallery saved = artistGalleryService.createGallery(dto);

        // ✅ Entity → DTO 변환해서 응답
        return ResponseEntity.ok(saved.TODTO());
    }
}


