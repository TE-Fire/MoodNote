package com.moodnote.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    private String username;

    private String password;

    private String email;

    private String nickname;

    private String avatar;

    private String phone;

    private Integer gender;

    private LocalDateTime lastLoginTime;
}