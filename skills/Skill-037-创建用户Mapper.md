# Skill: 创建用户Mapper

## 触发条件
当需要创建用户表的 Mapper 接口时。

## 前置依赖
- Skill-036-创建用户DTO和VO对象

## 执行规范

### 文件位置
- Mapper 接口：`moodnote-mapper/src/main/java/com/moodnote/mapper/`

### 命名规范
- Mapper 接口：`UserMapper.java`

### 代码规范
- 继承 BaseMapper 接口
- 定义必要的查询方法

### 依赖引入
- MyBatis-Plus 依赖

## 代码模板

### 模板说明
创建用户表的 Mapper 接口，包含基本的 CRUD 操作和登录相关的查询方法。

### 代码示例

#### UserMapper.java 用户 Mapper 接口
```java
package com.moodnote.mapper;

import com.moodnote.pojo.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查询用户
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     */
    User selectByEmail(@Param("email") String email);

    /**
     * 根据用户名或邮箱查询用户
     */
    User selectByUsernameOrEmail(@Param("username") String username);

    /**
     * 更新最后登录时间
     */
    void updateLastLoginTime(@Param("id") Long id);
}
```

## 验收标准
1. 用户 Mapper 接口创建成功
2. 继承了 BaseMapper 接口
3. 包含了登录相关的查询方法
4. 方法定义正确

## 关联 Skill
前置：Skill-036-创建用户DTO和VO对象

后置：Skill-038-创建认证Service接口与实现