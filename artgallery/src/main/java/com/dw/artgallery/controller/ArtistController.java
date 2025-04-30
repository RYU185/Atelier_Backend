package com.dw.artgallery.controller;


import com.dw.artgallery.DTO.ArtistDTO;
import com.dw.artgallery.DTO.BiographyDTO;
import com.dw.artgallery.DTO.UserDTO;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.UserRepository;
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

    @GetMapping
    public ResponseEntity<List<ArtistDTO>> getAllArtist(){
        return new ResponseEntity<>(artistService.getAllArtist(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
        public ResponseEntity<ArtistDTO> getArtistById(@PathVariable Long id){
        return new ResponseEntity<>(artistService.getArtistById(id),HttpStatus.OK);
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
            @RequestPart("profile_img") MultipartFile profileImg
    ) throws JsonProcessingException, IOException {

        // 확장자 추출
        String originalFilename = profileImg.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // UUID 기반 새 파일명 생성
        String newFileName = UUID.randomUUID().toString() + ext;
        Path savePath = Paths.get(uploadDir, newFileName);

        // 디렉토리 없으면 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 저장
        Files.copy(profileImg.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List<BiographyDTO> biographyList = objectMapper.readValue(
                biographyListJson,
                new TypeReference<>() {}
        );

        // DTO 생성 및 저장
        ArtistDTO artistDTO = new ArtistDTO();
        artistDTO.setName(name);
        artistDTO.setDescription(description);
        artistDTO.setUserId(userId);
        artistDTO.setProfile_img(newFileName); // 저장된 새 이름
        artistDTO.setBiographyList(biographyList);

        return new ResponseEntity<>(artistService.saveArtist(artistDTO), HttpStatus.CREATED);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public ResponseEntity<String> deleteArtist(@PathVariable Long id) {
        return new ResponseEntity<>(artistService.deleteArtist(id), HttpStatus.OK);
    }

}