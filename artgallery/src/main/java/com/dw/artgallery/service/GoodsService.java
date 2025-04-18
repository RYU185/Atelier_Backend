package com.dw.artgallery.service;

import com.dw.artgallery.DTO.GoodsCreateDTO;
import com.dw.artgallery.DTO.GoodsDTO;
import com.dw.artgallery.DTO.GoodsTotalDTO;
import com.dw.artgallery.enums.SortOrder;
import com.dw.artgallery.model.Goods;
import com.dw.artgallery.repository.GoodsCartRepository;
import com.dw.artgallery.repository.GoodsRepository;
import com.dw.artgallery.exception.ResourceNotFoundException;
import com.dw.artgallery.repository.PurchaseGoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    GoodsRepository goodsRepository;
    @Autowired
    UserService userService;
    @Autowired
    PurchaseGoodsRepository purchaseGoodsRepository;

    public List<GoodsDTO> getAllGoods() {
        List<Goods> goodsList = goodsRepository.findAll();

        return goodsList.stream()
                .map(GoodsDTO::fromEntity)
                .toList();
    }

    public GoodsDTO getGoodsById(Long id){
        return goodsRepository.findById(id)
                .map(GoodsDTO::fromEntity)
                .orElseThrow(()-> new ResourceNotFoundException("해당 굿즈가 존재하지 않습니다"));
    }

    public List<GoodsDTO> getGoodsByName(String name){
        return goodsRepository.findByNameLike("%"+name+"%").stream().map(GoodsDTO::fromEntity).toList();
    }

    public List<GoodsDTO>getGoodsSortByPrice(SortOrder sortOrder) {
        List<Goods> result;

        if (sortOrder == SortOrder.ASC) {
            result = goodsRepository.findAllByOrderByPriceAsc();
        } else {
            result = goodsRepository.findAllByOrderByPriceDesc();
        }
        return result.stream().map(GoodsDTO::fromEntity).toList();
    }

    public int getGoodsStockById(Long id){
        GoodsDTO goods = goodsRepository.findById(id)
                .map(GoodsDTO::fromEntity)
                .orElseThrow(()-> new ResourceNotFoundException("해당 굿즈가 존재하지 않습니다"));

        return goods.getStock();
    }

    public GoodsDTO addGoodsByImage(GoodsCreateDTO dto, List<String> imageUrls) {
        Goods goods = new Goods();
        goods.setName(dto.getName());
        goods.setDescription(dto.getDescription());
        goods.setPrice(dto.getPrice());
        goods.setStock(dto.getStock());
        goods.setImgUrlList(imageUrls); // 여러 장 URL 저장

        Goods saved = goodsRepository.save(goods);
        return GoodsDTO.fromEntity(saved);
    }


    public GoodsDTO updateGoods(Long id, GoodsDTO goodsDTO){

        Goods goods = goodsRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("찾으려는 굿즈 상품이 없습니다"));

        goods.setName(goodsDTO.getName());
        goods.setImgUrlList(goodsDTO.getImgUrlList());
        goods.setDescription(goodsDTO.getDescription());
        goods.setPrice(goodsDTO.getPrice());
        goods.setStock(goodsDTO.getStock());

        Goods updatedGoods = goodsRepository.save(goods);
        return GoodsDTO.fromEntity(updatedGoods);
    }

    public String deleteGoods(Long id){
        Goods goods = goodsRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("해당 상품이 존재하지 않습니다"));
        goodsRepository.delete(goods);
        return "해당 상품을 삭제하였습니다.";
    }
    // ✅ 관리자 전용 굿즈 전체 조회 (누적 판매량 포함)
    public List<GoodsTotalDTO> getAllGoodsForAdmin() {
        return goodsRepository.findAll().stream()
                .map(goods -> new GoodsTotalDTO(
                        goods.getId(),
                        goods.getName(),
                        goods.getImgUrlList(),
                        goods.getStock(),
                        purchaseGoodsRepository.getTotalSalesByGoodsId(goods.getId()) // 관리자만 조회 가능
                ))
                .collect(Collectors.toList());
    }



}
