package com.shop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // RestController, GetMapping 등 통합
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import lombok.RequiredArgsConstructor;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // ❌ [삭제] 상품 등록 폼(GET)은 필요 없습니다.
    // 리액트에서 그냥 빈 입력창 컴포넌트를 보여주면 끝입니다.

    // ✅ [수정] 상품 등록 (POST)
    @PostMapping(value = "/admin/item/new")
    public ResponseEntity itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                                  @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

        // 1. 유효성 검사 실패 시 에러 메시지 반환
        if(bindingResult.hasErrors()){
            return new ResponseEntity<String>("상품 정보를 입력해주세요.", HttpStatus.BAD_REQUEST);
        }

        // 2. 첫 번째 이미지 체크
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            return new ResponseEntity<String>("첫번째 상품 이미지는 필수 입력 값 입니다.", HttpStatus.BAD_REQUEST);
        }

        // 3. 저장 로직
        try {
            Long savedItemId = itemService.saveItem(itemFormDto, itemImgFileList);
            return new ResponseEntity<Long>(savedItemId, HttpStatus.OK); // 저장된 ID 반환
        } catch (Exception e){
            return new ResponseEntity<String>("상품 등록 중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ [수정] 상품 상세 조회 (관리자용)
    @GetMapping(value = "/admin/item/{itemId}")
    public ResponseEntity itemDtl(@PathVariable("itemId") Long itemId){
        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            return new ResponseEntity<ItemFormDto>(itemFormDto, HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<String>("존재하지 않는 상품 입니다.", HttpStatus.NOT_FOUND);
        }
    }

    // ✅ [수정] 상품 수정 (POST)
    @PostMapping(value = "/admin/item/{itemId}")
    public ResponseEntity itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                                     @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

        if(bindingResult.hasErrors()){
            return new ResponseEntity<String>("필수 정보를 모두 입력해주세요.", HttpStatus.BAD_REQUEST);
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            return new ResponseEntity<String>("첫번째 상품 이미지는 필수 입력 값 입니다.", HttpStatus.BAD_REQUEST);
        }

        try {
            Long updatedItemId = itemService.updateItem(itemFormDto, itemImgFileList);
            return new ResponseEntity<Long>(updatedItemId, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<String>("상품 수정 중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ [수정] 상품 관리 (리스트 조회)
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public ResponseEntity itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
        // Page 객체는 JSON으로 변환될 때 아주 예쁘게(content, pageable 정보 등) 바뀝니다.
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        return new ResponseEntity<Page<Item>>(items, HttpStatus.OK);
    }

    // ✅ [수정] 메인 상품 상세 페이지 (일반 사용자용)
    @GetMapping(value = "/item/{itemId}")
    public ResponseEntity itemDtlPublic(@PathVariable("itemId") Long itemId){
        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            return new ResponseEntity<ItemFormDto>(itemFormDto, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<String>("존재하지 않는 상품입니다.", HttpStatus.BAD_REQUEST);
        }
    }
}