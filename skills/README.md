# MoodNote 项目 Skill 索引

## 项目简介
MoodNote（晚风记事）是一个面向个人的轻量级心情日记记录 Web 应用，基于 Spring Boot 3（JDK 21）和 Vue 3 开发。

## Skill 列表

### 阶段一：项目初始化
1. **Skill-001-创建Maven父工程**
   - 描述：创建 Maven 父工程，配置基础依赖和模块聚合
   - 前置：无
   - 后置：Skill-002-创建子模块结构

2. **Skill-002-创建子模块结构**
   - 描述：创建各个子模块的目录结构和基础 POM 文件
   - 前置：Skill-001-创建Maven父工程
   - 后置：Skill-003-Vue3项目初始化

3. **Skill-003-Vue3项目初始化**
   - 描述：使用 Vite 初始化 Vue 3 项目，配置基础依赖
   - 前置：Skill-002-创建子模块结构
   - 后置：Skill-004-配置基础依赖

4. **Skill-004-配置基础依赖**
   - 描述：配置前后端的基础依赖和配置文件
   - 前置：Skill-003-Vue3项目初始化
   - 后置：Skill-035-创建用户实体

### 阶段二：用户认证系统
5. **Skill-035-创建用户实体**
   - 描述：创建用户表对应的实体类
   - 前置：Skill-011-创建BaseEntity基类
   - 后置：Skill-036-创建用户DTO和VO对象

6. **Skill-036-创建用户DTO和VO对象**
   - 描述：创建用户相关的数据传输对象和视图对象
   - 前置：Skill-035-创建用户实体
   - 后置：Skill-037-创建用户Mapper

7. **Skill-037-创建用户Mapper**
   - 描述：创建用户表的 Mapper 接口
   - 前置：Skill-036-创建用户DTO和VO对象
   - 后置：Skill-038-创建认证Service接口与实现

8. **Skill-038-创建认证Service接口与实现**
   - 描述：创建用户认证相关的业务逻辑
   - 前置：Skill-037-创建用户Mapper
   - 后置：Skill-039-创建认证Controller

9. **Skill-039-创建认证Controller**
   - 描述：创建用户认证相关的 RESTful API 接口
   - 前置：Skill-038-创建认证Service接口与实现
   - 后置：Skill-040-SpringSecurity配置

10. **Skill-040-SpringSecurity配置**
    - 描述：配置 Spring Security 进行认证和授权
    - 前置：Skill-039-创建认证Controller
    - 后置：Skill-041-前端登录注册页面

11. **Skill-041-前端登录注册页面**
    - 描述：创建前端登录、注册和个人主页页面
    - 前置：Skill-040-SpringSecurity配置
    - 后置：Skill-005-全局异常处理

### 阶段三：基础设施搭建
12. **Skill-005-全局异常处理**
   - 描述：创建全局异常处理器和相关的异常类
   - 前置：Skill-041-前端登录注册页面
   - 后置：Skill-006-统一返回格式

13. **Skill-006-统一返回格式**
   - 描述：创建统一的返回结果封装类
   - 前置：Skill-005-全局异常处理
   - 后置：Skill-007-数据库连接配置

14. **Skill-007-数据库连接配置**
   - 描述：配置数据库连接和初始化脚本
   - 前置：Skill-006-统一返回格式
   - 后置：Skill-008-MyBatis-Plus配置

15. **Skill-008-MyBatis-Plus配置**
   - 描述：配置 MyBatis-Plus 插件和相关设置
   - 前置：Skill-007-数据库连接配置
   - 后置：Skill-009-跨域配置

16. **Skill-009-跨域配置**
   - 描述：配置跨域支持，允许前端应用访问后端 API
   - 前置：Skill-008-MyBatis-Plus配置
   - 后置：Skill-010-日志配置

17. **Skill-010-日志配置**
    - 描述：配置 Logback 日志系统
    - 前置：Skill-009-跨域配置
    - 后置：Skill-011-创建BaseEntity基类

### 阶段四：数据库实体
18. **Skill-011-创建BaseEntity基类**
    - 描述：创建数据库实体的基类，包含公共字段
    - 前置：Skill-010-日志配置
    - 后置：Skill-012-创建日记实体

19. **Skill-012-创建日记实体**
    - 描述：创建日记表对应的实体类
    - 前置：Skill-011-创建BaseEntity基类
    - 后置：Skill-013-创建标签实体

20. **Skill-013-创建标签实体**
    - 描述：创建标签表对应的实体类
    - 前置：Skill-012-创建日记实体
    - 后置：Skill-014-创建日记-标签关联实体

21. **Skill-014-创建日记-标签关联实体**
    - 描述：创建日记-标签关联表对应的实体类
    - 前置：Skill-013-创建标签实体
    - 后置：Skill-015-创建DTO和VO对象

22. **Skill-015-创建DTO和VO对象**
    - 描述：创建数据传输对象和视图对象
    - 前置：Skill-014-创建日记-标签关联实体
    - 后置：Skill-016-创建BaseMapper

### 阶段五：数据访问层
23. **Skill-016-创建BaseMapper**
    - 描述：创建 Mapper 接口的基类
    - 前置：Skill-015-创建DTO和VO对象
    - 后置：Skill-017-创建日记Mapper

24. **Skill-017-创建日记Mapper**
    - 描述：创建日记表的 Mapper 接口和 XML 映射文件
    - 前置：Skill-016-创建BaseMapper
    - 后置：Skill-018-创建标签Mapper

25. **Skill-018-创建标签Mapper**
    - 描述：创建标签表的 Mapper 接口和 XML 映射文件
    - 前置：Skill-017-创建日记Mapper
    - 后置：Skill-019-创建日记标签关联Mapper

26. **Skill-019-创建日记标签关联Mapper**
    - 描述：创建日记-标签关联表的 Mapper 接口
    - 前置：Skill-018-创建标签Mapper
    - 后置：Skill-020-创建日记Service接口与实现

### 阶段六：业务逻辑层
27. **Skill-020-创建日记Service接口与实现**
    - 描述：创建日记业务逻辑的 Service 接口和实现类
    - 前置：Skill-019-创建日记标签关联Mapper
    - 后置：Skill-021-创建标签Service接口与实现

28. **Skill-021-创建标签Service接口与实现**
    - 描述：创建标签业务逻辑的 Service 接口和实现类
    - 前置：Skill-020-创建日记Service接口与实现
    - 后置：Skill-022-创建统计Service接口与实现

29. **Skill-022-创建统计Service接口与实现**
    - 描述：创建数据统计业务逻辑的 Service 接口和实现类
    - 前置：Skill-021-创建标签Service接口与实现
    - 后置：Skill-023-创建日记Controller

### 阶段七：API接口层
30. **Skill-023-创建日记Controller**
    - 描述：创建日记相关的 RESTful API 接口
    - 前置：Skill-022-创建统计Service接口与实现
    - 后置：Skill-024-创建标签Controller

31. **Skill-024-创建标签Controller**
    - 描述：创建标签相关的 RESTful API 接口
    - 前置：Skill-023-创建日记Controller
    - 后置：Skill-025-创建统计Controller

32. **Skill-025-创建统计Controller**
    - 描述：创建数据统计相关的 RESTful API 接口
    - 前置：Skill-024-创建标签Controller
    - 后置：Skill-026-创建SpringBoot启动类

33. **Skill-026-创建SpringBoot启动类**
    - 描述：创建 Spring Boot 应用的启动类
    - 前置：Skill-025-创建统计Controller
    - 后置：Skill-027-Axios封装与拦截器

### 阶段八：前端基础
34. **Skill-027-Axios封装与拦截器**
    - 描述：封装 Axios 实例，配置拦截器
    - 前置：Skill-026-创建SpringBoot启动类
    - 后置：Skill-028-路由配置

35. **Skill-028-路由配置**
    - 描述：配置 Vue Router 路由
    - 前置：Skill-027-Axios封装与拦截器
    - 后置：Skill-029-Pinia状态管理

36. **Skill-029-Pinia状态管理**
    - 描述：创建 Pinia 状态管理 store
    - 前置：Skill-028-路由配置
    - 后置：Skill-030-全局组件注册

37. **Skill-030-全局组件注册**
    - 描述：创建通用基础组件并在全局注册
    - 前置：Skill-029-Pinia状态管理
    - 后置：Skill-031-日记列表页

### 阶段九：前端页面
38. **Skill-031-日记列表页**
    - 描述：创建日记列表页面，展示所有日记并支持筛选和分页
    - 前置：Skill-030-全局组件注册
    - 后置：Skill-032-日记创建编辑页

39. **Skill-032-日记创建编辑页**
    - 描述：创建日记创建和编辑页面
    - 前置：Skill-031-日记列表页
    - 后置：Skill-033-日记详情页

40. **Skill-033-日记详情页**
    - 描述：创建日记详情页面，展示单篇日记的完整内容
    - 前置：Skill-032-日记创建编辑页
    - 后置：Skill-034-心情日历页

41. **Skill-034-心情日历页**
    - 描述：创建心情日历页面，展示月度心情分布
    - 前置：Skill-033-日记详情页
    - 后置：无

## 依赖关系树

```
├── 阶段一：项目初始化
│   ├── Skill-001-创建Maven父工程
│   ├── Skill-002-创建子模块结构
│   ├── Skill-003-Vue3项目初始化
│   └── Skill-004-配置基础依赖
│
├── 阶段二：用户认证系统
│   ├── Skill-035-创建用户实体
│   ├── Skill-036-创建用户DTO和VO对象
│   ├── Skill-037-创建用户Mapper
│   ├── Skill-038-创建认证Service接口与实现
│   ├── Skill-039-创建认证Controller
│   ├── Skill-040-SpringSecurity配置
│   └── Skill-041-前端登录注册页面
│
├── 阶段三：基础设施搭建
│   ├── Skill-005-全局异常处理
│   ├── Skill-006-统一返回格式
│   ├── Skill-007-数据库连接配置
│   ├── Skill-008-MyBatis-Plus配置
│   ├── Skill-009-跨域配置
│   └── Skill-010-日志配置
│
├── 阶段四：数据库实体
│   ├── Skill-011-创建BaseEntity基类
│   ├── Skill-012-创建日记实体
│   ├── Skill-013-创建标签实体
│   ├── Skill-014-创建日记-标签关联实体
│   └── Skill-015-创建DTO和VO对象
│
├── 阶段五：数据访问层
│   ├── Skill-016-创建BaseMapper
│   ├── Skill-017-创建日记Mapper
│   ├── Skill-018-创建标签Mapper
│   └── Skill-019-创建日记标签关联Mapper
│
├── 阶段六：业务逻辑层
│   ├── Skill-020-创建日记Service接口与实现
│   ├── Skill-021-创建标签Service接口与实现
│   └── Skill-022-创建统计Service接口与实现
│
├── 阶段七：API接口层
│   ├── Skill-023-创建日记Controller
│   ├── Skill-024-创建标签Controller
│   ├── Skill-025-创建统计Controller
│   └── Skill-026-创建SpringBoot启动类
│
├── 阶段八：前端基础
│   ├── Skill-027-Axios封装与拦截器
│   ├── Skill-028-路由配置
│   ├── Skill-029-Pinia状态管理
│   └── Skill-030-全局组件注册
│
└── 阶段九：前端页面
    ├── Skill-031-日记列表页
    ├── Skill-032-日记创建编辑页
    ├── Skill-033-日记详情页
    └── Skill-034-心情日历页
```

## 使用说明
1. 按照 Skill 列表的顺序逐一执行
2. 每个 Skill 都包含了详细的执行规范和代码模板
3. 执行完成后，项目即可正常运行
4. 后续可根据需要集成 Spring AI Agent 功能

## 技术栈
- 后端：Spring Boot 3（JDK 21）、Maven、MyBatis-Plus、MySQL
- 前端：Vue 3、Vite、Vue Router、Pinia、Axios、ECharts
- 工具：Lombok、MapStruct、Knife4j、Hutool