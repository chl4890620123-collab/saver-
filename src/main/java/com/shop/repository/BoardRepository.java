package com.shop.repository;

import com.shop.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 최신글이 위에 오도록 정렬해서 가져오기
    List<Board> findAllByOrderByIdDesc();
}