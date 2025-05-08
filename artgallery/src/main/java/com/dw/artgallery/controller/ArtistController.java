package com.dw.artgallery.controller;


import com.dw.artgallery.DTO.ArtDTO;
import com.dw.artgallery.DTO.ArtistDTO;
import com.dw.artgallery.DTO.BiographyDTO;
import com.dw.artgallery.DTO.UserDTO;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.UserRepository;
import com.dw.artgallery.service.ArtService;
import com.dw.artgallery.service.ArtistService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/artist")
public class ArtistController {
    private final ArtistService artistService;
    private final ArtService artService;

    @GetMapping
    public ResponseEntity<List<ArtistDTO>> getAllArtist(){
        return new ResponseEntity<>(artistService.getAllArtist(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
        public ResponseEntity<ArtistDTO> getArtistById(@PathVariable Long id){
        return new ResponseEntity<>(artistService.getArtistById(id),HttpStatus.OK);
    }

    @GetMapping("/user-id/{userId}")
    public ResponseEntity<ArtistDTO> getArtistByUserId(@PathVariable String userId) {
        return new ResponseEntity<>(artistService.getArtistByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ArtDTO>> getArtByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(artService.getArtByUserId(userId));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<ArtistDTO>> getArtistByName(@PathVariable String name){
        return new ResponseEntity<>(artistService.getArtistByName(name),HttpStatus.OK);
    }


    @Value("${file.upload-dir}")
    private String uploadDir;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveArtist(
            @RequestPart("name") String name,
            @RequestPart("description") String description,
            @RequestPart("userId") String userId,
            @RequestPart("biographyList") String biographyListJson,
            @RequestPart(value = "profile_img", required = false) MultipartFile profileImg
    ) throws JsonProcessingException, IOException {

        if (profileImg == null || profileImg.isEmpty()) {
            System.out.println("❌ 프로필 이미지가 비어 있습니다.");
            return ResponseEntity.badRequest().body("프로필 이미지를 업로드해주세요.");
        }

        // ✅ uploads/Artist 경로 조합
        Path artistUploadPath = Paths.get( uploadDir, "Artist")
                .toAbsolutePath()
                .normalize();

        System.out.println("📂 아티스트 업로드 디렉토리: " + artistUploadPath);

        if (!Files.exists(artistUploadPath)) {
            Files.createDirectories(artistUploadPath);
            System.out.println("✅ Artist 디렉토리 생성 완료");
        }

        // 확장자 추출 및 새 파일명 생성
        String originalFilename = profileImg.getOriginalFilename();
        String ext = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFileName = UUID.randomUUID().toString() + ext;
        Path targetPath = artistUploadPath.resolve(newFileName);

        // 파일 저장
        Files.copy(profileImg.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("✅ 프로필 이미지 저장 완료 → " + targetPath);

        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        List<BiographyDTO> biographyList = objectMapper.readValue(
                biographyListJson,
                new TypeReference<>() {}
        );

        // DTO 구성
        ArtistDTO artistDTO = new ArtistDTO();
        artistDTO.setName(name);
        artistDTO.setDescription(description);
        artistDTO.setUserId(userId);
        artistDTO.setProfile_img("/uploads/Artist/" + newFileName); // ✅ 정적 리소스 URL로 저장
        artistDTO.setBiographyList(biographyList);

        // 저장 및 응답
        return new ResponseEntity<>(artistService.saveArtist(artistDTO), HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public ResponseEntity<String> deleteArtist(@PathVariable Long id) {
        return new ResponseEntity<>(artistService.deleteArtist(id), HttpStatus.OK);
    }

}