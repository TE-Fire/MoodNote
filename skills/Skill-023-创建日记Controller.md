# Skill: 创建日记Controller

## 触发条件
当需要创建日记相关的 RESTful API 接口时。

## 前置依赖
- Skill-022-创建统计Service接口与实现

## 执行规范

### 文件位置
- Controller 文件：`moodnote-server/src/main/java/com/moodnote/controller/`

### 命名规范
- Controller 名称：`DiaryController.java`
- 路径前缀：`/api/v1/diaries`

### 代码规范
- 使用 @RestController 注解
- 使用 @RequestMapping 注解定义路径
- 使用 @PostMapping、@GetMapping、@PutMapping、@DeleteMapping 注解定义请求方法
- 使用 @RequestParam 注解获取查询参数
- 使用 @PathVariable 注解获取路径参数
- 使用 @Validated 注解进行参数校验

### 依赖引入
- Spring Web 依赖
- Jakarta Validation 依赖

## 代码模板

### 模板说明
创建日记相关的 RESTful API 接口，包含创建、查询、更新、删除等操作。

### 代码示例

#### DiaryController.java 日记 Controller
```java
package com.moodnote.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moodnote.common.utils.Result;
import com.moodnote.common.utils.PageResult;
import com.moodnote.pojo.dto.DiaryCreateDTO;
import com.moodnote.pojo.dto.DiaryUpdateDTO;
import com.moodnote.pojo.vo.DiaryListVO;
import com.moodnote.pojo.vo.DiaryVO;
import com.moodnote.service.DiaryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/v1/diaries")
public class DiaryController {
    
    @Resource
    private DiaryService diaryService;

    /**
     * 创建日记
     */
    @PostMapping
    public Result<Long> createDiary(@Valid @RequestBody DiaryCreateDTO dto) {
        log.info("Create diary: {}", dto.getTitle());
        Long id = diaryService.createDiary(dto);
        return Result.success(id);
    }

    /**
     * 分页查询日记列表
     */
    @GetMapping
    public Result<PageResult<DiaryListVO>> getDiaryList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer moodType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long tagId) {
        log.info("Get diary list, page: {}, pageSize: {}", page, pageSize);
        Page<DiaryListVO> pageInfo = diaryService.getDiaryList(page, pageSize, moodType, startDate, endDate, keyword, tagId);
        PageResult<DiaryListVO> result = PageResult.build(
                pageInfo.getTotal(),
                (int) pageInfo.getSize(),
                (int) pageInfo.getCurrent(),
                pageInfo.getRecords()
        );
        return Result.success(result);
    }

    /**
     * 查询日记详情
     */
    @GetMapping("/{id}")
    public Result<DiaryVO> getDiaryById(@PathVariable Long id) {
        log.info("Get diary by id: {}", id);
        DiaryVO diary = diaryService.getDiaryById(id);
        return Result.success(diary);
    }

    /**
     * 更新日记
     */
    @PutMapping("/{id}")
    public Result<?> updateDiary(@PathVariable Long id, @Valid @RequestBody DiaryUpdateDTO dto) {
        log.info("Update diary: {}", id);
        diaryService.updateDiary(id, dto);
        return Result.success();
    }

    /**
     * 删除日记（软删除）
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteDiary(@PathVariable Long id) {
        log.info("Delete diary: {}", id);
        diaryService.deleteDiary(id);
        return Result.success();
    }
}
```

## 验收标准
1. 日记 Controller 创建成功
2. 包含了完整的 CRUD 接口
3. 使用了正确的 HTTP 方法
4. 实现了分页查询
5. 包含了参数校验
6. 日志记录完整

## 关联 Skill
前置：Skill-022-创建统计Service接口与实现

后置：Skill-024-创建标签Controller