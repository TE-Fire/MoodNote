# Skill: 创建用户实体

## 触发条件
当需要创建用户表对应的实体类时。

## 前置依赖
- Skill-011-创建BaseEntity基类

## 执行规范

### 文件位置
- 实体文件：`moodnote-pojo/src/main/java/com/moodnote/pojo/entity/`

### 命名规范
- 实体名称：`User.java`
- 表名：`mood_user`

### 代码规范
- 继承 BaseEntity 基类
- 使用 MyBatis-Plus 的表注解
- 使用 Lombok 注解简化代码
- 字段命名与数据库表一致

### 依赖引入
- MyBatis-Plus 依赖
- Lombok 依赖

## 代码模板

### 模板说明
创建用户表对应的实体类，包含所有字段。

### 代码示例

#### User.java 用户实体
```java
package com.moodnote.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mood_user")
public class User extends BaseEntity {
    
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别：0未知/1男/2女
     */
    private Integer gender;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
}
```

## 验收标准
1. 用户实体类创建成功
2. 继承了 BaseEntity 基类
3. 配置了正确的表名注解
4. 包含了所有必要的字段
5. 字段类型与数据库表一致

## 关联 Skill
前置：Skill-011-创建BaseEntity基类

后置：Skill-036-创建用户DTO和VO对象