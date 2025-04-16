package com.dw.artgallery.service;

import com.dw.artgallery.DTO.RealDrawingRequestDTO;
import com.dw.artgallery.DTO.RealDrawingResponseDTO;
import com.dw.artgallery.model.RealDrawing;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.RealDrawingRepository;
import com.dw.artgallery.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RealDrawingService {

    private final RealDrawingRepository realDrawingRepository;
    private final UserRepository userRepository;

    // 1. 드로잉 저장
    @Transactional
    public RealDrawingResponseDTO saveDrawing(RealDrawingRequestDTO dto, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        RealDrawing drawing = new RealDrawing();
        drawing.setImageData(dto.getImageData());
        drawing.setIsTemporary(dto.getIsTemporary());
        drawing.setTitle(dto.getTitle());
        drawing.setUser(user);

        RealDrawing saved = realDrawingRepository.save(drawing);
        return convertToDTO(saved);
    }

    // 2. 로그인한 유저의 전체 드로잉 (임시 포함)
    @Transactional
    public List<RealDrawingResponseDTO> getUserDrawings(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        List<RealDrawing> drawings = realDrawingRepository.findByUser(user);
        return drawings.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // 3. 로그인한 유저의 임시 드로잉만 조회
    @Transactional
    public List<RealDrawingResponseDTO> getTemporaryDrawings(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        List<RealDrawing> tempDrawings = realDrawingRepository.findByUserAndIsTemporary(user, true);
        return tempDrawings.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // 4. 특정 임시 드로잉 ID로 불러오기 (유저 체크 포함)
    @Transactional
    public RealDrawingResponseDTO getTemporaryDrawingById(Long id, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        RealDrawing drawing = realDrawingRepository.findByIdAndUserAndIsTemporary(id, user, true)
                .orElseThrow(() -> new NoSuchElementException("임시 드로잉을 찾을 수 없습니다."));

        return convertToDTO(drawing);
    }

    // 내부 변환 함수
    private RealDrawingResponseDTO convertToDTO(RealDrawing drawing) {
        return new RealDrawingResponseDTO(
                drawing.getId(),
                drawing.getImageData(),
                drawing.getIsTemporary(),
                drawing.getTitle(),
                drawing.getUpdatedAt(),
                drawing.getUser().getUserId()
        );
    }
}