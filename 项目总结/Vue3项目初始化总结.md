# Vue 3 项目初始化知识总结

## 一、Vue 3 项目初始化步骤

### 1.1 环境准备

在开始初始化 Vue 3 项目之前，需要确保以下环境已准备就绪：

- **Node.js**：版本 16.0 或更高
- **npm**：版本 7.0 或更高
- **Git**：用于版本控制

### 1.2 初始化项目

#### 方法一：使用 Vite 官方脚手架

Vite 是 Vue 官方推荐的构建工具，提供了快速的开发体验。

```bash
# 在项目根目录执行
npm create vite@latest moodnote-web -- --template vue
```

#### 方法二：手动创建项目结构

如果 Vite 脚手架执行失败，可以手动创建项目结构：

1. **创建项目目录**：`moodnote-web`
2. **创建标准目录结构**：
   - `src/api`：API 请求封装
   - `src/assets`：静态资源
   - `src/components`：组件
   - `src/composables`：组合式函数
   - `src/router`：路由配置
   - `src/stores`：状态管理
   - `src/utils`：工具函数
   - `src/views`：页面视图
   - `public`：公共静态资源

### 1.3 配置项目依赖

在 `package.json` 文件中配置项目依赖：

- **核心依赖**：Vue 3、Vue Router、Pinia
- **功能依赖**：Axios、ECharts、Day.js
- **开发依赖**：Vite、ESLint、Sass

### 1.4 配置构建工具

在 `vite.config.js` 文件中配置 Vite 构建工具，包括：

- 插件配置
- 开发服务器配置（端口、代理等）
- 构建配置

### 1.5 配置环境变量

创建 `.env.development` 和 `.env.production` 文件，配置不同环境的变量，如 API 基础路径。

## 二、项目文件结构与作用

### 2.1 根目录文件

| 文件/目录 | 作用 |
|----------|------|
| `index.html` | 项目入口 HTML 文件 |
| `package.json` | 项目配置文件，定义依赖和脚本 |
| `vite.config.js` | Vite 构建工具配置 |
| `.env.development` | 开发环境变量配置 |
| `.env.production` | 生产环境变量配置 |
| `src/` | 源代码目录 |
| `public/` | 公共静态资源目录 |

### 2.2 src 目录结构

#### 2.2.1 核心文件

| 文件 | 作用 |
|------|------|
| `main.js` | 项目入口文件，初始化 Vue 应用 |
| `App.vue` | 根组件，应用的容器 |
| `router/index.js` | 路由配置，定义页面路由 |

#### 2.2.2 功能目录

| 目录 | 作用 |
|------|------|
| `api/` | API 请求封装，按业务模块划分 |
| `assets/` | 静态资源，包括图片和样式 |
| `components/` | 组件，包括通用组件和业务组件 |
| `composables/` | 组合式函数（Hooks），封装可复用逻辑 |
| `stores/` | Pinia 状态管理 store |
| `utils/` | 工具函数，如 Axios 封装、日期处理等 |
| `views/` | 页面视图，对应路由配置 |

### 2.3 关键文件详解

#### 2.3.1 package.json

```json
{
  "name": "moodnote-web",
  "private": true,
  "version": "0.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",           // 启动开发服务器
    "build": "vite build",   // 构建生产版本
    "preview": "vite preview", // 预览生产构建
    "lint": "eslint . --ext .vue,.js,.jsx,.cjs,.mjs --fix --ignore-path .gitignore" // 代码 lint
  },
  "dependencies": {
    "vue": "^3.4.21",        // Vue 3 核心库
    "vue-router": "^4.3.0",   // Vue 路由
    "pinia": "^2.1.7",        // Pinia 状态管理
    "axios": "^1.6.7",        // HTTP 请求库
    "echarts": "^5.5.0",      // 数据可视化
    "dayjs": "^1.11.10"       // 日期处理库
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.4", // Vite Vue 插件
    "eslint": "^8.57.0",      // ESLint 代码规范
    "eslint-plugin-vue": "^9.23.0", // Vue ESLint 插件
    "sass": "^1.75.0",        // SCSS 预处理器
    "vite": "^5.2.0"          // Vite 构建工具
  }
}
```

#### 2.3.2 vite.config.js

```js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()], // 使用 Vue 插件
  server: {
    port: 3000, // 开发服务器端口
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端 API 地址
        changeOrigin: true, // 允许跨域
        rewrite: (path) => path.replace(/^\/api/, '') // 重写路径
      }
    }
  }
})
```

#### 2.3.3 main.js

```js
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// 导入全局样式
import './assets/styles/global.scss'

const app = createApp(App)

app.use(createPinia()) // 使用 Pinia 状态管理
app.use(router) // 使用 Vue Router

app.mount('#app') // 挂载应用
```

#### 2.3.4 router/index.js

```js
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(), // 使用 HTML5 History 模式
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Home.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue')
    },
    // 其他路由配置...
  ]
})

export default router
```

## 三、Vue 3 项目最佳实践

### 3.1 代码规范

- **使用 Composition API**：优先使用 `<script setup>` 语法糖
- **组件命名**：使用大驼峰命名（PascalCase）
- **文件命名**：使用小驼峰（camelCase）或短横线（kebab-case）
- **路由路径**：使用短横线（kebab-case）
- **代码风格**：使用 ESLint 进行代码规范检查

### 3.2 项目结构优化

- **按功能模块组织**：将相关功能的文件放在一起
- **组件拆分**：将复杂组件拆分为更小的可复用组件
- **逻辑复用**：使用组合式函数（Hooks）封装可复用逻辑
- **状态管理**：使用 Pinia 管理全局状态

### 3.3 性能优化

- **路由懒加载**：使用动态导入（`import()`）实现路由懒加载
- **组件懒加载**：对于大型组件，使用动态导入
- **图片优化**：使用适当的图片格式和大小
- **网络请求优化**：使用 Axios 拦截器处理请求和响应

### 3.4 开发工具

- **Vite**：快速的开发服务器和构建工具
- **ESLint**：代码规范检查
- **Prettier**：代码格式化
- **Vue DevTools**：Vue 开发调试工具

## 四、常见问题与解决方案

### 4.1 依赖安装失败

**问题**：执行 `npm install` 时失败

**解决方案**：
- 检查网络连接
- 清除 npm 缓存：`npm cache clean --force`
- 使用镜像源：`npm install --registry=https://registry.npmmirror.com`

### 4.2 开发服务器启动失败

**问题**：执行 `npm run dev` 时启动失败

**解决方案**：
- 检查端口是否被占用
- 检查 vite.config.js 配置是否正确
- 检查 package.json 脚本配置

### 4.3 路由跳转失败

**问题**：页面路由跳转时出现错误

**解决方案**：
- 检查路由配置是否正确
- 确保组件路径正确
- 检查路由参数传递是否正确

### 4.4 API 请求失败

**问题**：后端 API 请求失败

**解决方案**：
- 检查 vite.config.js 中的代理配置
- 确保后端服务正在运行
- 检查 API 路径是否正确

## 五、总结

Vue 3 项目初始化是前端开发的重要一步，通过合理的项目结构和配置，可以为后续的开发工作奠定良好的基础。本总结详细介绍了 Vue 3 项目初始化的步骤、项目文件结构与作用，以及最佳实践和常见问题解决方案，希望能为开发者提供参考。

在实际项目开发中，应根据项目规模和需求，灵活调整项目结构和配置，以达到最佳的开发体验和性能表现。