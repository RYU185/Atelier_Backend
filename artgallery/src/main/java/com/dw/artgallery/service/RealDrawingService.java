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

    @Transactional
    public RealDrawingResponseDTO saveDrawing(RealDrawingRequestDTO dto, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        RealDrawing drawing;

        if (dto.getId() != null) {
            System.out.println("수정 모드: 기존 ID = " + dto.getId());

            drawing = realDrawingRepository.findById(dto.getId())
                    .filter(d -> d.getUser().getUserId().equals(user.getUserId()))
                    .orElseThrow(() -> new NoSuchElementException("해당 드로잉을 찾을 수 없거나 권한이 없습니다."));

            // ✅ 기존 드로잉 수정
            drawing.setImageData(dto.getImageData());
            drawing.setIsTemporary(dto.getIsTemporary());
            drawing.setTitle(dto.getTitle());

        } else {
            System.out.println("새로 생성 모드");

            // ✅ 새로운 드로잉 생성
            drawing = new RealDrawing();
            drawing.setImageData(dto.getImageData());
            drawing.setIsTemporary(dto.getIsTemporary());
            drawing.setTitle(dto.getTitle());
            drawing.setUser(user);
        }

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



    // 4. 특정 임시 드로잉 ID로 불러오기 (유저 체크 포함)
    @Transactional
    public RealDrawingResponseDTO getTemporaryDrawingById(Long id, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        RealDrawing drawing = realDrawingRepository.findByIdAndUserAndIsTemporary(id, user, true)
                .orElseThrow(() -> new NoSuchElementException("임시 드로잉을 찾을 수 없습니다."));

        return convertToDTO(drawing);
    }

    public void deleteTemporaryDrawing(Long id, String userId) {
        RealDrawing drawing = realDrawingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drawing not found"));

        if (!drawing.getUser().getUserId().equals(userId)) {
            throw new SecurityException("Not your drawing!");
        }

        realDrawingRepository.deleteById(id);
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