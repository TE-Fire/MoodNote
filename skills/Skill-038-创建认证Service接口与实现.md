# Skill: 创建认证Service接口与实现

## 触发条件
当需要创建用户认证相关的业务逻辑时。

## 前置依赖
- Skill-037-创建用户Mapper

## 执行规范

### 文件位置
- Service 接口：`moodnote-service/src/main/java/com/moodnote/service/`
- Service 实现：`moodnote-service/src/main/java/com/moodnote/service/impl/`

### 命名规范
- Service 接口：`AuthService.java`
- Service 实现：`AuthServiceImpl.java`

### 代码规范
- 接口定义业务方法
- 实现类包含具体业务逻辑
- 使用 @Service 注解标记实现类
- 使用 @Autowired 注入 Mapper

### 依赖引入
- Spring Security 依赖
- JJWT 依赖
- Spring Mail 依赖
- Kaptcha 依赖

## 代码模板

### 模板说明
创建用户认证相关的 Service 接口和实现类，包含注册、登录、登出、验证码等功能。

### 代码示例

#### 1. AuthService.java 认证 Service 接口
```java
package com.moodnote.service;

import com.moodnote.pojo.dto.*;
import com.moodnote.pojo.vo.LoginVO;
import com.moodnote.pojo.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface AuthService {
    
    /**
     * 用户注册
     */
    void register(RegisterDTO dto);

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto, HttpServletRequest request);

    /**
     * 用户登出
     */
    void logout(HttpServletRequest request, HttpServletResponse response);

    /**
     * 刷新 token
     */
    String refreshToken(HttpServletRequest request);

    /**
     * 发送邮箱验证码
     */
    void sendCode(SendCodeDTO dto);

    /**
     * 重置密码
     */
    void resetPassword(ResetPasswordDTO dto);

    /**
     * 获取图形验证码
     */
    Map<String, Object> getCaptcha();

    /**
     * 获取用户信息
     */
    UserVO getProfile(Long userId);

    /**
     * 更新用户信息
     */
    void updateProfile(Long userId, UpdateProfileDTO dto);
}
```

#### 2. AuthServiceImpl.java 认证 Service 实现类
```java
package com.moodnote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moodnote.exception.BusinessException;
import com.moodnote.exception.ErrorCode;
import com.moodnote.mapper.UserMapper;
import com.moodnote.pojo.dto.*;
import com.moodnote.pojo.entity.User;
import com.moodnote.pojo.vo.LoginVO;
import com.moodnote.pojo.vo.UserVO;
import com.moodnote.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expire}")
    private Long jwtExpire;

    @Override
    @Transactional
    public void register(RegisterDTO dto) {
        // 校验验证码
        String codeKey = "email:code:" + dto.getEmail();
        String redisCode = redisTemplate.opsForValue().get(codeKey);
        if (redisCode == null || !redisCode.equals(dto.getCode())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "验证码错误");
        }

        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(dto.getUsername());
        if (existingUser != null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名已存在");
        }

        // 检查邮箱是否已存在
        existingUser = userMapper.selectByEmail(dto.getEmail());
        if (existingUser != null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        user.setGender(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setDeleted(0);

        int result = userMapper.insert(user);
        if (result != 1) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "注册失败");
        }

        // 删除验证码
        redisTemplate.delete(codeKey);
    }

    @Override
    public LoginVO login(LoginDTO dto, HttpServletRequest request) {
        // 校验图形验证码
        String captchaKey = "captcha:" + dto.getCaptchaKey();
        String redisCaptcha = redisTemplate.opsForValue().get(captchaKey);
        if (redisCaptcha == null || !redisCaptcha.equalsIgnoreCase(dto.getCaptcha())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "验证码错误");
        }

        // 删除验证码
        redisTemplate.delete(captchaKey);

        // 查询用户
        User user = userMapper.selectByUsernameOrEmail(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名或密码错误");
        }

        // 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名或密码错误");
        }

        // 更新最后登录时间
        userMapper.updateLastLoginTime(user.getId());

        // 生成 token
        String token = generateToken(user.getId());

        // 构建返回对象
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setPassword(null); // 不返回密码

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUser(userVO);

        return loginVO;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 从请求头获取 token
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token)) {
            // 可以将 token 加入黑名单
            String tokenKey = "token:blacklist:" + token;
            redisTemplate.opsForValue().set(tokenKey, "1", jwtExpire, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public String refreshToken(HttpServletRequest request) {
        // 从请求头获取 token
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "token 为空");
        }

        // 解析 token 获取用户 ID
        Long userId = parseToken(token);
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "token 无效");
        }

        // 生成新 token
        return generateToken(userId);
    }

    @Override
    public void sendCode(SendCodeDTO dto) {
        // 生成验证码
        String code = generateCode();

        // 发送邮件
        sendEmail(dto.getEmail(), code);

        // 存储验证码到 Redis
        String codeKey = "email:code:" + dto.getEmail();
        redisTemplate.opsForValue().set(codeKey, code, 5, TimeUnit.MINUTES);
    }

    @Override
    public void resetPassword(ResetPasswordDTO dto) {
        // 校验验证码
        String codeKey = "email:code:" + dto.getEmail();
        String redisCode = redisTemplate.opsForValue().get(codeKey);
        if (redisCode == null || !redisCode.equals(dto.getCode())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "验证码错误");
        }

        // 查询用户
        User user = userMapper.selectByEmail(dto.getEmail());
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "邮箱未注册");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        int result = userMapper.updateById(user);
        if (result != 1) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "重置密码失败");
        }

        // 删除验证码
        redisTemplate.delete(codeKey);
    }

    @Override
    public Map<String, Object> getCaptcha() {
        // 生成验证码
        String captcha = generateCaptcha();
        String captchaKey = UUID.randomUUID().toString();

        // 存储验证码到 Redis
        redisTemplate.opsForValue().set("captcha:" + captchaKey, captcha, 5, TimeUnit.MINUTES);

        // 构建返回对象
        Map<String, Object> result = new HashMap<>();
        result.put("key", captchaKey);
        result.put("image", generateCaptchaImage(captcha)); // 实际项目中需要生成图片

        return result;
    }

    @Override
    public UserVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setPassword(null); // 不返回密码

        return userVO;
    }

    @Override
    public void updateProfile(Long userId, UpdateProfileDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        // 更新用户信息
        if (StringUtils.hasText(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (StringUtils.hasText(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }
        if (StringUtils.hasText(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        user.setUpdateTime(LocalDateTime.now());

        int result = userMapper.updateById(user);
        if (result != 1) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "更新失败");
        }
    }

    // 生成 token
    private String generateToken(Long userId) {
        // 实际项目中使用 JJWT 生成 token
        return "token-" + userId + "-" + System.currentTimeMillis();
    }

    // 解析 token
    private Long parseToken(String token) {
        // 实际项目中使用 JJWT 解析 token
        try {
            String[] parts = token.split("-");
            return Long.parseLong(parts[1]);
        } catch (Exception e) {
            return null;
        }
    }

    // 生成邮箱验证码
    private String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    // 生成图形验证码
    private String generateCaptcha() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    // 生成验证码图片
    private String generateCaptchaImage(String captcha) {
        // 实际项目中生成图片并返回 base64
        return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
    }

    // 发送邮件
    private void sendEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("MoodNote 验证码");
        message.setText("您的验证码是：" + code + "，5分钟内有效。");
        mailSender.send(message);
    }
}
```

## 验收标准
1. 认证 Service 接口创建成功
2. 认证 Service 实现类创建成功
3. 包含了完整的认证功能
4. 实现了验证码、邮箱发送等功能
5. 包含了异常处理

## 关联 Skill
前置：Skill-037-创建用户Mapper

后置：Skill-039-创建认证Controller