# Skill: Axios封装与拦截器

## 触发条件
当前端需要统一处理 API 请求，包括请求头设置、响应处理、错误处理等时。

## 前置依赖
- Skill-026-创建SpringBoot启动类

## 执行规范

### 文件位置
- 工具文件：`moodnote-web/src/utils/`

### 命名规范
- 文件名称：`request.js`

### 代码规范
- 使用 Axios 创建实例
- 配置请求拦截器和响应拦截器
- 统一处理错误响应
- 配置基础 URL

### 依赖引入
- Axios 依赖

## 代码模板

### 模板说明
封装 Axios 实例，配置拦截器，统一处理 API 请求和响应。

### 代码示例

#### request.js Axios 封装
```js
import axios from 'axios'

// 创建 Axios 实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 可以在这里添加 token 等认证信息
    // const token = localStorage.getItem('token')
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`
    // }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 统一处理响应
    if (res.code !== 200) {
      // 可以在这里添加错误提示
      console.error('请求失败:', res.message)
      return Promise.reject(new Error(res.message || 'Error'))
    }
    
    return res
  },
  error => {
    // 统一处理网络错误
    console.error('网络错误:', error.message)
    // 可以在这里添加错误提示
    return Promise.reject(error)
  }
)

export default request
```

## 验收标准
1. Axios 封装文件创建成功
2. 配置了基础 URL 和超时时间
3. 实现了请求拦截器和响应拦截器
4. 统一处理了错误响应
5. 代码结构清晰，易于维护

## 关联 Skill
前置：Skill-026-创建SpringBoot启动类

后置：Skill-028-路由配置