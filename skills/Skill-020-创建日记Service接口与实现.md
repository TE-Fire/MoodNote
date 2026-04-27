# Skill: 创建日记Service接口与实现

## 触发条件
当需要创建日记业务逻辑的 Service 接口和实现类时。

## 前置依赖
- Skill-019-创建日记标签关联Mapper

## 执行规范

### 文件位置
- Service 接口：`moodnote-service/src/main/java/com/moodnote/service/`
- Service 实现：`moodnote-service/src/main/java/com/moodnote/service/impl/`

### 命名规范
- Service 接口：`DiaryService.java`
- Service 实现：`DiaryServiceImpl.java`

### 代码规范
- 接口定义业务方法
- 实现类包含具体业务逻辑
- 使用 @Service 注解标记实现类
- 使用 @Autowired 注入 Mapper

### 依赖引入
- MyBatis-Plus 依赖
- MapStruct 依赖

## 代码模板

### 模板说明
创建日记业务逻辑的 Service 接口和实现类，包含日记的 CRUD 操作和标签关联处理。

### 代码示例

#### 1. DiaryService.java 日记 Service 接口
```java
package com.moodnote.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moodnote.pojo.dto.DiaryCreateDTO;
import com.moodnote.pojo.dto.DiaryUpdateDTO;
import com.moodnote.pojo.vo.DiaryListVO;
import com.moodnote.pojo.vo.DiaryVO;

import java.util.List;

public interface DiaryService {
    
    /**
     * 创建日记
     */
    Long createDiary(DiaryCreateDTO dto);

    /**
     * 更新日记
     */
    void updateDiary(Long id, DiaryUpdateDTO dto);

    /**
     * 删除日记（软删除）
     */
    void deleteDiary(Long id);

    /**
     * 查询日记详情
     */
    DiaryVO getDiaryById(Long id);

    /**
     * 分页查询日记列表
     */
    Page<DiaryListVO> getDiaryList(int page, int pageSize, Integer moodType, String startDate, String endDate, String keyword, Long tagId);
}
```

#### 2. DiaryServiceImpl.java 日记 Service 实现类
```java
package com.moodnote.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moodnote.exception.BusinessException;
import com.moodnote.exception.ErrorCode;
import com.moodnote.mapper.DiaryMapper;
import com.moodnote.mapper.DiaryTagMapper;
import com.moodnote.pojo.dto.DiaryCreateDTO;
import com.moodnote.pojo.dto.DiaryUpdateDTO;
import com.moodnote.pojo.entity.Diary;
import com.moodnote.pojo.entity.DiaryTag;
import com.moodnote.pojo.vo.DiaryListVO;
import com.moodnote.pojo.vo.DiaryVO;
import com.moodnote.pojo.vo.TagVO;
import com.moodnote.service.DiaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DiaryServiceImpl implements DiaryService {
    
    @Resource
    private DiaryMapper diaryMapper;

    @Resource
    private DiaryTagMapper diaryTagMapper;

    @Override
    @Transactional
    public Long createDiary(DiaryCreateDTO dto) {
        // 创建日记
        Diary diary = new Diary();
        BeanUtils.copyProperties(dto, diary);
        diary.setCreateTime(LocalDateTime.now());
        diary.setUpdateTime(LocalDateTime.now());
        diary.setDeleted(0);
        
        int result = diaryMapper.insert(diary);
        if (result != 1) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "创建日记失败");
        }
        
        // 关联标签
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            saveDiaryTags(diary.getId(), dto.getTagIds());
        }
        
        return diary.getId();
    }

    @Override
    @Transactional
    public void updateDiary(Long id, DiaryUpdateDTO dto) {
        // 检查日记是否存在
        Diary existingDiary = diaryMapper.selectById(id);
        if (existingDiary == null || existingDiary.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "日记不存在");
        }
        
        // 更新日记
        Diary diary = new Diary();
        BeanUtils.copyProperties(dto, diary);
        diary.setId(id);
        diary.setUpdateTime(LocalDateTime.now());
        
        int result = diaryMapper.updateById(diary);
        if (result != 1) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "更新日记失败");
        }
        
        // 更新标签关联
        diaryTagMapper.deleteByDiaryId(id);
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            saveDiaryTags(id, dto.getTagIds());
        }
    }

    @Override
    public void deleteDiary(Long id) {
        // 检查日记是否存在
        Diary existingDiary = diaryMapper.selectById(id);
        if (existingDiary == null || existingDiary.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "日记不存在");
        }
        
        // 软删除日记
        Diary diary = new Diary();
        diary.setId(id);
        diary.setDeleted(1);
        diary.setUpdateTime(LocalDateTime.now());
        
        int result = diaryMapper.updateById(diary);
        if (result != 1) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "删除日记失败");
        }
    }

    @Override
    public DiaryVO getDiaryById(Long id) {
        // 查询日记详情
        Diary diary = diaryMapper.selectDiaryById(id);
        if (diary == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "日记不存在");
        }
        
        // 查询关联的标签
        List<Long> tagIds = diaryMapper.selectTagIdsByDiaryId(id);
        
        // 构建返回对象
        DiaryVO vo = new DiaryVO();
        BeanUtils.copyProperties(diary, vo);
        
        // TODO: 填充标签信息
        
        return vo;
    }

    @Override
    public Page<DiaryListVO> getDiaryList(int page, int pageSize, Integer moodType, String startDate, String endDate, String keyword, Long tagId) {
        Page<DiaryListVO> pageInfo = new Page<>(page, pageSize);
        return diaryMapper.selectDiaryList(pageInfo, moodType, startDate, endDate, keyword, tagId);
    }

    /**
     * 保存日记标签关联
     */
    private void saveDiaryTags(Long diaryId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            DiaryTag diaryTag = new DiaryTag();
            diaryTag.setDiaryId(diaryId);
            diaryTag.setTagId(tagId);
            diaryTag.setCreateTime(LocalDateTime.now());
            diaryTag.setDeleted(0);
            diaryTagMapper.insert(diaryTag);
        }
    }
}
```

## 验收标准
1. 日记 Service 接口创建成功
2. 日记 Service 实现类创建成功
3. 包含了完整的 CRUD 操作
4. 实现了标签关联处理
5. 包含了异常处理

## 关联 Skill
前置：Skill-019-创建日记标签关联Mapper

后置：Skill-021-创建标签Service接口与实现