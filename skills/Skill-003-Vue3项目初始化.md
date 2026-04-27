# Skill: Vue3项目初始化

## 触发条件
当后端 Maven 子模块创建完成后，需要初始化前端 Vue 3 项目。

## 前置依赖
- Skill-002-创建子模块结构

## 执行规范

### 文件位置
- 前端工程目录：`d:\moodnote-parent\moodnote-web`

### 命名规范
- 项目名称：`moodnote-web`
- 配置文件：遵循 Vue 3 项目标准命名

### 代码规范
- 使用 Vite 作为构建工具
- 使用 Vue 3.4+ 版本
- 使用 Composition API（`<script setup>`）
- 遵循前端工程化最佳实践

### 依赖引入
- Vue 3.4+
- Vue Router 4.x
- Pinia 2.x
- Axios 1.6+
- ECharts 5.x
- Day.js 1.x
- SCSS
- ESLint 8.x

## 代码模板

### 模板说明
使用 Vite 初始化 Vue 3 项目，并配置基础依赖。

### 代码示例

#### 1. 初始化命令
```bash
# 在 moodnote-parent 目录下执行
npm create vite@latest moodnote-web -- --template vue
```

#### 2. package.json 配置
```json
{
  "name": "moodnote-web",
  "private": true,
  "version": "0.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext .vue,.js,.jsx,.cjs,.mjs --fix --ignore-path .gitignore"
  },
  "dependencies": {
    "vue": "^3.4.21",
    "vue-router": "^4.3.0",
    "pinia": "^2.1.7",
    "axios": "^1.6.7",
    "echarts": "^5.5.0",
    "dayjs": "^1.11.10"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.4",
    "eslint": "^8.57.0",
    "eslint-plugin-vue": "^9.23.0",
    "sass": "^1.75.0",
    "vite": "^5.2.0"
  }
}
```

#### 3. vite.config.js 配置
```js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```

#### 4. .env.development 配置
```
VITE_API_BASE_URL = '/api'
```

#### 5. .env.production 配置
```
VITE_API_BASE_URL = '/api'
```

## 验收标准
1. Vue 3 项目初始化成功
2. 安装了所有必要的依赖包
3. Vite 配置正确，包含代理设置
4. 环境变量配置完成
5. 项目可以正常启动和构建

## 关联 Skill
前置：Skill-002-创建子模块结构

后置：Skill-004-配置基础依赖