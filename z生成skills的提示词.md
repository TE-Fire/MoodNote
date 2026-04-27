# Skills 生成提示词

## 角色定义
你是一个全栈项目 Skill 拆解专家。请基于下面提供的 MoodNote 项目 WorkFlow，将项目开发过程拆解为多个原子化的 Skill 文件，每个 Skill 对应一个可独立执行的具体开发任务。

## 项目 WorkFlow 概要

### 项目信息
- **项目名称：** MoodNote（晚风记事）
- **项目类型：** 个人心情日记 Web 应用
- **后端技术：** Spring Boot 3（JDK 21）+ Maven 多模块
- **前端技术：** Vue 3 + Vite
- **数据库：** MySQL + 逻辑外键
- **后续扩展：** Spring AI Agent 集成

### 模块命名规范
| 模块 | 命名 | 职责 |
|------|------|------|
| 父工程 | moodnote-parent | Maven 依赖管理与模块聚合 |
| 公共模块 | moodnote-common | 工具类、全局异常、常量枚举、基础配置 |
| 实体模块 | moodnote-pojo | Entity、DTO、VO、Query 对象 |
| 数据访问 | moodnote-mapper | Mapper 接口与 MyBatis XML |
| 业务逻辑 | moodnote-service | Service 接口与实现、对象转换 |
| 启动模块 | moodnote-server | Spring Boot 启动、Controller |
| 前端工程 | moodnote-web | Vue 3 项目 |

### 数据库规范
- 使用 MySQL 数据库
- 所有表关联使用逻辑外键（不使用物理外键约束）
- 所有表必须包含字段：id（主键）、create_time、update_time、deleted（软删除标记）
- 表名前缀统一为 mood_

### API 规范
- RESTful 风格
- 统一路径前缀 /api/v1/
- 统一返回格式 Result<T>（包含 code、message、data）
- 分页查询统一使用 PageResult<T>

### 前端规范
- API 请求封装在 src/api/ 目录下
- 使用 Axios + 拦截器统一处理
- 状态管理使用 Pinia
- 路由使用 Vue Router 4
- 组件按 common/layout/business 三级分类

---

## Skill 生成要求

### 1. 原子化原则
- 每个 Skill 只负责一个明确的开发任务
- Skill 之间可以存在依赖关系，但尽量保持独立
- 每个 Skill 必须包含：触发条件、前置依赖、执行规范、代码模板、验收标准

### 2. Skill 分类维度

按照以下分类生成 Skill：

**阶段一：项目初始化**
- 创建 Maven 父工程
- 创建各子模块
- 初始化 Vue 3 项目
- 配置基础依赖

**阶段二：基础设施搭建**
- 全局异常处理
- 统一返回格式
- 数据库连接配置
- MyBatis-Plus 配置
- 跨域配置
- 日志配置

**阶段三：数据库实体**
- 创建 BaseEntity 基类
- 创建日记实体（mood_diary）
- 创建标签实体（mood_tag）
- 创建日记-标签关联实体（mood_diary_tag）

**阶段四：数据访问层**
- 创建 BaseMapper
- 创建日记 Mapper
- 创建标签 Mapper

**阶段五：业务逻辑层**
- 日记 Service 接口与实现
- 标签 Service 接口与实现
- 统计 Service 接口与实现

**阶段六：API 接口层**
- 日记 CRUD 接口
- 标签接口
- 统计接口
- 参数校验
- 分页查询封装

**阶段七：前端基础**
- Axios 封装与拦截器
- 路由配置
- Pinia 状态管理
- 全局组件注册

**阶段八：前端页面**
- 日记列表页
- 日记创建/编辑页
- 日记详情页
- 心情日历页

---

### 3. Skill 文件模板

每个 Skill 文件必须严格遵循以下 Markdown 格式：

```markdown
# Skill: [Skill 名称]

## 触发条件
[描述什么情况下使用此 Skill，例如："当用户要求创建新的数据库实体类时"]

## 前置依赖
- [依赖的 Skill 名称或已完成的模块]
- [例如：Skill-003: BaseEntity 基类已创建]

## 执行规范

### 文件位置
- [明确指出代码文件应该创建在哪个模块的哪个包下]

### 命名规范
- [类名、方法名、变量名的命名规则]

### 代码规范
- [必须遵循的编码规范]

### 依赖引入
- [需要引入的 Maven 依赖或 npm 包]

## 代码模板

### 模板说明
[模板的使用说明和可变部分标注]

### 代码示例
```[语言]
// 具体的代码模板，使用占位符标记可变部分
// 例如：将实体名称用 {{EntityName}} 表示
验收标准
[可验证的完成标准 1]

[可验证的完成标准 2]

[可验证的完成标准 3]

关联 Skill
前置：[依赖的前置 Skill]

后置：[此 Skill 完成后可执行的 Skill]

text

---

## 输出要求

1. 按照上述 8 个阶段的顺序，逐一生成每个阶段的 Skill 文件
2. 每个 Skill 保存为独立的 Markdown 文件
3. Skill 文件命名格式：`Skill-{序号}-{名称}.md`，例如 `Skill-001-创建Maven父工程.md`
4. 所有 Skill 文件统一放在 `skills/` 目录下
5. 额外生成一个 `skills/README.md`，列出所有 Skill 的索引及依赖关系树

## 特别注意

- 所有代码模板必须使用 JDK 21 语法特性
- 后端使用 Spring Boot 3.x 版本
- 数据库操作使用 MyBatis-Plus
- 对象转换使用 MapStruct
- 参数校验使用 Jakarta Validation
- 前端使用 Composition API（`<script setup>`）
- 前端 CSS 方案使用原生 CSS 或 SCSS，不使用 Tailwind
- 为 Spring AI 集成预留扩展点（在 Service 层设计接口时考虑 AI 增强场景）
- 所有 Skill 生成完毕后，检查依赖关系是否完整，确保不存在断链
使用说明
将上述提示词配合你的完整 WorkFlow.md 一起提供给 Trae IDE 的 AI 助手，即可批量生成结构化的 skills/ 目录，包含所有原子化的开发 Skill 文件。

后续流程：

text
WorkFlow.md（项目规范）
      +
Skill 生成提示词（本文档）
      │
      ▼
  AI 批量生成
      │
      ▼
skills/ 目录
├── README.md（Skill 索引与依赖树）
├── Skill-001-创建Maven父工程.md
├── Skill-002-创建子模块结构.md
├── Skill-003-Vue3项目初始化.md
├── ...
└── Skill-0xx-心情日历页面开发.md
      │
      ▼
  导入 Trae IDE
      │
      ▼
  AI 按 Skill 逐一执行编码