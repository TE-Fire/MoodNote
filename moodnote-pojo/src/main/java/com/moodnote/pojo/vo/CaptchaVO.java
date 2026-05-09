package com.moodnote.pojo.vo;

import lombok.Data;

@Data
public class CaptchaVO {
    // 验证码key
    private String captchaKey;
    // 验证码图片
    private String captchaImg;
}
