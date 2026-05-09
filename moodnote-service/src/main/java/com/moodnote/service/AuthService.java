package com.moodnote.service;

import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.SendCodeDTO;
import com.moodnote.pojo.vo.CaptchaVO;

public interface AuthService {

    /**
     * 发送验证码
     * @param sendCodeDTO
     * @return
     */
    Result<Void> sendCode(SendCodeDTO sendCodeDTO);

    /**
     * 验证验证码
     * @param email 邮箱
     * @param type 验证码类型（register/login/reset）
     * @param inputCode 用户输入的验证码
     * @return 是否验证成功
     */
    boolean verifyCode(String email, String type, String inputCode);

    /**
     * 获取图形验证码
     * @return
     */
    CaptchaVO getCaptcha();

    /**
     * 验证图形验证码
     * @param captchaKey 验证码key
     * @param captchaImg 验证码图片
     * @return 是否验证成功
     */
    boolean verifyCaptcha(String captchaKey, String inputCode);
}
