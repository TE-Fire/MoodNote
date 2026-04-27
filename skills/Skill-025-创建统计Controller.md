# Skill: 创建统计Controller

## 触发条件
当需要创建数据统计相关的 RESTful API 接口时。

## 前置依赖
- Skill-024-创建标签Controller

## 执行规范

### 文件位置
- Controller 文件：`moodnote-server/src/main/java/com/moodnote/controller/`

### 命名规范
- Controller 名称：`StatsController.java`
- 路径前缀：`/api/v1/stats`

### 代码规范
- 使用 @RestController 注解
- 使用 @RequestMapping 注解定义路径
- 使用 @GetMapping 注解定义请求方法
- 使用 @RequestParam 注解获取查询参数

### 依赖引入
- Spring Web 依赖

## 代码模板

### 模板说明
创建数据统计相关的 RESTful API 接口，包含心情日历、情绪趋势和标签统计功能。

### 代码示例

#### StatsController.java 统计 Controller
```java
package com.moodnote.controller;

import com.moodnote.common.utils.Result;
import com.moodnote.pojo.vo.MoodStatsVO;
import com.moodnote.service.StatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/stats")
public class StatsController {
    
    @Resource
    private StatsService statsService;

    /**
     * 获取心情日历数据
     */
    @GetMapping("/calendar")
    public Result<Map<String, Integer>> getMoodCalendarData(
            @RequestParam int year,
            @RequestParam int month) {
        log.info("Get mood calendar data: {}-{}", year, month);
        Map<String, Integer> calendarData = statsService.getMoodCalendarData(year, month);
        return Result.success(calendarData);
    }

    /**
     * 获取情绪趋势数据
     */
    @GetMapping("/trend")
    public Result<List<MoodStatsVO>> getMoodTrendData(
            @RequestParam String type,
            @RequestParam(required = false) String date) {
        log.info("Get mood trend data, type: {}", type);
        List<MoodStatsVO> trendData = statsService.getMoodTrendData(type, date);
        return Result.success(trendData);
    }

    /**
     * 获取标签使用统计
     */
    @GetMapping("/tags")
    public Result<Map<String, Object>> getTagStats() {
        log.info("Get tag stats");
        Map<String, Object> tagStats = statsService.getTagStats();
        return Result.success(tagStats);
    }
}
```

## 验收标准
1. 统计 Controller 创建成功
2. 包含了心情日历、情绪趋势和标签统计接口
3. 使用了正确的 HTTP 方法
4. 包含了必要的查询参数
5. 日志记录完整

## 关联 Skill
前置：Skill-024-创建标签Controller

后置：Skill-026-创建SpringBoot启动类