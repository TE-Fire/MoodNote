# Skill: 配置基础依赖

## 触发条件
当 Vue 3 项目初始化完成后，需要配置前后端的基础依赖和配置文件。

## 前置依赖
- Skill-003-Vue3项目初始化

## 执行规范

### 文件位置
- 后端配置文件：`moodnote-server/src/main/resources/`
- 前端配置文件：`moodnote-web/`

### 命名规范
- 配置文件名称：遵循各框架的标准命名
- 环境配置文件：`application-{环境}.yml`

### 代码规范
- 后端使用 YAML 格式配置文件
- 前端使用 Vite 环境变量配置
- 配置文件结构清晰，注释完整

### 依赖引入
- 后端：数据库连接、MyBatis-Plus、日志等
- 前端：API 基础配置、路由基础配置

## 代码模板

### 模板说明
配置后端 Spring Boot 应用的基础配置文件和前端的基础配置。

### 代码示例

#### 1. 后端 application.yml 配置
```yaml
spring:
  application:
    name: moodnote-server
  datasource:
    url: jdbc:mysql://localhost:3306/moodnote?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Shanghai

mybatis-plus:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.moodnote.pojo.entity
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

server:
  port: 8080
  servlet:
    context-path: /

# Knife4j 配置
knife4j:
  enable: true
  setting:
    language: zh_cn
```

#### 2. 后端 application-dev.yml 配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/moodnote?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456

server:
  port: 8080
```

#### 3. 后端 application-prod.yml 配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/moodnote?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456

server:
  port: 8080
```

#### 4. 前端 main.js 配置
```js
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

import './assets/styles/global.scss'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
```

#### 5. 前端 router/index.js 配置
```js
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Home.vue')
    },
    {
      path: '/diary/create',
      name: 'diary-create',
      component: () => import('../views/DiaryCreate.vue')
    },
    {
      path: '/diary/:id',
      name: 'diary-detail',
      component: () => import('../views/DiaryDetail.vue')
    },
    {
      path: '/diary/edit/:id',
      name: 'diary-edit',
      component: () => import('../views/DiaryEdit.vue')
    },
    {
      path: '/stats/calendar',
      name: 'stats-calendar',
      component: () => import('../views/StatsCalendar.vue')
    },
    {
      path: '/stats/trend',
      name: 'stats-trend',
      component: () => import('../views/StatsTrend.vue')
    }
  ]
})

export default router
```

## 验收标准
1. 后端配置文件创建成功
2. 数据库连接配置正确
3. MyBatis-Plus 配置正确
4. 前端路由配置完成
5. 前端应用入口配置正确

## 关联 Skill
前置：Skill-003-Vue3项目初始化

后置：Skill-005-全局异常处理