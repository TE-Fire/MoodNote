# Skill: 创建BaseMapper

## 触发条件
当需要创建 Mapper 接口的基类，统一管理通用的数据库操作方法时。

## 前置依赖
- Skill-015-创建DTO和VO对象

## 执行规范

### 文件位置
- 基类文件：`moodnote-mapper/src/main/java/com/moodnote/mapper/`

### 命名规范
- 基类名称：`BaseMapper.java`

### 代码规范
- 继承 MyBatis-Plus 的 BaseMapper
- 定义通用的数据库操作方法

### 依赖引入
- MyBatis-Plus 依赖

## 代码模板

### 模板说明
创建 Mapper 接口的基类，继承 MyBatis-Plus 的 BaseMapper。

### 代码示例

#### BaseMapper.java 基类
```java
package com.moodnote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * Mapper 基类
 * @param <T> 实体类型
 */
public interface BaseMapper<T> extends BaseMapper<T> {
    // 可以在这里定义通用的方法
}
```

## 验收标准
1. BaseMapper 基类创建成功
2. 继承了 MyBatis-Plus 的 BaseMapper
3. 结构清晰，便于扩展

## 关联 Skill
前置：Skill-015-创建DTO和VO对象

后置：Skill-017-创建日记Mapper