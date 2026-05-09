package com.moodnote.service.impl;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.moodnote.common.constant.DataConstant;
import com.moodnote.common.constant.MessageConstant;
import com.moodnote.common.constant.RedisKeyConstant;
import com.moodnote.common.exception.BusinessException;
import com.moodnote.common.exception.ErrorCode;
import com.moodnote.common.utils.EmailUtil;
import com.moodnote.common.utils.RandomCodeUtil;
import com.moodnote.common.utils.RedisUtil;
import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.SendCodeDTO;
import com.moodnote.pojo.vo.CaptchaVO;
import com.moodnote.service.AuthService;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DefaultKaptcha defaultKaptcha;


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

    @Override
    public CaptchaVO getCaptcha() {
        // 1. 生成验证码文本
        String captchaText = defaultKaptcha.createText();

        // 2. 生成验证码图片
        BufferedImage image = defaultKaptcha.createImage(captchaText);

        // 3. 生成唯一key
        String captchaKey = UUID.randomUUID().toString();

        // 4. 将验证码文本存储到redis，过期时间5分钟
        redisUtil.set(RedisKeyConstant.getCaptchaKey(captchaKey), captchaText, DataConstant.VERIFICATION_CODE_TIMEOUT, TimeUnit.MINUTES);

        // 5. 将图片转换为Base64编码
        String captchaImg = null;
        try {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", bStream);
            byte[] bytes = bStream.toByteArray();
            captchaImg = "data:image/jpeg;base64," + 
                        Base64.getEncoder().encodeToString(bytes);
            log.info(MessageConstant.CODE_TO_IMAGE_SUCCESS);
        } catch (Exception e) {
            log.info(MessageConstant.CODE_TO_IMAGE_ERROR);
            throw new BusinessException(ErrorCode.CODE_TO_IMAGE_ERROR, MessageConstant.CODE_TO_IMAGE_ERROR);
        }
        // 6. 构建返回对象
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaKey(captchaKey);
        captchaVO.setCaptchaImg(captchaImg);
        return captchaVO;
    }

    @Override
    public boolean verifyCaptcha(String captchaKey, String inputCode) {
        // 1. 从redis中获取验证码文本
        String storedCode = redisUtil.get(RedisKeyConstant.getCaptchaKey(captchaKey), String.class);
        
        if (storedCode == null) {
            throw new BusinessException(ErrorCode.CODE_EXPIRE, MessageConstant.CODE_EXPIRE);
        }
        if (storedCode.equalsIgnoreCase(inputCode)) {
            // 验证成功，删除redis中的验证码
            redisUtil.delete(RedisKeyConstant.getCaptchaKey(captchaKey));
            return true;
        }
        log.warn("验证码错误，key: {}, 输入: {}, 存储: {}", 
             captchaKey, inputCode, storedCode);
        
        return false;
    }
}