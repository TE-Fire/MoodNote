# MoodNote - 晚风记事

> 一个轻量级的心情日记记录应用

## 🌟 项目简介

MoodNote 是一款基于 Spring Boot + Vue 3 构建的心情日记记录应用，旨在帮助用户记录每日心情、追踪情绪变化。

### ✨ 核心功能

- **用户认证**：支持邮箱注册、登录、密码重置
- **日记管理**：创建、编辑、删除日记记录
- **心情记录**：记录每日心情状态
- **数据统计**：心情趋势分析、日历视图
- **安全保障**：JWT 认证、密码加密、Token 黑名单机制

### 🔧 技术栈

| 分类 | 技术 | 版本 |
| :--- | :--- | :--- |
| 后端框架 | Spring Boot | 3.2.x |
| 前端框架 | Vue | 3.x |
| 数据库 | MySQL | 8.0+ |
| 缓存 | Redis | 7.0+ |
| 认证 | JWT | - |
| 构建工具 | Maven / Vite | - |

---

## 🚀 快速开始

### 环境要求

- **Java**: 21+
- **Node.js**: 20+
- **MySQL**: 8.0+
- **Redis**: 7.0+

### 1. 克隆项目

```bash
git clone https://github.com/TE-Fire/moodnote-parent.git
cd moodnote-parent
```

### 2. 数据库配置

创建数据库并配置连接信息：

```sql
CREATE DATABASE moodnote_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 后端启动

```bash
# 进入后端目录
cd moodnote-server

# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8080`

### 4. 前端启动

```bash
# 进入前端目录
cd moodnote-web

# 安装依赖
npm install

# 开发模式运行
npm run dev
```

前端服务默认运行在 `http://localhost:3000`

---

## 📁 项目结构

```
moodnote-parent/
├── moodnote-server/          # 后端服务
│   ├── src/main/java/        # Java 源代码
│   ├── src/main/resources/   # 配置文件
│   └── pom.xml               # Maven 依赖管理
├── moodnote-web/             # 前端应用
│   ├── src/                  # Vue 源代码
│   ├── public/               # 静态资源
│   ├── vite.config.js        # Vite 配置
│   └── package.json          # npm 依赖管理
├── moodnote-service/         # 业务逻辑层
│   └── src/main/java/        # Service 层代码
├── moodnote-mapper/          # 数据访问层
│   ├── src/main/java/        # Mapper 接口
│   └── src/main/resources/   # MyBatis 配置
├── moodnote-common/          # 公共模块
│   └── src/main/java/        # 工具类、常量、异常
└── 项目总结/                  # 项目文档
    └── spring/               # Spring 相关文档
```

---

## 🔌 API 接口

### 认证相关

| 接口 | 方法 | 描述 |
| :--- | :--- | :--- |
| `/api/auth/register` | POST | 用户注册 |
| `/api/auth/login` | POST | 用户登录 |
| `/api/auth/logout` | POST | 用户登出 |
| `/api/auth/captcha` | GET | 获取图形验证码 |
| `/api/auth/send-code` | POST | 发送邮箱验证码 |
| `/api/auth/reset-password` | POST | 重置密码 |

### 用户相关

| 接口 | 方法 | 描述 |
| :--- | :--- | :--- |
| `/api/user/info` | GET | 获取用户信息 |

### 日记相关

| 接口 | 方法 | 描述 |
| :--- | :--- | :--- |
| `/api/diary` | GET | 获取日记列表 |
| `/api/diary/{id}` | GET | 获取日记详情 |
| `/api/diary` | POST | 创建日记 |
| `/api/diary/{id}` | PUT | 更新日记 |
| `/api/diary/{id}` | DELETE | 删除日记 |

---

## 🔐 安全机制

### JWT 认证流程

1. 用户登录成功后，服务端生成 JWT Token
2. Token 存储在前端 localStorage
3. 每次请求携带 `Authorization: Bearer {token}`
4. 后端 Filter 验证 Token 有效性
5. 登出时 Token 加入黑名单，立即失效

### Token 黑名单机制

- 使用 Redis 存储已失效的 Token
- 设置与 JWT 过期时间一致的 TTL
- 请求时检查 Token 是否在黑名单中

---

## 🛠 开发指南

### 代码规范

- **Java**: 遵循 Google Java 编码规范
- **Vue**: 遵循 Vue 官方风格指南
- **Git**: 使用 Conventional Commits 规范

### 分支管理

```
main          # 主分支，稳定版本
develop       # 开发分支，功能集成
feature/*     # 功能分支，新特性开发
hotfix/*      # 修复分支，紧急 Bug 修复
```

### 提交规范

```
feat: 添加新功能
fix: 修复 Bug
docs: 更新文档
style: 代码格式调整
refactor: 代码重构
test: 添加测试
chore: 构建/工具更新
```

---

## 📝 配置说明

### 后端配置文件

`moodnote-server/src/main/resources/application.yml`

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/moodnote_db
    username: admin
    password: password
  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: your-256-bit-secret-key-here
  expire-time: 24
```

### 前端环境变量

`moodnote-web/.env`

```env
VITE_API_BASE_URL=http://localhost:8080
```

---

## 🤝 贡献指南

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/feature-name`)
3. 提交代码 (`git commit -m "feat: 添加新功能"`)
4. 推送到分支 (`git push origin feature/feature-name`)
5. 创建 Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

---

## 📧 联系方式

如有问题或建议，欢迎通过以下方式联系：

- GitHub: [https://github.com/TE-Fire](https://github.com/TE-Fire)
- 邮箱: [你的邮箱]

---

*Made with ❤️ by TE-Fire*
