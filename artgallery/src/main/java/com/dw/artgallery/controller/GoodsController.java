package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.GoodsCreateDTO;
import com.dw.artgallery.DTO.GoodsDTO;
import com.dw.artgallery.DTO.GoodsTotalDTO;
import com.dw.artgallery.enums.SortOrder;
import com.dw.artgallery.repository.GoodsRepository;
import com.dw.artgallery.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
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
@RequestMapping("/api/goods")
public class GoodsController {
    private final GoodsService goodsService;
    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping
    public ResponseEntity<List<GoodsDTO>> getAllGoods(){
        return new ResponseEntity<>(goodsService.getAllGoods(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<GoodsDTO> getGoodsById(@PathVariable Long id){
        return new ResponseEntity<>(goodsService.getGoodsById(id), HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<GoodsDTO>> getGoodsByName(@PathVariable String name){
        return new ResponseEntity<>(goodsService.getGoodsByName(name), HttpStatus.OK);
    }

    @GetMapping ("/price/{sortOrder}")
    public ResponseEntity<List<GoodsDTO>> getGoodsSortByPrice(@PathVariable SortOrder sortOrder){
        return new ResponseEntity<>(goodsService.getGoodsSortByPrice(sortOrder),HttpStatus.OK);
    }

    @GetMapping("/stock/{id}")
    public ResponseEntity<Integer> getGoodsStockById(@PathVariable Long id) {
        return new ResponseEntity<>(goodsService.getGoodsStockById(id), HttpStatus.OK);
    }


    @PostMapping("/add")
    public ResponseEntity<GoodsDTO> addGoods(@ModelAttribute GoodsCreateDTO dto) {
        List<MultipartFile> files = dto.getImages();

        if (files == null || files.isEmpty()) {
            System.out.println("❗ 업로드된 파일이 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            // ✅ uploads/Goods 경로로 고정
            Path goodsUploadPath = Paths.get(uploadDir, "Goods")
                    .toAbsolutePath()
                    .normalize();

            System.out.println("📂 굿즈 업로드 디렉토리: " + goodsUploadPath);

            if (!Files.exists(goodsUploadPath)) {
                Files.createDirectories(goodsUploadPath);
                System.out.println("✅ 디렉토리 생성 완료: " + goodsUploadPath);
            }

            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();
                String ext = "";

                if (originalFilename != null && originalFilename.contains(".")) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                String fileName = UUID.randomUUID().toString() + ext;
                Path targetPath = goodsUploadPath.resolve(fileName).normalize();

                System.out.println("📥 굿즈 파일 복사 시작: " + targetPath);

                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("✅ 굿즈 파일 복사 완료: " + targetPath);

                // 웹에서 접근 가능한 URL 구성
                imageUrls.add("/uploads/Goods/" + fileName);
            }

            GoodsDTO newGoods = goodsService.addGoodsByImage(dto, imageUrls);
            System.out.println("🎉 굿즈 등록 완료 → 이미지 수: " + imageUrls.size());

            return new ResponseEntity<>(newGoods, HttpStatus.CREATED);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("🔥 파일 업로드 중 예외 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }




    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<GoodsDTO> updateGoods(@PathVariable Long id, @RequestBody GoodsDTO goodsDTO){
        return new ResponseEntity<>(goodsService.updateGoods(id, goodsDTO), HttpStatus.CREATED);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGoods(@PathVariable Long id) {
        return new ResponseEntity<>(goodsService.deleteGoods(id), HttpStatus.OK);
    }

    //  관리자 전용 굿즈 조회 (누적 판매량 포함)
//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<GoodsTotalDTO>> getAllGoodsForAdmin() {
        return new ResponseEntity<>(goodsService.getAllGoodsForAdmin(), HttpStatus.OK);
    }
}

