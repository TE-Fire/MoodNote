# Skill: 创建DTO和VO对象

## 触发条件
当需要创建数据传输对象（DTO）和视图对象（VO）时，用于前后端数据交互。

## 前置依赖
- Skill-014-创建日记-标签关联实体

## 执行规范

### 文件位置
- DTO 文件：`moodnote-pojo/src/main/java/com/moodnote/pojo/dto/`
- VO 文件：`moodnote-pojo/src/main/java/com/moodnote/pojo/vo/`

### 命名规范
- DTO 命名：`{业务名称}{操作}DTO.java`
- VO 命名：`{业务名称}VO.java`

### 代码规范
- 使用 Lombok 注解简化代码
- 使用 Jakarta Validation 进行参数校验
- 字段命名与前端保持一致

### 依赖引入
- Lombok 依赖
- Jakarta Validation 依赖

## 代码模板

### 模板说明
创建用于数据传输的 DTO 对象和用于前端展示的 VO 对象。

### 代码示例

#### 1. 日记相关 DTO

##### DiaryCreateDTO.java 创建日记请求体
```java
package com.moodnote.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class DiaryCreateDTO {
    
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private Integer moodType;

    private Integer weatherType;

    private String city;

    private Integer isPrivate;

    private List<Long> tagIds;
}
```

##### DiaryUpdateDTO.java 更新日记请求体
```java
package com.moodnote.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class DiaryUpdateDTO {
    
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private Integer moodType;

    private Integer weatherType;

    private String city;

    private Integer isPrivate;

    private List<Long> tagIds;
}
```

##### DiaryQueryDTO.java 日记查询条件
```java
package com.moodnote.pojo.dto;

import lombok.Data;

@Data
public class DiaryQueryDTO {
    
    private Integer page = 1;

    private Integer pageSize = 10;

    private Integer moodType;

    private String startDate;

    private String endDate;

    private String keyword;

    private Long tagId;
}
```

#### 2. 标签相关 DTO

##### TagCreateDTO.java 创建标签请求体
```java
package com.moodnote.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TagCreateDTO {
    
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 20, message = "标签名称长度不能超过20个字符")
    private String name;

    private String color;
}
```

#### 3. 视图对象 VO

##### DiaryVO.java 日记详情视图
```java
package com.moodnote.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DiaryVO {
    
    private Long id;

    private String title;

    private String content;

    private Integer moodType;

    private Integer weatherType;

    private String city;

    private Integer isPrivate;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<TagVO> tags;
}
```

##### DiaryListVO.java 日记列表项视图
```java
package com.moodnote.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DiaryListVO {
    
    private Long id;

    private String title;

    private String content;

    private Integer moodType;

    private Integer weatherType;

    private String city;

    private LocalDateTime createTime;

    private List<TagVO> tags;
}
```

##### TagVO.java 标签视图
```java
package com.moodnote.pojo.vo;

import lombok.Data;

@Data
public class TagVO {
    
    private Long id;

    private String name;

    private String color;
}
```

##### MoodStatsVO.java 心情统计视图
```java
package com.moodnote.pojo.vo;

import lombok.Data;

@Data
public class MoodStatsVO {
    
    private Integer moodType;

    private Long count;

    private String date;
}
```

## 验收标准
1. 所有 DTO 和 VO 对象创建成功
2. 包含了必要的字段
3. 使用了参数校验注解
4. 字段命名与前端保持一致
5. 结构清晰，职责明确

## 关联 Skill
前置：Skill-014-创建日记-标签关联实体

后置：Skill-016-创建BaseMapper