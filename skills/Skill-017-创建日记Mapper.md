# Skill: 创建日记Mapper

## 触发条件
当需要创建日记表的 Mapper 接口和 XML 映射文件时。

## 前置依赖
- Skill-016-创建BaseMapper

## 执行规范

### 文件位置
- Mapper 接口：`moodnote-mapper/src/main/java/com/moodnote/mapper/`
- XML 映射文件：`moodnote-mapper/src/main/resources/mapper/`

### 命名规范
- Mapper 接口：`DiaryMapper.java`
- XML 映射文件：`DiaryMapper.xml`

### 代码规范
- 继承 BaseMapper 接口
- 定义必要的查询方法
- XML 文件中配置复杂的 SQL 语句

### 依赖引入
- MyBatis-Plus 依赖

## 代码模板

### 模板说明
创建日记表的 Mapper 接口和 XML 映射文件，包含基本的 CRUD 操作和复杂查询。

### 代码示例

#### 1. DiaryMapper.java 日记 Mapper 接口
```java
package com.moodnote.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moodnote.pojo.entity.Diary;
import com.moodnote.pojo.vo.DiaryListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DiaryMapper extends BaseMapper<Diary> {
    
    /**
     * 分页查询日记列表
     */
    IPage<DiaryListVO> selectDiaryList(Page<DiaryListVO> page,
                                       @Param("moodType") Integer moodType,
                                       @Param("startDate") String startDate,
                                       @Param("endDate") String endDate,
                                       @Param("keyword") String keyword,
                                       @Param("tagId") Long tagId);

    /**
     * 查询日记详情
     */
    Diary selectDiaryById(@Param("id") Long id);

    /**
     * 查询日记关联的标签
     */
    List<Long> selectTagIdsByDiaryId(@Param("diaryId") Long diaryId);
}
```

#### 2. DiaryMapper.xml 日记 XML 映射文件
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.moodnote.mapper.DiaryMapper">
    
    <resultMap id="DiaryListVOMap" type="com.moodnote.pojo.vo.DiaryListVO">
        <id property="id" column="id"/>
        <result property="title" column="title"/>
        <result property="content" column="content"/>
        <result property="moodType" column="mood_type"/>
        <result property="weatherType" column="weather_type"/>
        <result property="city" column="city"/>
        <result property="createTime" column="create_time"/>
        <collection property="tags" ofType="com.moodnote.pojo.vo.TagVO">
            <id property="id" column="tag_id"/>
            <result property="name" column="tag_name"/>
            <result property="color" column="tag_color"/>
        </collection>
    </resultMap>
    
    <select id="selectDiaryList" resultMap="DiaryListVOMap">
        SELECT 
            d.id, 
            d.title, 
            d.content, 
            d.mood_type, 
            d.weather_type, 
            d.city, 
            d.create_time, 
            t.id as tag_id, 
            t.name as tag_name, 
            t.color as tag_color
        FROM 
            mood_diary d
        LEFT JOIN 
            mood_diary_tag dt ON d.id = dt.diary_id AND dt.deleted = 0
        LEFT JOIN 
            mood_tag t ON dt.tag_id = t.id AND t.deleted = 0
        WHERE 
            d.deleted = 0
        <if test="moodType != null">
            AND d.mood_type = #{moodType}
        </if>
        <if test="startDate != null">
            AND d.create_time >= #{startDate}
        </if>
        <if test="endDate != null">
            AND d.create_time <= #{endDate}
        </if>
        <if test="keyword != null and keyword != ''">
            AND (d.title LIKE CONCAT('%', #{keyword}, '%') OR d.content LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        <if test="tagId != null">
            AND dt.tag_id = #{tagId}
        </if>
        GROUP BY 
            d.id
        ORDER BY 
            d.create_time DESC
    </select>
    
    <select id="selectDiaryById" resultType="com.moodnote.pojo.entity.Diary">
        SELECT 
            * 
        FROM 
            mood_diary 
        WHERE 
            id = #{id} 
            AND deleted = 0
    </select>
    
    <select id="selectTagIdsByDiaryId" resultType="java.lang.Long">
        SELECT 
            tag_id 
        FROM 
            mood_diary_tag 
        WHERE 
            diary_id = #{diaryId} 
            AND deleted = 0
    </select>
</mapper>
```

## 验收标准
1. 日记 Mapper 接口创建成功
2. 日记 XML 映射文件创建成功
3. 包含了必要的查询方法
4. SQL 语句正确，包含了软删除条件
5. 关联查询配置正确

## 关联 Skill
前置：Skill-016-创建BaseMapper

后置：Skill-018-创建标签Mapper