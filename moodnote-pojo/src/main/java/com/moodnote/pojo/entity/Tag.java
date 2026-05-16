package com.moodnote.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true) // EqualsAndHashCode 注解用于自动生成 equals 方法，考虑父类的 id 字段
public class Tag extends BaseEntity {
    private Long userId;
    private String name;
    private String color;
}