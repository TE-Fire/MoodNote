# Skill: 创建认证Controller

## 触发条件
当需要创建用户认证相关的 RESTful API 接口时。

## 前置依赖
- Skill-038-创建认证Service接口与实现

## 执行规范

### 文件位置
- Controller 文件：`moodnote-server/src/main/java/com/moodnote/controller/`

### 命名规范
- 认证 Controller：`AuthController.java`
- 用户 Controller：`UserController.java`
- 路径前缀：`/api/v1/auth` 和 `/api/v1/user`

### 代码规范
- 使用 @RestController 注解
- 使用 @RequestMapping 注解定义路径
- 使用 @PostMapping、@GetMapping、@PutMapping 注解定义请求方法
- 使用 @Validated 注解进行参数校验

### 依赖引入
- Spring Web 依赖
- Jakarta Validation 依赖

## 代码模板

### 模板说明
创建用户认证相关的 RESTful API 接口，包含注册、登录、登出、验证码等功能。

### 代码示例

#### 1. AuthController.java 认证 Controller
```java
package com.moodnote.controller;

import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.*;
import com.moodnote.pojo.vo.LoginVO;
import com.moodnote.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    @Resource
    private AuthService authService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO dto) {
        log.info("Register user: {}", dto.getUsername());
        authService.register(dto);
        return Result.success("注册成功");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        log.info("Login user: {}", dto.getUsername());
        LoginVO loginVO = authService.login(dto, request);
        return Result.success(loginVO);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Logout user");
        authService.logout(request, response);
        return Result.success("登出成功");
    }

    /**
     * 刷新 token
     */
    @PostMapping("/refresh")
    public Result<String> refreshToken(HttpServletRequest request) {
        log.info("Refresh token");
        String token = authService.refreshToken(request);
        return Result.success(token);
    }

    /**
     * 发送邮箱验证码
     */
    @PostMapping("/send-code")
    public Result<?> sendCode(@Valid @RequestBody SendCodeDTO dto) {
        log.info("Send code to: {}", dto.getEmail());
        authService.sendCode(dto);
        return Result.success("验证码发送成功");
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public Result<?> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        log.info("Reset password for: {}", dto.getEmail());
        authService.resetPassword(dto);
        return Result.success("密码重置成功");
    }

    /**
     * 获取图形验证码
     */
    @GetMapping("/captcha")
    public Result<Map<String, Object>> getCaptcha() {
        log.info("Get captcha");
        Map<String, Object> captcha = authService.getCaptcha();
        return Result.success(captcha);
    }
}
```

#### 2. UserController.java 用户 Controller
```java
package com.moodnote.controller;

import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.UpdateProfileDTO;
import com.moodnote.pojo.vo.UserVO;
import com.moodnote.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    
    @Resource
    private AuthService authService;

    /**
     * 获取用户信息
     */
    @GetMapping("/profile")
    public Result<UserVO> getProfile() {
        // 实际项目中从 token 中获取用户 ID
        Long userId = 1L; // 临时硬编码
        log.info("Get profile for user: {}", userId);
        UserVO profile = authService.getProfile(userId);
        return Result.success(profile);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public Result<?> updateProfile(@Valid @RequestBody UpdateProfileDTO dto) {
        // 实际项目中从 token 中获取用户 ID
        Long userId = 1L; // 临时硬编码
        log.info("Update profile for user: {}", userId);
        authService.updateProfile(userId, dto);
        return Result.success("更新成功");
    }
}
```

## 验收标准
1. 认证 Controller 创建成功
2. 用户 Controller 创建成功
3. 包含了完整的认证接口
4. 使用了正确的 HTTP 方法
5. 包含了参数校验
6. 日志记录完整

## 关联 Skill
前置：Skill-038-创建认证Service接口与实现

后置：Skill-040-SpringSecurity配置