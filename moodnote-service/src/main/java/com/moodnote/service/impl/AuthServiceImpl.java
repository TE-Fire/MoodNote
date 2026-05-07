package com.moodnote.service.impl;

import com.moodnote.common.constant.DataConstant;
import com.moodnote.common.constant.MessageConstant;
import com.moodnote.common.constant.RedisKeyConstant;
import com.moodnote.common.utils.EmailUtil;
import com.moodnote.common.utils.RandomCodeUtil;
import com.moodnote.common.utils.RedisUtil;
import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.SendCodeDTO;
import com.moodnote.service.AuthService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private RedisUtil redisUtil;

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
            // 将验证码存储到redis，过期时间5分钟
            redisUtil.set(RedisKeyConstant.getVerificationKey(type, email), code, DataConstant.VERIFICATION_CODE_TIMEOUT, TimeUnit.MINUTES);
            return Result.success(MessageConstant.CODE_SEND_SUCCESS);
        } else {
            return Result.error(MessageConstant.CODE_SEND_FAILED);
        }
    }


    @Override
    public boolean verifyCode(String email, String type, String inputCode) {
        return false;
    }
}