package com.moodnote.service.impl;

import com.moodnote.common.constant.MessageConstant;
import com.moodnote.common.utils.EmailUtil;
import com.moodnote.common.utils.RandomCodeUtil;
import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.SendCodeDTO;
import com.moodnote.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private EmailUtil emailUtil;

    @Override
    public Result<Void> sendCode(SendCodeDTO sendCodeDTO) {
        String email = sendCodeDTO.getEmail();
        String type = sendCodeDTO.getType();
        
        log.info("发送验证码，邮箱：{}，类型：{}", email, type);
        
        String code = RandomCodeUtil.generateRandomCode();
        
        boolean success = switch (type) {
            case MessageConstant.REGISTER -> emailUtil.sendRegisterCode(email, code);
            case MessageConstant.LOGIN -> emailUtil.sendLoginCode(email, code);
            case MessageConstant.RESET -> emailUtil.sendResetCode(email, code);
            default -> false;
        };
        
        if (success) {
            return Result.success(MessageConstant.CODE_SEND_SUCCESS);
        } else {
            return Result.error(MessageConstant.CODE_SEND_FAILED);
        }
    }

 
}