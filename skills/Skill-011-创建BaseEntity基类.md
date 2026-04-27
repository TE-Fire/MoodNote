# Skill: 创建BaseEntity基类

## 触发条件
当需要创建数据库实体类的基类，统一管理公共字段时。

## 前置依赖
- Skill-010-日志配置

## 执行规范

### 文件位置
- 基类文件：`moodnote-pojo/src/main/java/com/moodnote/pojo/entity/`

### 命名规范
- 基类名称：`BaseEntity.java`

### 代码规范
- 使用 Lombok 注解简化代码
- 包含所有表的公共字段
- 实现序列化接口

### 依赖引入
- Lombok 依赖

## 代码模板

### 模板说明
创建数据库实体的基类，包含所有表的公共字段。

### 代码示例

#### BaseEntity.java 基类
```java
package com.moodnote.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 软删除标记
     * 0: 正常
     * 1: 已删除
     */
    @TableLogic
    private Integer deleted;
}
```

## 验收标准
1. BaseEntity 基类创建成功
2. 包含所有表的公共字段
3. 使用了 MyBatis-Plus 的字段填充注解
4. 使用了软删除注解
5. 实现了序列化接口

## 关联 Skill
前置：Skill-010-日志配置

后置：Skill-012-创建日记实体