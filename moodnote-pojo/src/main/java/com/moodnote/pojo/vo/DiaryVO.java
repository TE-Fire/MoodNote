package com.moodnote.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DiaryVO {
    private Long id;
    private String title;
    private String content;
    private Integer moodType;
    private Integer weatherType;
    private String city;
    private Integer isPrivate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<TagVO> tags;
}