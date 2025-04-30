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
    public int toggleLike(Long communityId, User user) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("해당 커뮤니티 글이 없습니다."));

        Optional<CommunityLike> likeOptional = communityLikeRepository.findByUserAndCommunity(user, community);

        if (likeOptional.isPresent()) {
            communityLikeRepository.delete(likeOptional.get());
        } else {
            try {
                CommunityLike like = CommunityLike.builder()
                        .user(user)
                        .community(community)
                        .build();
                communityLikeRepository.save(like);
            } catch (DataIntegrityViolationException e) {
                throw new RuntimeException("이미 좋아요를 눌렀습니다.");
            }
        }
        // 업데이트된 좋아요 개수를 조회하여 반환
        return communityLikeRepository.countByCommunity(community);
    }








    public String deleteCommunity(Long id, User user) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 커뮤니티 글이 존재하지 않습니다."));

        if (!community.getUser().getUserId().equals(user.getUserId()) && !user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new SecurityException("본인 글이거나 관리자만 삭제할 수 있습니다.");
        }

        community.setIsDeleted(true);
        communityRepository.save(community);
        return "커뮤니티 글이 삭제 처리되었습니다.";
    }
}



