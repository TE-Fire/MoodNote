package com.moodnote.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
    private String phone;
    private Integer gender;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
}