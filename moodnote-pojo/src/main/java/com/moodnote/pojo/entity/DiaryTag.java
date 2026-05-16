package com.moodnote.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DiaryTag {
    private Long id;
    private Long diaryId;
    private Long tagId;
    private LocalDateTime createTime;
    private Integer deleted;
}