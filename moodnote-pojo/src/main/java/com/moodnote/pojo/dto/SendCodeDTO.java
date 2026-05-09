package com.moodnote.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendCodeDTO {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "类型不能为空")
    private String type;

    @NotBlank(message = "验证码key不能为空")
    private String captchaKey;

    @NotBlank(message = "验证码不能为空")
    private String captcha;
}