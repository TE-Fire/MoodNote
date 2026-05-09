package com.moodnote.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.SendCodeDTO;
import com.moodnote.pojo.vo.CaptchaVO;
import com.moodnote.service.AuthService;

import lombok.extern.slf4j.Slf4j;

/**
 * 认证控制器
 * @author TE
 * @date 2026-5-07
 * @time 14:00:00
 */
@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    
    @PostMapping("/send-code")
    public Result<Void> sendCode(@RequestBody SendCodeDTO sendCodeDTO) {
        return authService.sendCode(sendCodeDTO);
    }

    @GetMapping("/captcha")
    public Result<CaptchaVO> getCaptcha() {
        log.info("获取图形验证码");
        return Result.success(authService.getCaptcha());
    }
}
