# Skill: 创建标签Controller

## 触发条件
当需要创建标签相关的 RESTful API 接口时。

## 前置依赖
- Skill-023-创建日记Controller

## 执行规范

### 文件位置
- Controller 文件：`moodnote-server/src/main/java/com/moodnote/controller/`

### 命名规范
- Controller 名称：`TagController.java`
- 路径前缀：`/api/v1/tags`

### 代码规范
- 使用 @RestController 注解
- 使用 @RequestMapping 注解定义路径
- 使用 @PostMapping、@GetMapping、@PutMapping、@DeleteMapping 注解定义请求方法
- 使用 @PathVariable 注解获取路径参数
- 使用 @Validated 注解进行参数校验

### 依赖引入
- Spring Web 依赖
- Jakarta Validation 依赖

## 代码模板

### 模板说明
创建标签相关的 RESTful API 接口，包含创建、查询、更新、删除等操作。

### 代码示例

#### TagController.java 标签 Controller
```java
package com.moodnote.controller;

import com.moodnote.common.utils.Result;
import com.moodnote.pojo.dto.TagCreateDTO;
import com.moodnote.pojo.vo.TagVO;
import com.moodnote.service.TagService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
public class TagController {
    
    @Resource
    private TagService tagService;

    /**
     * 创建标签
     */
    @PostMapping
    public Result<Long> createTag(@Valid @RequestBody TagCreateDTO dto) {
        log.info("Create tag: {}", dto.getName());
        Long id = tagService.createTag(dto);
        return Result.success(id);
    }

    /**
     * 查询标签列表
     */
    @GetMapping
    public Result<List<TagVO>> getTagList() {
        log.info("Get tag list");
        List<TagVO> tags = tagService.getAllTags();
        return Result.success(tags);
    }

    /**
     * 更新标签
     */
    @PutMapping("/{id}")
    public Result<?> updateTag(@PathVariable Long id, @Valid @RequestBody TagCreateDTO dto) {
        log.info("Update tag: {}", id);
        tagService.updateTag(id, dto);
        return Result.success();
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteTag(@PathVariable Long id) {
        log.info("Delete tag: {}", id);
        tagService.deleteTag(id);
        return Result.success();
    }

    /**
     * 统计标签使用次数
     */
    @GetMapping("/stats")
    public Result<List<TagVO>> getTagUsageStats() {
        log.info("Get tag usage stats");
        List<TagVO> stats = tagService.getTagUsageStats();
        return Result.success(stats);
    }
}
```

## 验收标准
1. 标签 Controller 创建成功
2. 包含了完整的 CRUD 接口
3. 使用了正确的 HTTP 方法
4. 包含了标签使用统计接口
5. 包含了参数校验
6. 日志记录完整

## 关联 Skill
前置：Skill-023-创建日记Controller

后置：Skill-025-创建统计Controller