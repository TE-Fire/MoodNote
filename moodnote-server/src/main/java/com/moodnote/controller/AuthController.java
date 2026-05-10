package com.moodnote.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.LoginDTO;
import com.moodnote.pojo.dto.RegisterDTO;
import com.moodnote.pojo.dto.ResetPasswordDTO;
import com.moodnote.pojo.dto.SendCodeDTO;
import com.moodnote.pojo.vo.CaptchaVO;
import com.moodnote.pojo.vo.LoginVO;
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
    
    /**
     * 发送验证码邮件
     * @param sendCodeDTO
     * @return
     */
    @PostMapping("/send-code")
    public Result<Void> sendCode(@RequestBody SendCodeDTO sendCodeDTO) {
        return authService.sendCode(sendCodeDTO);
    }

    /**
     * 获取图形验证码
     * @return
     */
    @GetMapping("/captcha")
    public Result<CaptchaVO> getCaptcha() {
        log.info("获取图形验证码");
        return Result.success(authService.getCaptcha());
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO registerDTO) {
        log.info("注册用户: {}", registerDTO);
        return authService.register(registerDTO);
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        log.info("登录用户: {}", loginDTO);
        return authService.login(loginDTO);
    }

    /**
     * 重置密码
     * @param resetPasswordDTO
     * @return
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        log.info("重置密码: {}", resetPasswordDTO);
        return authService.resetPassword(resetPasswordDTO);
    }
}
