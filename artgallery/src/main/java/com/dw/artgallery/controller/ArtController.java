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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/art")
public class ArtController {

    @Autowired
    private ArtService artService;



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

    @GetMapping("/art-list")
    public ResponseEntity<List<ArtDTO>> getArtByMultipleArtistIds(@RequestParam List<Long> artistIds) {
        return ResponseEntity.ok(artService.getArtsByArtistIds(artistIds));
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
    // 작품 등록
    @Value("${file.upload-dir}")
    private String uploadDir;


    @PostMapping("/add")
    public ResponseEntity<ArtDTO> createArt(@ModelAttribute ArtCreateDTO dto) {
        MultipartFile file = dto.getImage();



        if (file == null || file.isEmpty()) {
            System.out.println("❌ 파일이 비어있습니다. 업로드 실패");
            return ResponseEntity.badRequest().build();
        }

        try {
            Path artUploadPath = Paths.get(uploadDir, "Art")
                    .toAbsolutePath()
                    .normalize();


            if (!Files.exists(artUploadPath)) {
                Files.createDirectories(artUploadPath);

            }

            String originalFileName = file.getOriginalFilename();
            String fileName = originalFileName;
            int counter = 1;

            while (Files.exists(artUploadPath.resolve(fileName))) {
                int dotIndex = originalFileName.lastIndexOf(".");
                String name = dotIndex == -1 ? originalFileName : originalFileName.substring(0, dotIndex);
                String extension = dotIndex == -1 ? "" : originalFileName.substring(dotIndex);
                fileName = name + "_" + counter + extension;
                counter++;
            }

            Path targetPath = artUploadPath.resolve(fileName).normalize();


            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);


            dto.setImgUrl("/uploads/Art/" + fileName);
            ArtDTO created = artService.createArt(dto);


            return new ResponseEntity<>(created, HttpStatus.CREATED);

        } catch (IOException e) {
            System.out.println("🔥 파일 저장 중 예외 발생");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
