# Skill: 创建标签Mapper

## 触发条件
当需要创建标签表的 Mapper 接口和 XML 映射文件时。

## 前置依赖
- Skill-017-创建日记Mapper

## 执行规范

### 文件位置
- Mapper 接口：`moodnote-mapper/src/main/java/com/moodnote/mapper/`
- XML 映射文件：`moodnote-mapper/src/main/resources/mapper/`

### 命名规范
- Mapper 接口：`TagMapper.java`
- XML 映射文件：`TagMapper.xml`

### 代码规范
- 继承 BaseMapper 接口
- 定义必要的查询方法
- XML 文件中配置复杂的 SQL 语句

### 依赖引入
- MyBatis-Plus 依赖

## 代码模板

### 模板说明
创建标签表的 Mapper 接口和 XML 映射文件，包含基本的 CRUD 操作和统计方法。

### 代码示例

#### 1. TagMapper.java 标签 Mapper 接口
```java
package com.moodnote.mapper;

import com.moodnote.pojo.entity.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {
    
    /**
     * 查询所有标签
     */
    List<Tag> selectAllTags();

    /**
     * 统计标签使用次数
     */
    List<Tag> selectTagUsageStats();

    /**
     * 检查标签是否被引用
     */
    Integer countTagReferences(@Param("tagId") Long tagId);
}
```

#### 2. TagMapper.xml 标签 XML 映射文件
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.moodnote.mapper.TagMapper">
    
    <select id="selectAllTags" resultType="com.moodnote.pojo.entity.Tag">
        SELECT 
            * 
        FROM 
            mood_tag 
        WHERE 
            deleted = 0
        ORDER BY 
            create_time DESC
    </select>
    
    <select id="selectTagUsageStats" resultType="com.moodnote.pojo.entity.Tag">
        SELECT 
            t.id, 
            t.name, 
            t.color, 
            COUNT(dt.id) as usage_count
        FROM 
            mood_tag t
        LEFT JOIN 
            mood_diary_tag dt ON t.id = dt.tag_id AND dt.deleted = 0
        WHERE 
            t.deleted = 0
        GROUP BY 
            t.id
        ORDER BY 
            usage_count DESC
    </select>
    
    <select id="countTagReferences" resultType="java.lang.Integer">
        SELECT 
            COUNT(*) 
        FROM 
            mood_diary_tag 
        WHERE 
            tag_id = #{tagId} 
            AND deleted = 0
    </select>
</mapper>
```

## 验收标准
1. 标签 Mapper 接口创建成功
2. 标签 XML 映射文件创建成功
3. 包含了必要的查询方法
4. SQL 语句正确，包含了软删除条件
5. 统计查询配置正确

## 关联 Skill
前置：Skill-017-创建日记Mapper

后置：Skill-019-创建日记标签关联Mapper