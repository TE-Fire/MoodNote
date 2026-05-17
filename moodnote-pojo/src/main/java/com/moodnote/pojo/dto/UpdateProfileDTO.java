package com.moodnote.pojo.dto;

import com.moodnote.pojo.entity.BaseEntity;

import lombok.Data;

@Data
public class UpdateProfileDTO extends BaseEntity{

    private String nickname;
    private String avatar;
    private String phone;
    private Integer gender;
}