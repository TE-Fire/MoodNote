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
import com.moodnote.mapper.UserMapper;
import com.moodnote.pojo.dto.RegisterDTO;
import com.moodnote.pojo.dto.SendCodeDTO;
import com.moodnote.pojo.entity.User;
import com.moodnote.pojo.vo.CaptchaVO;
import com.moodnote.service.AuthService;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @Override
    public Result<Void> sendCode(SendCodeDTO sendCodeDTO) {
        // 验证图形验证码
        String captchaKey = sendCodeDTO.getCaptchaKey();
        String captcha = sendCodeDTO.getCaptcha();

        if (!verifyCaptcha(captchaKey, captcha)) {
            return Result.error(MessageConstant.CODE_ERROR);
        }

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

    

    /**
     * 注册
     * @param registerDTO
     * @return
     */
    @Override
    public Result<Void> register(RegisterDTO registerDTO) {
        // 1. 检查用户名是否已存在
        if (userMapper.existByUsername(registerDTO.getUsername())) {
            return Result.error(MessageConstant.USERNAME_EXISTED);
        }

        // 3. 检查邮箱是否已注册
        if (userMapper.existByEmail(registerDTO.getEmail())) {
            return Result.error(MessageConstant.EMAIL_EXISTED);
        }

        // 3. 验证邮箱验证码
        if (!verifyCode(registerDTO.getEmail(), MessageConstant.REGISTER, registerDTO.getCode())) {
            return Result.error(MessageConstant.CODE_ERROR);
        }

        // 4. 创建用户实体
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));  // 密码加密
        user.setEmail(registerDTO.getEmail());
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setAvatar(null);
        user.setGender(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 5. 保存到数据库
        userMapper.insert(user);

        log.info("用户注册成功，用户名：{}，邮箱：{}", registerDTO.getUsername(), registerDTO.getEmail());
        return Result.success(MessageConstant.REGISTER_SUCCESS);
    }

    /**
     * 验证图形验证码
     * @param captchaKey
     * @param inputCode
     * @return
     */
    private boolean verifyCaptcha(String captchaKey, String inputCode) {
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

    /**
     * 验证邮箱验证码
     * @param email 邮箱地址
     * @param type 类型（register/login/reset）
     * @param inputCode 用户输入的验证码
     * @return 验证是否成功
     */
    private boolean verifyCode(String email, String type, String inputCode) {
        // 1. 从redis中获取验证码文本
        String storedCode = redisUtil.get(RedisKeyConstant.getVerificationKey(type, email), String.class);
        
        // 2. 检查验证码是否存在（过期或不存在）
        if (storedCode == null) {
            log.warn("邮箱验证码已过期或不存在，email: {}, type: {}", email, type);
            return false;
        }
        
        // 3. 检查验证码是否正确
        if (!storedCode.equalsIgnoreCase(inputCode)) {
            log.warn("邮箱验证码错误，email: {}, input: {}, stored: {}", email, inputCode, storedCode);
            return false;
        }
        
        // 4. 验证成功，删除redis中的验证码（防止重复使用）
        redisUtil.delete(RedisKeyConstant.getVerificationKey(type, email));
        log.info("邮箱验证码验证成功，email: {}, type: {}", email, type);
        return true;
    }
}