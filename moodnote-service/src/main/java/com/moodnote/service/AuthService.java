package com.moodnote.service;

import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.SendCodeDTO;

public interface AuthService {

    /**
     * 发送验证码
     * @param sendCodeDTO
     * @return
     */
    Result<Void> sendCode(SendCodeDTO sendCodeDTO);
    
}
