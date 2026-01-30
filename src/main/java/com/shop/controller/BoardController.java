package com.shop.controller;

import com.shop.entity.Board;
import com.shop.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;

    // 1. 목록 조회
    @GetMapping("/list")
    public List<Board> list() {
        return boardRepository.findAllByOrderByIdDesc();
    }

    // 2. 상세 조회
    @GetMapping("/{id}")
    public Board getOne(@PathVariable Long id) {
        return boardRepository.findById(id).orElse(null);
    }

    // 3. 글 쓰기
    @PostMapping("/new")
    public String write(@RequestBody Map<String, String> data) {
        Board board = new Board();
        board.setTitle(data.get("title"));
        board.setContent(data.get("content"));
        board.setWriterName(data.get("writerName"));
        boardRepository.save(board);
        return "작성 완료";
    }

    // 4. ⭐ [수정 기능] 추가됨
    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody Map<String, String> data) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board != null) {
            board.setTitle(data.get("title"));
            board.setContent(data.get("content"));
            boardRepository.save(board); // 변경된 내용 저장
            return "수정 완료";
        }
        return "오류";
    }

    // 5. ⭐ [삭제 기능] 추가됨
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        boardRepository.deleteById(id);
        return "삭제 완료";
    }
}