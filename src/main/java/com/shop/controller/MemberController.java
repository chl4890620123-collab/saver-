package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Map;

@RequestMapping("/members")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    // 1. 회원가입
    @PostMapping(value = "/new")
    public ResponseEntity newMember(@RequestBody @Valid MemberFormDto memberFormDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity<String>("입력값을 확인해주세요.", HttpStatus.BAD_REQUEST);
        }
        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("회원가입 성공", HttpStatus.OK);
    }

    // 2. 로그인 (세션 생성 로직 추가됨!)
    @PostMapping(value = "/login")
    public ResponseEntity login(@RequestBody Map<String, String> data, HttpServletRequest request) {
        String email = data.get("email");
        String password = data.get("password");

        try {
            // 회원 정보 조회
            UserDetails userDetails = memberService.loadUserByUsername(email);

            // 비밀번호 일치 확인
            if (passwordEncoder.matches(password, userDetails.getPassword())) {

                // ⭐ 핵심: 스프링 시큐리티 인증 토큰 생성
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // 시큐리티 컨텍스트에 토큰 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // ⭐ 핵심: 세션(HttpSession)에 시큐리티 컨텍스트 저장 (이게 있어야 브라우저가 기억함)
                HttpSession session = request.getSession(true);
                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

                return new ResponseEntity<String>("환영합니다! " + userDetails.getUsername(), HttpStatus.OK);
            } else {
                return new ResponseEntity<String>("비밀번호가 틀렸습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<String>("존재하지 않는 회원입니다.", HttpStatus.BAD_REQUEST);
        }
    }
}