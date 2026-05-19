package com.moodnote.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class TagVO {
    @JsonIgnore // 忽略日记ID，不返回给前端
    private Long diaryId;
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createTime;
}