package com.dw.artgallery.controller;


import com.dw.artgallery.DTO.CommunityAddDTO;
import com.dw.artgallery.DTO.CommunityDTO;
import com.dw.artgallery.DTO.CommunityDetailDTO;

import com.dw.artgallery.DTO.CommunityUpdateDTO;
import com.dw.artgallery.exception.ResourceNotFoundException;
import com.dw.artgallery.model.Community;
import com.dw.artgallery.model.CommunityLike;
import com.dw.artgallery.model.UploadIMG;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.CommunityLikeRepository;
import com.dw.artgallery.repository.CommunityRepository;
import com.dw.artgallery.repository.UploadIMGRepository;
import com.dw.artgallery.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    CommunityService communityService;
    @Autowired
    CommunityRepository communityRepository;
    @Autowired
    CommunityLikeRepository communityLikeRepository;
    @Autowired
    UploadIMGRepository uploadIMGRepository;

    // Community 전체 조회
    @GetMapping
    public ResponseEntity<List<CommunityDTO>> getAllCommunity() {
        return new ResponseEntity<>(communityService.getAllCommunity(), HttpStatus.OK);
    }

    // Community 업로드일 기준 최신 순 조회
    @GetMapping("/desc")
    public ResponseEntity<List<CommunityDTO>> getDescCommunity() {
        return new ResponseEntity<>(communityService.getDescCommunity(), HttpStatus.OK);
    }

    // Community 업로드일 기준 오래된 Community 순 조회
    @GetMapping("/asc")
    public ResponseEntity<List<CommunityDTO>> getAscCommunity() {
        return new ResponseEntity<>(communityService.getAscCommunity(), HttpStatus.OK);
    }

    // Community 좋아요 많은 순 조회
    @GetMapping("/popular")
    public ResponseEntity<List<CommunityDTO>> getPopularCommunities() {
        return new ResponseEntity<>(communityService.getPopularCommunities(), HttpStatus.OK);
    }

    // Community id로 조회
    @GetMapping("/id/{id}")
    public ResponseEntity<CommunityDTO> getIdCommunity(@PathVariable Long id) {
        return new ResponseEntity<>(communityService.getIdCommunity(id), HttpStatus.OK);
    }

    // Community id로 디테일 조회
    @GetMapping("/detail/id/{id}")
    public ResponseEntity<CommunityDetailDTO> getIdCommunities(@PathVariable Long id) {
        return new ResponseEntity<>(communityService.getIdCommunities(id), HttpStatus.OK);
    }

    // Community userId로  조회
    @GetMapping("/userid/{userid}")
    public ResponseEntity<List<CommunityDTO>> getUserIDCommunity(@PathVariable String userid) {
        return new ResponseEntity<>(communityService.getUserIDCommunity(userid), HttpStatus.OK);
    }

    // 로그인한 회원의 Community 조회
    @GetMapping("/my")
    public ResponseEntity<List<CommunityDTO>> getMyCommunity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Object principal = authentication.getPrincipal();
        String userId;
        if (principal instanceof com.dw.artgallery.model.User user) {
            userId = user.getUserId();
        } else if (principal instanceof String id) {
            userId = id;
        } else {
            throw new IllegalStateException("알 수 없는 사용자 타입: " + principal.getClass());
        }
        List<CommunityDTO> communities = communityService.getUserIDCommunity(userId);
        return new ResponseEntity<>(communities, HttpStatus.OK);
    }

    // 좋아요 확인
    @GetMapping("/like/check/{communityId}")
    public ResponseEntity<Boolean> checkLikeStatus(@PathVariable Long communityId,
                                                   @AuthenticationPrincipal User user) {
        if (user == null) {
            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }
        Optional<Community> communityOptional = communityRepository.findById(communityId);
        if (communityOptional.isEmpty()) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND); // 해당 커뮤니티가 없으면 false 반환
        }
        Community community = communityOptional.get();
        boolean isLiked = communityLikeRepository.findByUserAndCommunity(user, community).isPresent();
        return new ResponseEntity<>(isLiked, HttpStatus.OK);
    }


    // Community 좋아요
    @PostMapping("/like/{id}")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long id,
                                                          @AuthenticationPrincipal User user) {
        int likeCount = communityService.toggleLike(id, user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "좋아요 상태가 변경되었습니다."); // 일반적인 메시지
        response.put("likeCount", likeCount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/add")
    public ResponseEntity<CommunityDTO> addCommunity(
            @ModelAttribute CommunityAddDTO dto,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @AuthenticationPrincipal User user) {

        try {

            Community community = new Community();
            community.setText(dto.getText());
            community.setUploadDate(LocalDateTime.now());
            community.setModifyDate(LocalDateTime.now());
            community.setUser(user);

            List<UploadIMG> uploadImgs = new ArrayList<>();
            if (files != null && files.length > 0) {
                Path userUploadPath = Paths.get(uploadDir,"Community", user.getUsername());

                if (!Files.exists(userUploadPath)) {
                    Files.createDirectories(userUploadPath);
                }

                for (MultipartFile file : files) {
                    String originalFileName = file.getOriginalFilename();
                    String fileName = originalFileName;
                    int counter = 1;

                    // 중복 파일 이름 처리
                    while (Files.exists(userUploadPath.resolve(fileName))) {
                        int dotIndex = originalFileName.lastIndexOf(".");
                        String name = dotIndex == -1 ? originalFileName : originalFileName.substring(0, dotIndex);
                        String extension = dotIndex == -1 ? "" : originalFileName.substring(dotIndex);
                        fileName = name + "_" + counter + extension;
                        counter++;
                    }

                    // 파일 경로 설정
                    Path filePath = userUploadPath.resolve(fileName).normalize();
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    System.out.println("파일 업로드 완료: " + filePath.toString());
                    
                    // UploadIMG 저장
                    UploadIMG uploadIMG = new UploadIMG();
                    uploadIMG.setImgUrl("/uploads/Community/"+user.getUsername() + "/" + fileName); // URL 저장
                    uploadImgs.add(uploadIMGRepository.save(uploadIMG));
                }
            }

            // 3. 커뮤니티에 이미지 연결
            community.setCommunityIMGS(uploadImgs);

            // 4. 커뮤니티 저장
            Community savedCommunity = communityRepository.save(community);

            // 5. DTO 변환 및 반환
            CommunityDTO communityDTO = savedCommunity.toDto(communityLikeRepository);
            return new ResponseEntity<>(communityDTO, HttpStatus.CREATED);

        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



//    @PutMapping("/update/{id}")
//    public ResponseEntity<CommunityDTO> updateCommunity(@PathVariable Long id,
//                                                        @RequestBody CommunityAddDTO dto,
//                                                        @AuthenticationPrincipal User user) {
//        CommunityDTO updated = communityService.updateCommunity(id, dto, user);
//        if (updated != null) {
//            return new ResponseEntity<>(updated, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 수정할 게시글이 없거나 권한이 없는 경우
//        }
//    }

    // Community id로 논리적 삭제
    @PostMapping("/delete/{id}")
    public ResponseEntity<String> deleteCommunity(@PathVariable Long id,
                                                  @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(communityService.deleteCommunity(id, user), HttpStatus.OK);
    }


}