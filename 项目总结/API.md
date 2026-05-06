# MoodNote API 文档

## 基础信息

- 基础 URL: `http://localhost:8080`
- 接口前缀: `/api`
- 数据格式: JSON
- 字符编码: UTF-8

---

## 1. 认证模块

### 1.1 获取图形验证码

#### 接口信息
- **接口路径**: `/api/auth/captcha`
- **请求方法**: `GET`
- **是否需要认证**: 否

#### 请求参数
无

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "captchaKey": "abc123def456",
    "captchaImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAA..."
  }
}
```

#### 响应字段说明
| 字段名 | 类型 | 说明 |
|-------|------|------|
| captchaKey | String | 验证码唯一标识 |
| captchaImage | String | Base64编码的验证码图片 |

---

### 1.2 发送邮箱验证码

#### 接口信息
- **接口路径**: `/api/auth/send-code`
- **请求方法**: `POST`
- **是否需要认证**: 否

#### 请求参数
**Headers**:
```
Content-Type: application/json
```

**Body** (JSON):
```json
{
  "email": "user@example.com",
  "type": "register"
}
```

#### 请求字段说明
| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| email | String | 是 | 邮箱地址 |
| type | String | 是 | 类型: register(注册) / reset(重置密码) |

#### 响应示例
**成功响应**:
```json
{
  "code": 200,
  "message": "验证码发送成功",
  "data": null
}
```

**失败响应 - 邮箱已注册**:
```json
{
  "code": 1001,
  "message": "该邮箱已被注册",
  "data": null
}
```

---

### 1.3 用户注册

#### 接口信息
- **接口路径**: `/api/auth/register`
- **请求方法**: `POST`
- **是否需要认证**: 否

#### 请求参数
**Headers**:
```
Content-Type: application/json
```

**Body** (JSON):
```json
{
  "username": "testuser",
  "password": "123456",
  "email": "user@example.com",
  "code": "123456",
  "nickname": "测试用户"
}
```

#### 请求字段说明
| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| username | String | 是 | 用户名 (3-50个字符) |
| password | String | 是 | 密码 (至少6位) |
| email | String | 是 | 邮箱地址 |
| code | String | 是 | 邮箱验证码 |
| nickname | String | 否 | 昵称 |

#### 响应示例
**成功响应**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

**失败响应 - 用户已存在**:
```json
{
  "code": 1001,
  "message": "用户已存在",
  "data": null
}
```

**失败响应 - 邮箱验证码错误**:
```json
{
  "code": 1005,
  "message": "邮箱验证码错误",
  "data": null
}
```

---

### 1.4 用户登录

#### 接口信息
- **接口路径**: `/api/auth/login`
- **请求方法**: `POST`
- **是否需要认证**: 否

#### 请求参数
**Headers**:
```
Content-Type: application/json
```

**Body** (JSON):
```json
{
  "username": "testuser",
  "password": "123456",
  "captcha": "ABCD",
  "captchaKey": "abc123def456"
}
```

#### 请求字段说明
| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| username | String | 是 | 用户名或邮箱 |
| password | String | 是 | 密码 |
| captcha | String | 是 | 图形验证码 |
| captchaKey | String | 是 | 验证码key |

#### 响应示例
**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJpYXQiOjE3MTUwNzAwMDAsImV4cCI6MTcxNTE1NjQwMH0.example",
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "user@example.com",
      "nickname": "测试用户",
      "avatar": null,
      "phone": null,
      "gender": 0,
      "lastLoginTime": "2026-05-06T18:30:00",
      "createTime": "2026-05-01T10:00:00"
    }
  }
}
```

**失败响应 - 验证码错误**:
```json
{
  "code": 1004,
  "message": "验证码错误",
  "data": null
}
```

**失败响应 - 用户不存在**:
```json
{
  "code": 1002,
  "message": "用户不存在",
  "data": null
}
```

**失败响应 - 密码错误**:
```json
{
  "code": 1003,
  "message": "密码错误",
  "data": null
}
```

---

### 1.5 用户登出

#### 接口信息
- **接口路径**: `/api/auth/logout`
- **请求方法**: `POST`
- **是否需要认证**: 是

#### 请求参数
**Headers**:
```
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Body**: 无

#### 响应示例
```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

---

### 1.6 重置密码

#### 接口信息
- **接口路径**: `/api/auth/reset-password`
- **请求方法**: `POST`
- **是否需要认证**: 否

#### 请求参数
**Headers**:
```
Content-Type: application/json
```

**Body** (JSON):
```json
{
  "email": "user@example.com",
  "code": "123456",
  "newPassword": "654321"
}
```

#### 请求字段说明
| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| email | String | 是 | 邮箱地址 |
| code | String | 是 | 邮箱验证码 |
| newPassword | String | 是 | 新密码 (至少6位) |

#### 响应示例
**成功响应**:
```json
{
  "code": 200,
  "message": "密码重置成功",
  "data": null
}
```

**失败响应 - 用户不存在**:
```json
{
  "code": 1002,
  "message": "用户不存在",
  "data": null
}
```

---

## 2. 用户模块

### 2.1 获取当前用户信息

#### 接口信息
- **接口路径**: `/api/user/info`
- **请求方法**: `GET`
- **是否需要认证**: 是

#### 请求参数
**Headers**:
```
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "user@example.com",
    "nickname": "测试用户",
    "avatar": null,
    "phone": null,
    "gender": 0,
    "lastLoginTime": "2026-05-06T18:30:00",
    "createTime": "2026-05-01T10:00:00"
  }
}
```

---

### 2.2 更新用户信息

#### 接口信息
- **接口路径**: `/api/user/profile`
- **请求方法**: `PUT`
- **是否需要认证**: 是

#### 请求参数
**Headers**:
```
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Body** (JSON):
```json
{
  "nickname": "新昵称",
  "avatar": "https://example.com/avatar.jpg",
  "phone": "13800138000",
  "gender": 1
}
```

#### 请求字段说明
| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| nickname | String | 否 | 昵称 |
| avatar | String | 否 | 头像URL |
| phone | String | 否 | 手机号 |
| gender | Integer | 否 | 性别: 0未知/1男/2女 |

#### 响应示例
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

## 附录: 错误码说明

| 错误码 | 说明 |
|-------|------|
| 200 | 操作成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 1001 | 用户已存在 |
| 1002 | 用户不存在 |
| 1003 | 密码错误 |
| 1004 | 验证码错误 |
| 1005 | 邮箱验证码错误 |
| 1006 | 邮件发送失败 |
| 1007 | Token已过期 |
| 1008 | Token无效 |

---

## Apifox 导入提示

1. 打开 Apifox 软件
2. 新建项目或选择已有项目
3. 进入「接口管理」→「手动创建」
4. 根据上述接口信息逐个创建接口
5. 或者使用 JSON/YAML 格式导入

### 快速复制

你可以直接复制以下 JSON 内容，在 Apifox 中「导入」→「OpenAPI」→「粘贴内容」导入：

```json
{
  "openapi": "3.0.0",
  "info": {
    "title": "MoodNote API",
    "version": "1.0.0",
    "description": "晚风记事 API 文档"
  },
  "servers": [
    {
      "url": "http://localhost:8080",
      "description": "本地开发环境"
    }
  ],
  "tags": [
    {
      "name": "认证模块"
    },
    {
      "name": "用户模块"
    }
  ]
}
```

## 注意事项

1. **Token 认证**: 需要认证的接口请在 Headers 中添加 `Authorization: Bearer {token}`
2. **邮箱验证码**: 开发环境验证码可能是固定的测试值（如 123456）
3. **图形验证码**: Base64 格式的图片可以在 Apifox 中直接预览
4. **时间格式**: 所有时间字段统一使用 `yyyy-MM-dd HH:mm:ss` 格式
