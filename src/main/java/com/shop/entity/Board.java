package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @ToString
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;   // 제목

    @Column(length = 1000)
    private String content; // 내용

    private String writerName; // 작성자

    private LocalDateTime regTime = LocalDateTime.now(); // 등록시간
}