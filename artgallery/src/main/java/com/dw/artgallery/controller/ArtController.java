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

        System.out.println(" [UPLOAD START] 업로드 요청 수신됨");
        System.out.println(" 받은 파일 이름: " + (file != null ? file.getOriginalFilename() : "null"));
        System.out.println(" 파일 크기: " + (file != null ? file.getSize() + " bytes" : "파일 없음"));

        if (file == null || file.isEmpty()) {
            System.out.println("파일이 비어있습니다. 업로드 실패");
            return ResponseEntity.badRequest().build();
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            System.out.println(" 업로드 디렉토리 설정: " + uploadDir);
            System.out.println(" 절대 경로 변환: " + uploadPath);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("✅ 디렉토리 생성 완료: " + uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            String newFileName = UUID.randomUUID() + ext;
            Path targetPath = uploadPath.resolve(newFileName);

            System.out.println(" 최종 저장 파일명: " + newFileName);
            System.out.println(" 복사 경로: " + targetPath);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("파일 저장 완료 → 존재 여부 확인: " + Files.exists(targetPath));

            dto.setImgUrl("/uploads/" + newFileName);
            ArtDTO created = artService.createArt(dto);

            System.out.println(" 아트 정보 저장 완료 → ID: " + created.getId());
            return new ResponseEntity<>(created, HttpStatus.CREATED);

        } catch (IOException e) {
            System.out.println(" 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
