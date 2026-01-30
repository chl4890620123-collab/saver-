package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // 어노테이션 통합
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import com.shop.dto.OrderHistDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController // 1. 모든 응답을 JSON 데이터로 보냅니다.
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문하기
    @PostMapping(value = "/order")
    public ResponseEntity order(@RequestBody @Valid OrderDto orderDto,
                                BindingResult bindingResult, Principal principal){

        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();
        Long orderId;

        try {
            orderId = orderService.order(orderDto, email);
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    // ⭐ [수정됨] 구매 이력 조회
    // 기존: HTML 파일 이름("order/orderHist") 리턴 -> 리액트 에러 발생
    // 변경: 주문 데이터(Page 객체) 자체를 JSON으로 리턴
    @GetMapping(value = {"/orders", "/orders/{page}"})
    public ResponseEntity<Page<OrderHistDto>> orderHist(@PathVariable("page") Optional<Integer> page, 
                                                        Principal principal){

        // 1. 페이징 설정 (페이지 번호가 없으면 0페이지)
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);

        // 2. 서비스 호출해서 데이터 가져오기
        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        // 3. Model에 담는 과정 삭제하고, 데이터 바로 리턴!
        return new ResponseEntity<>(ordersHistDtoList, HttpStatus.OK);
    }

    // 주문 취소
    @PostMapping("/order/{orderId}/cancel")
    public ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId , Principal principal){

        if(!orderService.validateOrder(orderId, principal.getName())){
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}