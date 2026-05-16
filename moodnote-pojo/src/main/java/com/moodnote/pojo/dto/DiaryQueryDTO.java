package com.moodnote.pojo.dto;

import lombok.Data;

@Data
public class DiaryQueryDTO {
    private String keyword;
    private Integer moodType;
    private Integer weatherType;
    private String startDate;
    private String endDate;
    private Integer pageNum;
    private Integer pageSize;
}