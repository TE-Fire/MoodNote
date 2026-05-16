package com.moodnote.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class DiaryUpdateDTO {
    private String title;
    private String content;
    private Integer moodType;
    private Integer weatherType;
    private String city;
    private Integer isPrivate;
    private List<Long> tagIds;
}