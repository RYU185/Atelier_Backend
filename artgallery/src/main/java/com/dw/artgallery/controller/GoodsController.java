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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String uploadDir = "./";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        List<String> imageUrls = files.stream().map(file -> {
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            String newFileName = UUID.randomUUID().toString() + ext;
            File savedFile = new File(uploadDir, newFileName);

            try {
                file.transferTo(savedFile);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("파일 업로드 실패");
            }

            return  newFileName;
        }).toList();

        GoodsDTO newGoods = goodsService.addGoodsByImage(dto, imageUrls);
        return new ResponseEntity<>(newGoods, HttpStatus.CREATED);
    }




    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<GoodsDTO> updateGoods(@PathVariable Long id, @RequestBody GoodsDTO goodsDTO){
        return new ResponseEntity<>(goodsService.updateGoods(id, goodsDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
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

