# Skill: 创建日记实体

## 触发条件
当需要创建日记表对应的实体类时。

## 前置依赖
- Skill-011-创建BaseEntity基类

## 执行规范

### 文件位置
- 实体文件：`moodnote-pojo/src/main/java/com/moodnote/pojo/entity/`

### 命名规范
- 实体名称：`Diary.java`
- 表名：`mood_diary`

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
创建日记表对应的实体类，包含所有字段。

### 代码示例

#### Diary.java 日记实体
```java
package com.moodnote.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mood_diary")
public class Diary extends BaseEntity {
    
    /**
     * 日记标题
     */
    private String title;

    /**
     * 正文内容
     */
    private String content;

    /**
     * 心情类型：1开心/2平静/3难过/4焦虑/5生气
     */
    private Integer moodType;

    /**
     * 天气类型：1晴/2多云/3阴/4雨/5雪
     */
    private Integer weatherType;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 是否私密：0公开/1私密
     */
    private Integer isPrivate;
}
```

## 验收标准
1. 日记实体类创建成功
2. 继承了 BaseEntity 基类
3. 配置了正确的表名注解
4. 包含了所有必要的字段
5. 字段类型与数据库表一致

## 关联 Skill
前置：Skill-011-创建BaseEntity基类

后置：Skill-013-创建标签实体