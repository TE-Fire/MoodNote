package com.moodnote.service;

import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.LoginDTO;
import com.moodnote.pojo.dto.RegisterDTO;
import com.moodnote.pojo.dto.ResetPasswordDTO;
import com.moodnote.pojo.dto.SendCodeDTO;
import com.moodnote.pojo.vo.CaptchaVO;
import com.moodnote.pojo.vo.LoginVO;

public interface AuthService {

    /**
     * 发送验证码
     * @param sendCodeDTO
     * @return
     */
    Result<Void> sendCode(SendCodeDTO sendCodeDTO);


    /**
     * 获取图形验证码
     * @return
     */
    CaptchaVO getCaptcha();

    /**
     * 注册
     * @param registerDTO
     * @return
     */
    Result<Void> register(RegisterDTO registerDTO);


    /**
     * 登录
     * @param loginDTO
     * @return
     */
    Result<LoginVO> login(LoginDTO loginDTO);


    /**
     * 重置密码
     * @param resetPasswordDTO
     * @return
     */
    Result<Void> resetPassword(ResetPasswordDTO resetPasswordDTO);

    /**
     * 退出登录
     * @return
     */
    Result<Void> logout();
}
