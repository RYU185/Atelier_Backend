package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.ArtistGalleryAddDTO;
import com.dw.artgallery.DTO.ArtistGalleryDTO;
import com.dw.artgallery.DTO.ArtistGalleryDetailDTO;
import com.dw.artgallery.DTO.DeadlineDTO;
import com.dw.artgallery.model.ArtistGallery;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.UserRepository;
import com.dw.artgallery.service.ArtistGalleryService;
import com.dw.artgallery.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/artistgallery")
public class ArtistGalleryController {

    private final ArtistGalleryService artistGalleryService;
    private final UserRepository userRepository;


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

    // ArtistGallery 추가 (관리자만)
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArtistGalleryDetailDTO> createGallery(
            @RequestPart("dto") ArtistGalleryAddDTO dto,
            @RequestPart("poster") MultipartFile posterFile
    ) {
       return new ResponseEntity<>(
               artistGalleryService.createGallery(dto,posterFile),
               HttpStatus.OK
       );
    }

    // 마감일 수정 (관리자만)
    @PutMapping("/deadline/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateDeadline(
            @PathVariable Long id,
            @RequestBody DeadlineDTO dto
    ) {
        String result = artistGalleryService.updateDeadline(id, dto);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/poster-match")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Long>> getArtistIdsByPoster(@RequestParam String filename) {
        return ResponseEntity.ok(artistGalleryService.getArtistIdsByPoster(filename));
    }

}


