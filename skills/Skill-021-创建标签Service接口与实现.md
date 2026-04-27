# Skill: 创建标签Service接口与实现

## 触发条件
当需要创建标签业务逻辑的 Service 接口和实现类时。

## 前置依赖
- Skill-020-创建日记Service接口与实现

## 执行规范

### 文件位置
- Service 接口：`moodnote-service/src/main/java/com/moodnote/service/`
- Service 实现：`moodnote-service/src/main/java/com/moodnote/service/impl/`

### 命名规范
- Service 接口：`TagService.java`
- Service 实现：`TagServiceImpl.java`

### 代码规范
- 接口定义业务方法
- 实现类包含具体业务逻辑
- 使用 @Service 注解标记实现类
- 使用 @Autowired 注入 Mapper

### 依赖引入
- MyBatis-Plus 依赖

## 代码模板

### 模板说明
创建标签业务逻辑的 Service 接口和实现类，包含标签的 CRUD 操作和统计功能。

### 代码示例

#### 1. TagService.java 标签 Service 接口
```java
package com.moodnote.service;

import com.moodnote.pojo.dto.TagCreateDTO;
import com.moodnote.pojo.vo.TagVO;

import java.util.List;

public interface TagService {
    
    /**
     * 创建标签
     */
    Long createTag(TagCreateDTO dto);

    /**
     * 更新标签
     */
    void updateTag(Long id, TagCreateDTO dto);

    /**
     * 删除标签
     */
    void deleteTag(Long id);

    /**
     * 查询所有标签
     */
    List<TagVO> getAllTags();

    /**
     * 统计标签使用次数
     */
    List<TagVO> getTagUsageStats();
}
```

#### 2. TagServiceImpl.java 标签 Service 实现类
```java
package com.moodnote.service.impl;

import com.moodnote.exception.BusinessException;
import com.moodnote.exception.ErrorCode;
import com.moodnote.mapper.TagMapper;
import com.moodnote.pojo.dto.TagCreateDTO;
import com.moodnote.pojo.entity.Tag;
import com.moodnote.pojo.vo.TagVO;
import com.moodnote.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TagServiceImpl implements TagService {
    
    @Resource
    private TagMapper tagMapper;

    @Override
    public Long createTag(TagCreateDTO dto) {
        // 创建标签
        Tag tag = new Tag();
        BeanUtils.copyProperties(dto, tag);
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());
        tag.setDeleted(0);
        
        int result = tagMapper.insert(tag);
        if (result != 1) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "创建标签失败");
        }
        
        return tag.getId();
    }

    @Override
    public void updateTag(Long id, TagCreateDTO dto) {
        // 检查标签是否存在
        Tag existingTag = tagMapper.selectById(id);
        if (existingTag == null || existingTag.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "标签不存在");
        }
        
        // 更新标签
        Tag tag = new Tag();
        BeanUtils.copyProperties(dto, tag);
        tag.setId(id);
        tag.setUpdateTime(LocalDateTime.now());
        
        int result = tagMapper.updateById(tag);
        if (result != 1) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "更新标签失败");
        }
    }

    @Override
    public void deleteTag(Long id) {
        // 检查标签是否存在
        Tag existingTag = tagMapper.selectById(id);
        if (existingTag == null || existingTag.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "标签不存在");
        }
        
        // 检查标签是否被引用
        Integer count = tagMapper.countTagReferences(id);
        if (count > 0) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "标签正在被使用，无法删除");
        }
        
        // 软删除标签
        Tag tag = new Tag();
        tag.setId(id);
        tag.setDeleted(1);
        tag.setUpdateTime(LocalDateTime.now());
        
        int result = tagMapper.updateById(tag);
        if (result != 1) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "删除标签失败");
        }
    }

    @Override
    public List<TagVO> getAllTags() {
        List<Tag> tags = tagMapper.selectAllTags();
        return tags.stream().map(tag -> {
            TagVO vo = new TagVO();
            BeanUtils.copyProperties(tag, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TagVO> getTagUsageStats() {
        List<Tag> tags = tagMapper.selectTagUsageStats();
        return tags.stream().map(tag -> {
            TagVO vo = new TagVO();
            BeanUtils.copyProperties(tag, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
```

## 验收标准
1. 标签 Service 接口创建成功
2. 标签 Service 实现类创建成功
3. 包含了完整的 CRUD 操作
4. 实现了标签使用统计功能
5. 包含了异常处理

## 关联 Skill
前置：Skill-020-创建日记Service接口与实现

后置：Skill-022-创建统计Service接口与实现