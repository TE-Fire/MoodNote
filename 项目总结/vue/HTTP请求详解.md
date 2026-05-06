# HTTP 请求详解：从原生 JS 到 Axios 封装

---

## 一、HTTP 请求基础

### 1.1 什么是 HTTP

HTTP（Hypertext Transfer Protocol，超文本传输协议）是互联网上应用最广泛的网络协议之一。它基于 **客户端-服务器** 模型，客户端（浏览器、APP、前端应用等）向服务器发送请求，服务器处理请求后返回响应。

简单来说，HTTP 就是客户端和服务器之间沟通的"语言"。当你在浏览器中输入网址并按下回车时，浏览器就会向服务器发送 HTTP 请求，服务器收到请求后会返回相应的网页内容。

### 1.2 HTTP 请求的基本流程

HTTP 请求遵循一个标准的请求-响应循环：

```
客户端                    服务器
  |                         |
  |---- 请求行 + 请求头 ---->|
  |                         |
  |<---- 状态行 + 响应头 + 响应体 ---|
  |                         |
```

**请求的组成部分**：

| 组成部分 | 说明 | 示例 |
| :--- | :--- | :--- |
| **请求行** | 包含请求方法、URL、HTTP 版本 | `GET /api/users HTTP/1.1` |
| **请求头** | 描述请求的附加信息 | `Content-Type: application/json` |
| **请求体** | 携带请求数据（POST/PUT 请求使用） | `{"username": "admin"}` |

**响应的组成部分**：

| 组成部分 | 说明 | 示例 |
| :--- | :--- | :--- |
| **状态行** | 包含 HTTP 版本、状态码、状态描述 | `HTTP/1.1 200 OK` |
| **响应头** | 描述响应的附加信息 | `Content-Type: application/json` |
| **响应体** | 返回的实际数据 | `{"code": 200, "data": [...]}` |

### 1.3 HTTP 常用方法

HTTP 定义了多种请求方法，每种方法有其特定的用途：

| 方法 | 用途 | 是否有请求体 | 幂等性 |
| :--- | :--- | :--- | :--- |
| **GET** | 获取资源 | 否 | 是 |
| **POST** | 创建资源 | 是 | 否 |
| **PUT** | 更新资源 | 是 | 是 |
| **DELETE** | 删除资源 | 否 | 是 |
| **PATCH** | 部分更新资源 | 是 | 否 |
| **HEAD** | 获取资源头部信息 | 否 | 是 |

**幂等性**：指多次执行相同的请求，结果是否相同。GET、PUT、DELETE 是幂等的，POST 不是。

### 1.4 HTTP 状态码

状态码用于表示服务器对请求的处理结果：

| 状态码范围 | 含义 | 常见状态码 |
| :--- | :--- | :--- |
| **1xx** | 信息性响应 | 100（继续） |
| **2xx** | 成功响应 | 200（成功）、201（创建）、204（无内容） |
| **3xx** | 重定向 | 301（永久重定向）、302（临时重定向）、304（未修改） |
| **4xx** | 客户端错误 | 400（请求错误）、401（未授权）、403（禁止访问）、404（未找到） |
| **5xx** | 服务器错误 | 500（服务器内部错误）、502（网关错误）、503（服务不可用） |

---

## 二、JavaScript 中的异步编程

### 2.1 为什么需要异步

JavaScript 是**单线程**语言，这意味着它一次只能执行一个任务。如果所有操作都是同步的，那么一个耗时的操作（如网络请求）会阻塞整个程序，导致页面卡顿。

异步编程允许程序在等待某些操作完成时继续执行其他任务。就像你在烧水时可以同时准备泡茶的杯子，而不是一直盯着水壶。

### 2.2 回调函数（Callback）

回调函数是最早的异步处理方式。它是一个作为参数传递给另一个函数的函数，在异步操作完成后被调用。

#### 2.2.1 回调函数基础

```javascript
// 定义一个接受回调函数的异步函数
function fetchData(callback) {
  setTimeout(() => {
    const data = { name: '张三', age: 25 }
    // 异步操作完成后调用回调函数
    callback(null, data)
  }, 1000)
}

// 使用回调函数
fetchData((error, result) => {
  if (error) {
    console.error('获取数据失败:', error)
    return
  }
  console.log('获取数据成功:', result)
})
```

#### 2.2.2 回调地狱（Callback Hell）

当需要执行多个嵌套的异步操作时，回调函数会导致代码缩进越来越深，形成"回调地狱"：

```javascript
// 回调地狱示例
getUser(userId, (err, user) => {
  if (err) throw err
  
  getUserPosts(user.id, (err, posts) => {
    if (err) throw err
    
    getPostComments(posts[0].id, (err, comments) => {
      if (err) throw err
      
      getCommentAuthor(comments[0].authorId, (err, author) => {
        if (err) throw err
        console.log('最终结果:', author)
      })
    })
  })
})
```

这种代码难以阅读、维护和调试。为了解决这个问题，Promise 应运而生。

---

## 三、Promise 详解

### 3.1 什么是 Promise

Promise 是 ES6 引入的一种处理异步操作的对象。它代表了一个异步操作的最终完成（或失败）及其结果值。

我们可以把 Promise 想象成一个**承诺**：

1. **Pending（待定）**：初始状态，既没有被兑现，也没有被拒绝
2. **Fulfilled（已兑现）**：操作成功完成
3. **Rejected（已拒绝）**：操作失败

```
Pending（进行中）
    |
    +----> Fulfilled（已成功）----> .then()
    |
    +----> Rejected（已失败）------> .catch()
```

### 3.2 创建 Promise

```javascript
const promise = new Promise((resolve, reject) => {
  // 异步操作
  setTimeout(() => {
    const success = true
    
    if (success) {
      // 成功时调用 resolve
      resolve('操作成功！')
    } else {
      // 失败时调用 reject
      reject(new Error('操作失败！'))
    }
  }, 1000)
})
```

`resolve` 函数将 Promise 状态从 Pending 变为 Fulfilled。
`reject` 函数将 Promise 状态从 Pending 变为 Rejected。

### 3.3 使用 Promise

Promise 提供了 `.then()` 和 `.catch()` 方法来处理结果：

```javascript
promise
  .then(result => {
    console.log('成功:', result)
    return result + ' 继续处理'
  })
  .then(newResult => {
    console.log('处理后:', newResult)
  })
  .catch(error => {
    console.error('失败:', error)
  })
```

### 3.4 Promise 链式调用

Promise 的强大之处在于支持链式调用，解决了回调地狱问题：

```javascript
// 链式调用示例
getUser(userId)
  .then(user => getUserPosts(user.id))
  .then(posts => getPostComments(posts[0].id))
  .then(comments => getCommentAuthor(comments[0].authorId))
  .then(author => console.log('最终结果:', author))
  .catch(error => console.error('发生错误:', error))
```

### 3.5 Promise 常用方法

#### 3.5.1 Promise.all()

等待所有 Promise 都成功完成：

```javascript
const promise1 = fetch('/api/users')
const promise2 = fetch('/api/posts')
const promise3 = fetch('/api/comments')

Promise.all([promise1, promise2, promise3])
  .then(responses => {
    // 所有请求都成功
    console.log('所有响应:', responses)
  })
  .catch(error => {
    // 任一请求失败
    console.error('有请求失败:', error)
  })
```

#### 3.5.2 Promise.race()

返回第一个完成的 Promise（无论成功或失败）：

```javascript
const fastPromise = new Promise(resolve => setTimeout(resolve, 100, '快的'))
const slowPromise = new Promise(resolve => setTimeout(resolve, 500, '慢的'))

Promise.race([fastPromise, slowPromise])
  .then(result => {
    console.log('先完成的:', result) // 输出: 快的
  })
```

#### 3.5.3 Promise.allSettled()

等待所有 Promise 完成（无论成功或失败）：

```javascript
Promise.allSettled([promise1, promise2, promise3])
  .then(results => {
    results.forEach(result => {
      if (result.status === 'fulfilled') {
        console.log('成功:', result.value)
      } else {
        console.log('失败:', result.reason)
      }
    })
  })
```

### 3.6 async/await（ES2017）

async/await 是 Promise 的语法糖，让异步代码看起来像同步代码：

```javascript
async function getData() {
  try {
    // await 等待 Promise 完成
    const user = await getUser(userId)
    const posts = await getUserPosts(user.id)
    const comments = await getPostComments(posts[0].id)
    const author = await getCommentAuthor(comments[0].authorId)
    
    console.log('最终结果:', author)
  } catch (error) {
    // 统一错误处理
    console.error('发生错误:', error)
  }
}

// 调用
getData()
```

---

## 四、JavaScript 原生 HTTP 请求

### 4.1 XMLHttpRequest（旧版）

这是最早的浏览器原生 HTTP 请求方式，现在已经逐渐被 Fetch API 取代，但在一些旧项目中仍能看到。

```javascript
// 创建 XMLHttpRequest 对象
const xhr = new XMLHttpRequest()

// 设置请求方法和 URL
xhr.open('GET', 'https://api.example.com/users')

// 设置响应类型
xhr.responseType = 'json'

// 设置请求头
xhr.setRequestHeader('Content-Type', 'application/json')

// 监听请求状态变化
xhr.onreadystatechange = function() {
  // readyState 的值：
  // 0: 未初始化
  // 1: 已打开（open 已调用）
  // 2: 已发送（send 已调用）
  // 3: 接收中（正在接收响应）
  // 4: 完成
  
  if (xhr.readyState === 4) {
    // 状态码 200 表示成功
    if (xhr.status === 200) {
      console.log('响应数据:', xhr.response)
    } else {
      console.error('请求失败，状态码:', xhr.status)
    }
  }
}

// 发送请求
xhr.send()
```

**POST 请求示例**：

```javascript
const xhr = new XMLHttpRequest()
xhr.open('POST', 'https://api.example.com/login')
xhr.responseType = 'json'
xhr.setRequestHeader('Content-Type', 'application/json')

// 请求成功完成
xhr.onload = function() {
  if (xhr.status === 200) {
    console.log('登录成功:', xhr.response)
  } else {
    console.error('登录失败:', xhr.status)
  }
}

// 请求出错
xhr.onerror = function() {
  console.error('网络请求出错')
}

// 请求超时
xhr.ontimeout = function() {
  console.error('请求超时')
}

// 设置超时时间（毫秒）
xhr.timeout = 5000

// 发送 JSON 数据
const data = {
  username: 'admin',
  password: '123456'
}
xhr.send(JSON.stringify(data))
```

### 4.2 Fetch API（现代方式）

Fetch API 是 ES6 引入的现代 HTTP 请求方式，基于 Promise：

```javascript
// GET 请求
fetch('https://api.example.com/users')
  .then(response => {
    // 检查 HTTP 状态码
    if (!response.ok) {
      throw new Error(`HTTP 错误: ${response.status}`)
    }
    // 解析 JSON 响应
    return response.json()
  })
  .then(data => {
    console.log('数据:', data)
  })
  .catch(error => {
    console.error('请求失败:', error)
  })
```

**POST 请求示例**：

```javascript
fetch('https://api.example.com/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'admin',
    password: '123456'
  }),
  credentials: 'include' // 携带 Cookie
})
.then(response => response.json())
.then(data => console.log('登录成功:', data))
.catch(error => console.error('登录失败:', error))
```

**Fetch API 的特点**：

| 特点 | 说明 |
| :--- | :--- |
| 基于 Promise | 支持 `.then()`、`.catch()` 和 async/await |
| 更简洁的语法 | 相比 XMLHttpRequest 更直观 |
| 默认不携带 Cookie | 需要设置 `credentials: 'include'` |
| 错误处理 | 需要手动检查 `response.ok`，HTTP 错误不会触发 `.catch()` |
| 流式响应 | 支持处理大文件和流媒体 |

---

## 五、Axios 库详解

### 5.1 什么是 Axios

Axios 是一个基于 Promise 的 HTTP 客户端，支持浏览器和 Node.js。它是目前最流行的 HTTP 请求库之一。

**主要特性**：

- 支持 Promise API
- 拦截请求和响应
- 自动转换 JSON 数据
- 取消请求
- 客户端支持防御 XSRF
- 支持请求超时
- 支持重试机制

### 5.2 基本使用

```javascript
import axios from 'axios'

// GET 请求
axios.get('/api/users', {
  params: {
    page: 1,
    limit: 10
  }
})
.then(response => {
  console.log('数据:', response.data)
})
.catch(error => {
  console.error('请求失败:', error)
})

// POST 请求
axios.post('/api/login', {
  username: 'admin',
  password: '123456'
})
.then(response => {
  console.log('登录成功:', response.data)
})
.catch(error => {
  console.error('登录失败:', error)
})

// async/await 方式
async function login() {
  try {
    const response = await axios.post('/api/login', {
      username: 'admin',
      password: '123456'
    })
    return response.data
  } catch (error) {
    throw error
  }
}
```

### 5.3 创建 axios 实例

```javascript
const instance = axios.create({
  baseURL: 'https://api.example.com',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 使用实例
instance.get('/users')
```

---

## 六、请求拦截器与响应拦截器

### 6.1 什么是拦截器

拦截器允许你在请求发送前或响应返回后进行统一处理。它们就像是请求和响应的"过滤器"。

**请求拦截器**：在请求发送前执行，可以用来添加 token、设置统一请求头。
**响应拦截器**：在响应返回后执行，可以用来统一处理错误、数据格式化。

### 6.2 请求拦截器

```javascript
axios.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    console.log('请求即将发送:', config.url)
    
    // 添加认证 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 可以修改 config
    config.headers['X-Requested-With'] = 'XMLHttpRequest'
    
    return config
  },
  error => {
    // 处理请求错误
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)
```

### 6.3 响应拦截器

```javascript
axios.interceptors.response.use(
  response => {
    // 对响应数据做些什么
    console.log('响应已收到:', response.status)
    
    // 直接返回 data 部分，简化后续处理
    return response.data
  },
  error => {
    // 处理响应错误
    console.error('响应拦截器错误:', error)
    
    if (error.response) {
      // HTTP 状态码错误
      const { status } = error.response
      
      if (status === 401) {
        // token 失效，跳转到登录页
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
    }
    
    return Promise.reject(error)
  }
)
```

### 6.4 移除拦截器

```javascript
// 添加拦截器并保存引用
const myInterceptor = axios.interceptors.request.use(config => {
  // ...
  return config
})

// 移除拦截器
axios.interceptors.request.eject(myInterceptor)
```

---

## 七、完整封装示例

### 7.1 创建 request.js

```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 添加 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 统一处理返回结果
    if (res.code !== 200) {
      // 业务错误提示
      ElMessage({
        message: res.message || '请求失败',
        type: 'error',
        duration: 3000
      })
      
      // token 失效处理
      if (res.code === 401) {
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
      
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应拦截器错误:', error)
    
    // 网络错误处理
    if (!error.response) {
      ElMessage({
        message: '网络异常，请检查网络连接',
        type: 'error',
        duration: 3000
      })
    } else {
      // HTTP 状态码错误处理
      const status = error.response.status
      let message = ''
      
      switch (status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权，请重新登录'
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求资源未找到'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `请求失败，状态码: ${status}`
      }
      
      ElMessage({
        message: message,
        type: 'error',
        duration: 3000
      })
    }
    
    return Promise.reject(error)
  }
)

// 封装常用请求方法
const api = {
  get(url, params = {}) {
    return request.get(url, { params })
  },
  
  post(url, data = {}) {
    return request.post(url, data)
  },
  
  put(url, data = {}) {
    return request.put(url, data)
  },
  
  delete(url, params = {}) {
    return request.delete(url, { params })
  }
}

export default api
```

### 7.2 使用封装的 API

```javascript
import api from '@/utils/request'

// 获取用户列表
async function getUsers(page = 1, limit = 10) {
  const result = await api.get('/api/users', {
    page,
    limit
  })
  return result.data
}

// 用户登录
async function login(username, password) {
  const result = await api.post('/api/auth/login', {
    username,
    password
  })
  // 保存 token
  localStorage.setItem('token', result.token)
  return result
}

// 更新用户信息
async function updateUser(userId, userData) {
  const result = await api.put(`/api/users/${userId}`, userData)
  return result
}

// 删除用户
async function deleteUser(userId) {
  const result = await api.delete('/api/users', {
    id: userId
  })
  return result
}
```

---

## 八、错误处理策略

### 8.1 错误类型

在 HTTP 请求中，可能遇到三种类型的错误：

| 错误类型 | 说明 | 示例 |
| :--- | :--- | :--- |
| **网络错误** | 无法连接到服务器 | 断网、服务器宕机 |
| **HTTP 错误** | 状态码非 2xx | 404、500 |
| **业务错误** | 状态码 200，但业务逻辑错误 | code !== 200 |

### 8.2 统一错误处理

```javascript
// 在响应拦截器中统一处理
request.interceptors.response.use(
  response => {
    const res = response.data
    
    if (res.code !== 200) {
      // 业务错误
      throw new Error(res.message)
    }
    
    return res
  },
  error => {
    // HTTP 错误和网络错误
    if (error.response) {
      // HTTP 错误
      const { status, data } = error.response
      console.error(`HTTP 错误: ${status}`, data)
      
      // 根据状态码进行不同处理
      if (status === 401) {
        // 未授权处理
      } else if (status === 403) {
        // 禁止访问处理
      }
    } else {
      // 网络错误
      console.error('网络错误:', error.message)
    }
    
    return Promise.reject(error)
  }
)
```

### 8.3 在业务代码中处理错误

```javascript
async function fetchUserInfo(userId) {
  try {
    const result = await api.get(`/api/users/${userId}`)
    return result.data
  } catch (error) {
    // 业务层特定错误处理
    console.error('获取用户信息失败:', error)
    
    // 可以选择重新抛出错误或返回默认值
    throw error
  }
}
```

---

## 九、最佳实践总结

### 9.1 配置管理

- **使用环境变量**：配置 baseURL、超时时间等
- **统一请求头**：在请求拦截器中设置
- **超时设置**：根据业务需求设置合理的超时时间

### 9.2 安全考虑

- **使用 HTTPS**：确保数据传输安全
- **Token 管理**：使用 Bearer 认证，存储在 localStorage 或 cookie
- **敏感数据加密**：密码等敏感信息加密传输

### 9.3 性能优化

- **请求缓存**：对相同请求进行缓存
- **请求合并**：合并重复请求
- **启用 gzip**：减少数据传输量
- **请求防抖/节流**：防止频繁请求

### 9.4 代码组织

```
src/
  utils/
    request.js    # axios 封装
  api/
    user.js       # 用户相关接口
    diary.js      # 日记相关接口
    tag.js        # 标签相关接口
```

**api/user.js 示例**：

```javascript
import api from '@/utils/request'

export const userApi = {
  login: (data) => api.post('/api/auth/login', data),
  register: (data) => api.post('/api/auth/register', data),
  logout: () => api.post('/api/auth/logout'),
  getUserInfo: () => api.get('/api/user/info'),
  updateUser: (data) => api.put('/api/user', data),
  deleteUser: (userId) => api.delete(`/api/users/${userId}`)
}
```

---

## 十、总结

从原生的 `XMLHttpRequest` 到现代的 `Fetch API`，再到封装完善的 `Axios`，HTTP 请求方式不断演进。

**核心要点**：

1. **HTTP 是客户端-服务器协议**：遵循请求-响应模型
2. **异步编程是 JavaScript 的核心**：解决单线程阻塞问题
3. **回调函数**：最早的异步处理方式，但易导致回调地狱
4. **Promise**：解决回调地狱，支持链式调用
5. **async/await**：Promise 的语法糖，使异步代码同步化
6. **Axios**：功能强大的 HTTP 客户端，提供拦截器机制
7. **统一封装**：提高代码可维护性和复用性

通过学习和掌握这些知识，你可以编写出高效、优雅的前端 HTTP 请求代码。