package com.dw.artgallery.service;




import com.dw.artgallery.DTO.CommunityAddDTO;
import com.dw.artgallery.DTO.CommunityDTO;
import com.dw.artgallery.DTO.CommunityDetailDTO;

import com.dw.artgallery.model.*;
import com.dw.artgallery.repository.*;
import com.dw.artgallery.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommunityService {
    @Autowired
    CommunityRepository communityRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommunityLikeRepository communityLikeRepository;
    @Autowired
    DrawingRepository drawingRepository;
    @Autowired
    UploadIMGRepository uploadIMGRepository;


    public List<CommunityDTO> getAllCommunity() {
        return communityRepository.findAllNotDeleted().stream()
                .map(c -> c.toDto(communityLikeRepository))
                .toList();
    }


    public List<CommunityDTO> getDescCommunity() {
        return communityRepository.findRecentCommunities()
                .stream()
                .map(c -> c.toDto(communityLikeRepository))
                .collect(Collectors.toList());
    }


    public List<CommunityDTO> getAscCommunity() {
        return communityRepository.findOldestCommunities()
                .stream()
                .map(c -> c.toDto(communityLikeRepository))
                .collect(Collectors.toList());
    }


    public List<CommunityDTO> getPopularCommunities() {
        List<Community> communities = communityRepository.findAllWithLikes();

        return communities.stream()
                .sorted((a, b) -> Long.compare(
                        communityLikeRepository.countByCommunity(b),
                        communityLikeRepository.countByCommunity(a)
                ))
                .map(c -> c.toDto(communityLikeRepository))
                .collect(Collectors.toList());
    }

    public CommunityDTO getIdCommunity(Long id) {
        return communityRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않거나 삭제된 커뮤니티입니다."))
                .toDto(communityLikeRepository);
    }


    public CommunityDetailDTO getIdCommunities(Long id) {
        Community community = communityRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않거나 삭제된 커뮤니티입니다."));
        return community.ToDto(communityLikeRepository);
    }


    public List<CommunityDTO> getUserIDCommunity(String userId) {
        List<Community> communities = communityRepository.findByUserIdNotDeleted(userId);
        if (communities.isEmpty()) {
            throw new ResourceNotFoundException("해당 유저의 커뮤니티가 없습니다.");
        }
        return communities.stream()
                .map(c -> c.toDto(communityLikeRepository))
                .toList();
    }


    @Transactional
    public boolean toggleLike(Long communityId, User user) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("해당 커뮤니티 글이 없습니다."));

        Optional<CommunityLike> likeOptional = communityLikeRepository.findByUserAndCommunity(user, community);

        if (likeOptional.isPresent()) {
            communityLikeRepository.delete(likeOptional.get());
            return false; // 좋아요 취소
        } else {
            try {
                CommunityLike like = CommunityLike.builder()
                        .user(user)
                        .community(community)
                        .build();
                communityLikeRepository.save(like);
                return true; // 좋아요 추가
            } catch (DataIntegrityViolationException e) {
                throw new RuntimeException("이미 좋아요를 눌렀습니다.");
            }
        }
    }

    public CommunityDTO addCommunity(CommunityAddDTO dto, User user) {
        Community community = new Community();
        community.setText(dto.getText());
        community.setUploadDate(LocalDateTime.now());
        community.setModifyDate(LocalDateTime.now());
        community.setUser(user); // 작성자 연관관계 설정

        // 이미지 리스트 생성 및 UploadIMG 엔티티 저장
        List<UploadIMG> imgs = dto.getImg().stream()
                .map(url -> {
                    UploadIMG img = new UploadIMG();
                    img.setImgUrl(url);
                    return uploadIMGRepository.save(img); // UploadIMG 저장
                }).collect(Collectors.toList());

        community.setCommunityIMGS(imgs);

        Community saved = communityRepository.save(community);
        return saved.toDto(communityLikeRepository);
    }

    public CommunityDTO updateCommunity(Long id, CommunityAddDTO dto, User user) {
        Community community = communityRepository.findById(id)
                .orElse(null);

        if (community == null || !community.getUser().getUserId().equals(user.getUserId())) {
            return null; // 수정할 게시글이 없거나 수정 권한이 없는 경우
        }

        community.setText(dto.getText());
        community.setModifyDate(LocalDateTime.now());

        if (dto.getImg() != null && !dto.getImg().isEmpty()) {
            List<UploadIMG> newImgs = dto.getImg().stream()
                    .map(url -> {
                        UploadIMG img = new UploadIMG();
                        img.setImgUrl(url);
                        return uploadIMGRepository.save(img);
                    }).collect(Collectors.toList());
            community.getCommunityIMGS().addAll(newImgs);
        }

        Community updated = communityRepository.save(community);
        return updated.toDto(communityLikeRepository);
    }







    public String deleteCommunity(Long id, User user) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 커뮤니티 글이 존재하지 않습니다."));

        if (!community.getUser().getUserId().equals(user.getUserId())&& !user.isAdmin()) {
            throw new SecurityException("본인 글만 삭제할 수 있습니다.");
        }

        community.setIsDeleted(true);
        communityRepository.save(community);
        return "커뮤니티 글이 삭제 처리되었습니다.";
    }
}



