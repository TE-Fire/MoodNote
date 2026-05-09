package com.moodnote.pojo.vo;

import com.moodnote.pojo.entity.User;

import lombok.Data;

@Data
public class LoginVO {

    private String token;
    private User user;
}