# Skill: 创建用户DTO和VO对象

## 触发条件
当需要创建用户相关的数据传输对象（DTO）和视图对象（VO）时。

## 前置依赖
- Skill-035-创建用户实体

## 执行规范

### 文件位置
- DTO 文件：`moodnote-pojo/src/main/java/com/moodnote/pojo/dto/`
- VO 文件：`moodnote-pojo/src/main/java/com/moodnote/pojo/vo/`

### 命名规范
- DTO 命名：`{业务名称}{操作}DTO.java`
- VO 命名：`{业务名称}VO.java`

### 代码规范
- 使用 Lombok 注解简化代码
- 使用 Jakarta Validation 进行参数校验
- 字段命名与前端保持一致

### 依赖引入
- Lombok 依赖
- Jakarta Validation 依赖

## 代码模板

### 模板说明
创建用户相关的 DTO 和 VO 对象，用于前后端数据交互。

### 代码示例

#### 1. 认证相关 DTO

##### RegisterDTO.java 注册请求体
```java
package com.moodnote.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6位")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "验证码不能为空")
    private String code;

    private String nickname;
}
```

##### LoginDTO.java 登录请求体
```java
package com.moodnote.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    
    @NotBlank(message = "用户名/邮箱不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String captcha;

    @NotBlank(message = "验证码key不能为空")
    private String captchaKey;
}
```

##### SendCodeDTO.java 发送验证码请求体
```java
package com.moodnote.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendCodeDTO {
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "类型不能为空")
    private String type; // register / reset
}
```

##### ResetPasswordDTO.java 重置密码请求体
```java
package com.moodnote.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordDTO {
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "验证码不能为空")
    private String code;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6位")
    private String newPassword;
}
```

##### UpdateProfileDTO.java 更新用户信息请求体
```java
package com.moodnote.pojo.dto;

import lombok.Data;

@Data
public class UpdateProfileDTO {
    
    private String nickname;
    private String avatar;
    private String phone;
    private Integer gender;
}
```

#### 2. 视图对象 VO

##### UserVO.java 用户信息视图
```java
package com.moodnote.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
    private String phone;
    private Integer gender;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
}
```

##### LoginVO.java 登录响应视图
```java
package com.moodnote.pojo.vo;

import lombok.Data;

@Data
public class LoginVO {
    
    private String token;
    private UserVO user;
}
```

## 验收标准
1. 所有用户相关的 DTO 和 VO 对象创建成功
2. 包含了必要的字段
3. 使用了参数校验注解
4. 字段命名与前端保持一致
5. 结构清晰，职责明确

## 关联 Skill
前置：Skill-035-创建用户实体

后置：Skill-037-创建用户Mapper