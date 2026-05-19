package com.moodnote.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TagVO {
    private Long diaryId;
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createTime;
}