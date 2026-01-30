package com.shop.controller;

import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;

    // ⭐ [수정] 주소를 "/" 에서 "/api/main" 으로 변경!
    // 이제 localhost:8080/ 으로 접속하면 이 코드가 실행되지 않고, index.html이 나옵니다.
    @GetMapping(value = "/api/main")
    public ResponseEntity<Page<MainItemDto>> main(ItemSearchDto itemSearchDto,
                                                  @RequestParam("page") Optional<Integer> page) {

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

}