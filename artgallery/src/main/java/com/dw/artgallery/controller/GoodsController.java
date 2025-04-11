package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.GoodsDTO;
import com.dw.artgallery.DTO.GoodsTotalDTO;
import com.dw.artgallery.enums.SortOrder;
import com.dw.artgallery.repository.GoodsRepository;
import com.dw.artgallery.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/goods")
public class GoodsController {
    private final GoodsService goodsService;

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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GoodsDTO> addGoods(@RequestBody GoodsDTO goodsDTO){
        return new ResponseEntity<>(goodsService.addGoods(goodsDTO), HttpStatus.CREATED);
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

    //  관리자 전용 굿즈 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<GoodsTotalDTO>> getAllGoodsForAdmin() {
        return new ResponseEntity<>(goodsService.getAllGoodsForAdmin(), HttpStatus.OK);
    }
}

