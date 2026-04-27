# Skill: 创建日记标签关联Mapper

## 触发条件
当需要创建日记-标签关联表的 Mapper 接口时。

## 前置依赖
- Skill-018-创建标签Mapper

## 执行规范

### 文件位置
- Mapper 接口：`moodnote-mapper/src/main/java/com/moodnote/mapper/`

### 命名规范
- Mapper 接口：`DiaryTagMapper.java`

### 代码规范
- 继承 BaseMapper 接口
- 定义必要的关联操作方法

### 依赖引入
- MyBatis-Plus 依赖

## 代码模板

### 模板说明
创建日记-标签关联表的 Mapper 接口，包含关联关系的操作方法。

### 代码示例

#### DiaryTagMapper.java 日记标签关联 Mapper 接口
```java
package com.moodnote.mapper;

import com.moodnote.pojo.entity.DiaryTag;
import org.apache.ibatis.annotations.Param;

public interface DiaryTagMapper extends BaseMapper<DiaryTag> {
    
    /**
     * 删除日记的所有标签关联
     */
    void deleteByDiaryId(@Param("diaryId") Long diaryId);
}
```

## 验收标准
1. 日记标签关联 Mapper 接口创建成功
2. 继承了 BaseMapper 接口
3. 包含了删除日记标签关联的方法
4. 方法定义正确

## 关联 Skill
前置：Skill-018-创建标签Mapper

后置：Skill-020-创建日记Service接口与实现