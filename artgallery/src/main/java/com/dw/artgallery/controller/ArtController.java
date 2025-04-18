package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.ArtCreateDTO;
import com.dw.artgallery.DTO.ArtDTO;
import com.dw.artgallery.DTO.ArtUpdateDTO;
import com.dw.artgallery.service.ArtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/art")
public class ArtController {

    @Autowired
    private ArtService artService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 모든 작품 조회
    @GetMapping
    public ResponseEntity<List<ArtDTO>> getAllArt() {
        return ResponseEntity.ok(artService.getAllArt());
    }

    // ID로 작품 조회
    @GetMapping("/id/{id}")
    public ResponseEntity<ArtDTO> getIdArt(@PathVariable Long id) {
        return ResponseEntity.ok(artService.findByIdArtId(id));
    }

    // 특정 작가의 참여 작품 조회
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<ArtDTO>> getArtByArtistId(@PathVariable Long artistId) {
        return ResponseEntity.ok(artService.getArtByArtistId(artistId));
    }


    // 작품 수정
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArtDTO> updateArt(@PathVariable Long id, @RequestBody ArtUpdateDTO artUpdateDTO) {
        return ResponseEntity.ok(artService.updateArt(id, artUpdateDTO));
    }

    // 작품 삭제
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteArt(@PathVariable Long id) {
        artService.deleteArtById(id);
        return ResponseEntity.ok("작품이 성공적으로 삭제되었습니다.");
    }

    // 작품 등록
    @PostMapping("/add")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArtDTO> createArt(@ModelAttribute ArtCreateDTO dto) {
        MultipartFile file = dto.getImage();
        System.out.println("📁 실제 업로드 경로: " + uploadDir);
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

        // 디렉토리 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String ext = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFileName = UUID.randomUUID().toString() + ext;
        File savedFile = new File(uploadDir, newFileName);

        try {
            file.transferTo(savedFile);
            dto.setImgUrl("/uploads/" + newFileName); // 💡 웹에서 접근 가능한 경로로 설정
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

        ArtDTO created = artService.createArt(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
