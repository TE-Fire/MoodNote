# Skill: 创建日记-标签关联实体

## 触发条件
当需要创建日记-标签关联表对应的实体类时。

## 前置依赖
- Skill-013-创建标签实体

## 执行规范

### 文件位置
- 实体文件：`moodnote-pojo/src/main/java/com/moodnote/pojo/entity/`

### 命名规范
- 实体名称：`DiaryTag.java`
- 表名：`mood_diary_tag`

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
创建日记-标签关联表对应的实体类，包含所有字段。

### 代码示例

#### DiaryTag.java 日记-标签关联实体
```java
package com.moodnote.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mood_diary_tag")
public class DiaryTag extends BaseEntity {
    
    /**
     * 日记ID（逻辑外键）
     */
    private Long diaryId;

    /**
     * 标签ID（逻辑外键）
     */
    private Long tagId;
}
```

## 验收标准
1. 日记-标签关联实体类创建成功
2. 继承了 BaseEntity 基类
3. 配置了正确的表名注解
4. 包含了所有必要的字段
5. 字段类型与数据库表一致

## 关联 Skill
前置：Skill-013-创建标签实体

后置：Skill-015-创建DTO和VO对象