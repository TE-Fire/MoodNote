# Skill: 创建统计Service接口与实现

## 触发条件
当需要创建数据统计业务逻辑的 Service 接口和实现类时。

## 前置依赖
- Skill-021-创建标签Service接口与实现

## 执行规范

### 文件位置
- Service 接口：`moodnote-service/src/main/java/com/moodnote/service/`
- Service 实现：`moodnote-service/src/main/java/com/moodnote/service/impl/`

### 命名规范
- Service 接口：`StatsService.java`
- Service 实现：`StatsServiceImpl.java`

### 代码规范
- 接口定义统计方法
- 实现类包含具体统计逻辑
- 使用 @Service 注解标记实现类
- 使用 @Autowired 注入 Mapper

### 依赖引入
- MyBatis-Plus 依赖

## 代码模板

### 模板说明
创建数据统计业务逻辑的 Service 接口和实现类，包含心情日历、情绪趋势和标签统计功能。

### 代码示例

#### 1. StatsService.java 统计 Service 接口
```java
package com.moodnote.service;

import com.moodnote.pojo.vo.MoodStatsVO;

import java.util.List;
import java.util.Map;

public interface StatsService {
    
    /**
     * 获取心情日历数据
     */
    Map<String, Integer> getMoodCalendarData(int year, int month);

    /**
     * 获取情绪趋势数据
     */
    List<MoodStatsVO> getMoodTrendData(String type, String date);

    /**
     * 获取标签使用统计
     */
    Map<String, Object> getTagStats();
}
```

#### 2. StatsServiceImpl.java 统计 Service 实现类
```java
package com.moodnote.service.impl;

import com.moodnote.mapper.DiaryMapper;
import com.moodnote.mapper.TagMapper;
import com.moodnote.pojo.entity.Diary;
import com.moodnote.pojo.entity.Tag;
import com.moodnote.pojo.vo.MoodStatsVO;
import com.moodnote.service.StatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatsServiceImpl implements StatsService {
    
    @Resource
    private DiaryMapper diaryMapper;

    @Resource
    private TagMapper tagMapper;

    @Override
    public Map<String, Integer> getMoodCalendarData(int year, int month) {
        // 构建日期范围
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        // 查询该月的所有日记
        // TODO: 实现具体的查询逻辑
        
        // 模拟数据
        Map<String, Integer> calendarData = new HashMap<>();
        for (int day = 1; day <= endDate.getDayOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            // 随机生成心情类型 1-5
            calendarData.put(dateStr, new Random().nextInt(5) + 1);
        }
        
        return calendarData;
    }

    @Override
    public List<MoodStatsVO> getMoodTrendData(String type, String date) {
        // 解析日期
        LocalDate baseDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        LocalDate startDate;
        LocalDate endDate = baseDate;
        
        if ("week".equals(type)) {
            // 最近7天
            startDate = baseDate.minusDays(6);
        } else if ("month".equals(type)) {
            // 最近30天
            startDate = baseDate.minusDays(29);
        } else {
            // 默认最近7天
            startDate = baseDate.minusDays(6);
        }
        
        // 查询日期范围内的日记
        // TODO: 实现具体的查询逻辑
        
        // 模拟数据
        List<MoodStatsVO> trendData = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            MoodStatsVO stats = new MoodStatsVO();
            stats.setDate(currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            stats.setMoodType(new Random().nextInt(5) + 1);
            stats.setCount((long) (new Random().nextInt(3) + 1));
            trendData.add(stats);
            currentDate = currentDate.plusDays(1);
        }
        
        return trendData;
    }

    @Override
    public Map<String, Object> getTagStats() {
        // 查询标签使用统计
        List<Tag> tags = tagMapper.selectTagUsageStats();
        
        // 构建统计数据
        Map<String, Object> tagStats = new HashMap<>();
        List<Map<String, Object>> tagList = new ArrayList<>();
        
        for (Tag tag : tags) {
            Map<String, Object> tagMap = new HashMap<>();
            tagMap.put("id", tag.getId());
            tagMap.put("name", tag.getName());
            tagMap.put("color", tag.getColor());
            // TODO: 从查询结果中获取使用次数
            tagMap.put("usageCount", 0);
            tagList.add(tagMap);
        }
        
        tagStats.put("tags", tagList);
        tagStats.put("total", tagList.size());
        
        return tagStats;
    }
}
```

## 验收标准
1. 统计 Service 接口创建成功
2. 统计 Service 实现类创建成功
3. 包含了心情日历、情绪趋势和标签统计功能
4. 实现了不同时间维度的统计
5. 返回数据结构清晰

## 关联 Skill
前置：Skill-021-创建标签Service接口与实现

后置：Skill-023-创建日记Controller