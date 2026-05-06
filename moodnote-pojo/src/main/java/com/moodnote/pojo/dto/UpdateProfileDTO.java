package com.moodnote.pojo.dto;

import lombok.Data;

@Data
public class UpdateProfileDTO {

    private String nickname;
    private String avatar;
    private String phone;
    private Integer gender;
}