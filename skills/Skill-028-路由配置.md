# Skill: 路由配置

## 触发条件
当前端需要配置 Vue Router 路由时。

## 前置依赖
- Skill-027-Axios封装与拦截器

## 执行规范

### 文件位置
- 路由文件：`moodnote-web/src/router/`

### 命名规范
- 文件名称：`index.js`

### 代码规范
- 使用 createRouter 创建路由实例
- 配置路由规则
- 使用 createWebHistory 模式

### 依赖引入
- Vue Router 依赖

## 代码模板

### 模板说明
配置 Vue Router 路由，定义应用的页面路由规则。

### 代码示例

#### index.js 路由配置
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
1. 路由配置文件创建成功
2. 配置了所有必要的路由规则
3. 使用了正确的组件导入方式
4. 路由路径命名规范
5. 应用能够正常导航

## 关联 Skill
前置：Skill-027-Axios封装与拦截器

后置：Skill-029-Pinia状态管理