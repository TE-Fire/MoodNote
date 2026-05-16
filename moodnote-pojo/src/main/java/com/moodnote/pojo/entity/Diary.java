package com.moodnote.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true) // EqualsAndHashCode 注解用于自动生成 equals 方法，考虑父类的 id 字段
public class Diary extends BaseEntity {
    private Long userId;
    private String title;
    private String content;
    private Integer moodType;
    private Integer weatherType;
    private String city;
    private Integer isPrivate;
}