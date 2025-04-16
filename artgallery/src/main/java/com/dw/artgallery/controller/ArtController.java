    package com.dw.artgallery.controller;

    import com.dw.artgallery.DTO.ArtCreateDTO;
    import com.dw.artgallery.DTO.ArtDTO;
    import com.dw.artgallery.DTO.ArtDetailDTO;
    import com.dw.artgallery.DTO.ArtUpdateDTO;
    import com.dw.artgallery.model.Art;
    import com.dw.artgallery.service.ArtService;
    import jakarta.validation.Valid;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;
    import java.io.File;


    import java.io.IOException;
    import java.util.List;

    @RestController
    @RequestMapping("/api/art")
    public class ArtController {
        @Autowired
        ArtService artService;

        // 모든 작품 조회
        @GetMapping
        public ResponseEntity<List<ArtDTO>> getAllArt() {
            return ResponseEntity.ok(artService.getAllArt());
        }

        // 작품 ID로 조회
        @GetMapping("/id/{id}")
        public ResponseEntity<ArtDTO> getIdArt(@PathVariable Long id) {
            return ResponseEntity.ok(artService.findByIdArtId(id));
        }


        // 작품 수정 (관리자)
        @PutMapping("/update/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ArtDTO> updateArt(@PathVariable Long id, @RequestBody ArtUpdateDTO artUpdateDTO) {
            return ResponseEntity.ok(artService.updateArt(id, artUpdateDTO));
        }

        // 작품 삭제 (관리자)
        @PostMapping("/{id}/delete")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<String> deleteArt(@PathVariable Long id) {
            artService.deleteArtById(id);
            return ResponseEntity.ok("작품이 성공적으로 삭제되었습니다.");
        }

        @PostMapping("/add")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ArtDTO> createArt(@RequestParam("file") MultipartFile file,
                                                @Valid @RequestBody ArtCreateDTO artCreateDTO) {
            String uploadDir = "/path/to/upload/directory";  // 실제 업로드 디렉토리 경로

            // 파일 저장 처리 (예시)
            String imgUrl = saveFile(file, uploadDir);

            // artCreateDTO에 imgUrl 설정
            artCreateDTO.setImgUrl(imgUrl);

            // 작품 등록
            return new ResponseEntity<>(artService.createArt(artCreateDTO), HttpStatus.CREATED);
        }

        private String saveFile(MultipartFile file, String uploadDir) {
            String fileName = file.getOriginalFilename();
            String filePath = uploadDir + "/" + fileName;

            try {
                file.transferTo(new File(filePath));  // 파일 저장
            } catch (IOException e) {
                throw new RuntimeException("파일 저장 실패", e);
            }

            return filePath;  // 파일 경로 반환
        }
    }
