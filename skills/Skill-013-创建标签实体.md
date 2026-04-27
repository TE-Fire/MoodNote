# Skill: 创建标签实体

## 触发条件
当需要创建标签表对应的实体类时。

## 前置依赖
- Skill-012-创建日记实体

## 执行规范

### 文件位置
- 实体文件：`moodnote-pojo/src/main/java/com/moodnote/pojo/entity/`

### 命名规范
- 实体名称：`Tag.java`
- 表名：`mood_tag`

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
创建标签表对应的实体类，包含所有字段。

### 代码示例

#### Tag.java 标签实体
```java
package com.moodnote.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mood_tag")
public class Tag extends BaseEntity {
    
    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签颜色（十六进制）
     */
    private String color;
}
```

## 验收标准
1. 标签实体类创建成功
2. 继承了 BaseEntity 基类
3. 配置了正确的表名注解
4. 包含了所有必要的字段
5. 字段类型与数据库表一致

## 关联 Skill
前置：Skill-012-创建日记实体

后置：Skill-014-创建日记-标签关联实体