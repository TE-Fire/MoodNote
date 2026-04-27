# Skill: 前端登录注册页面

## 触发条件
当需要创建前端登录、注册和个人主页页面时。

## 前置依赖
- Skill-040-SpringSecurity配置

## 执行规范

### 文件位置
- 页面文件：`moodnote-web/src/views/`
- 组件文件：`moodnote-web/src/components/auth/`

### 命名规范
- 登录页面：`Login.vue`
- 注册页面：`Register.vue`
- 个人主页：`Profile.vue`
- 登录组件：`LoginForm.vue`
- 注册组件：`RegisterForm.vue`

### 代码规范
- 使用 Vue 3 的 Composition API
- 使用 `<script setup>` 语法
- 集成 Pinia 状态管理
- 实现表单验证
- 实现验证码功能

### 依赖引入
- Vue 3 依赖
- Pinia 依赖
- Axios 依赖

## 代码模板

### 模板说明
创建前端登录、注册和个人主页页面，实现用户认证功能。

### 代码示例

#### 1. Login.vue 登录页面
```vue
<template>
  <div class="login-page">
    <div class="login-container">
      <h2>登录</h2>
      <LoginForm @login-success="handleLoginSuccess" />
      <div class="login-footer">
        <span>还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import LoginForm from '../components/auth/LoginForm.vue'

const router = useRouter()

const handleLoginSuccess = () => {
  router.push('/')
}
</script>

<style scoped>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.login-container {
  width: 400px;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-container h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #303133;
}

.login-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #606266;
}

.login-footer a {
  color: #409eff;
  text-decoration: none;
  margin-left: 5px;
}

.login-footer a:hover {
  text-decoration: underline;
}
</style>
```

#### 2. Register.vue 注册页面
```vue
<template>
  <div class="register-page">
    <div class="register-container">
      <h2>注册</h2>
      <RegisterForm @register-success="handleRegisterSuccess" />
      <div class="register-footer">
        <span>已有账号？</span>
        <router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import RegisterForm from '../components/auth/RegisterForm.vue'

const router = useRouter()

const handleRegisterSuccess = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.register-container {
  width: 400px;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.register-container h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #303133;
}

.register-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #606266;
}

.register-footer a {
  color: #409eff;
  text-decoration: none;
  margin-left: 5px;
}

.register-footer a:hover {
  text-decoration: underline;
}
</style>
```

#### 3. Profile.vue 个人主页
```vue
<template>
  <div class="profile-page">
    <div class="profile-container">
      <h2>个人主页</h2>
      <div class="profile-info">
        <div class="avatar-section">
          <img :src="userInfo.avatar || defaultAvatar" alt="头像" class="avatar" />
          <h3>{{ userInfo.nickname || userInfo.username }}</h3>
          <p>{{ userInfo.email }}</p>
        </div>
        <div class="info-section">
          <div class="info-item">
            <span class="label">用户名：</span>
            <span class="value">{{ userInfo.username }}</span>
          </div>
          <div class="info-item">
            <span class="label">昵称：</span>
            <span class="value">{{ userInfo.nickname || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">邮箱：</span>
            <span class="value">{{ userInfo.email }}</span>
          </div>
          <div class="info-item">
            <span class="label">手机号：</span>
            <span class="value">{{ userInfo.phone || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">性别：</span>
            <span class="value">{{ getGenderText(userInfo.gender) }}</span>
          </div>
          <div class="info-item">
            <span class="label">注册时间：</span>
            <span class="value">{{ formatDate(userInfo.createTime) }}</span>
          </div>
          <div class="info-item">
            <span class="label">最后登录：</span>
            <span class="value">{{ formatDate(userInfo.lastLoginTime) }}</span>
          </div>
        </div>
        <div class="action-section">
          <BaseButton type="primary" @click="handleEdit">编辑资料</BaseButton>
          <BaseButton type="danger" @click="handleLogout">退出登录</BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import BaseButton from '../components/common/BaseButton.vue'
import dayjs from 'dayjs'
import request from '../utils/request'

const router = useRouter()
const userInfo = ref({})
const defaultAvatar = 'https://via.placeholder.com/100'

const getGenderText = (gender) => {
  const genderMap = {
    0: '未知',
    1: '男',
    2: '女'
  }
  return genderMap[gender] || '未知'
}

const formatDate = (date) => {
  if (!date) return '未登录'
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

const handleEdit = () => {
  // 编辑资料逻辑
  console.log('编辑资料')
}

const handleLogout = async () => {
  try {
    await request.post('/api/v1/auth/logout')
    // 清除本地存储的 token
    localStorage.removeItem('token')
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
  }
}

const fetchUserInfo = async () => {
  try {
    const response = await request.get('/api/v1/user/profile')
    userInfo.value = response.data
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.profile-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.profile-container {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.profile-container h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.profile-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-section {
  text-align: center;
  margin-bottom: 30px;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-bottom: 15px;
}

.avatar-section h3 {
  margin: 0 0 5px 0;
  color: #303133;
}

.avatar-section p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.info-section {
  width: 100%;
  margin-bottom: 30px;
}

.info-item {
  display: flex;
  margin-bottom: 15px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.label {
  width: 100px;
  font-weight: 500;
  color: #606266;
}

.value {
  flex: 1;
  color: #303133;
}

.action-section {
  display: flex;
  gap: 10px;
}
</style>
```

#### 4. LoginForm.vue 登录表单组件
```vue
<template>
  <form @submit.prevent="handleSubmit" class="login-form">
    <div class="form-item">
      <label for="username">用户名/邮箱</label>
      <input 
        type="text" 
        id="username" 
        v-model="form.username" 
        placeholder="请输入用户名或邮箱"
        required
      />
    </div>
    
    <div class="form-item">
      <label for="password">密码</label>
      <input 
        type="password" 
        id="password" 
        v-model="form.password" 
        placeholder="请输入密码"
        required
      />
    </div>
    
    <div class="form-item captcha-item">
      <label for="captcha">验证码</label>
      <div class="captcha-input">
        <input 
          type="text" 
          id="captcha" 
          v-model="form.captcha" 
          placeholder="请输入验证码"
          required
        />
        <img :src="captchaImage" alt="验证码" class="captcha-image" @click="refreshCaptcha" />
      </div>
    </div>
    
    <div class="form-actions">
      <BaseButton type="primary" native-type="submit" :loading="loading">登录</BaseButton>
    </div>
  </form>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import BaseButton from '../common/BaseButton.vue'
import request from '../../utils/request'

const emit = defineEmits(['login-success'])

const form = ref({
  username: '',
  password: '',
  captcha: '',
  captchaKey: ''
})

const loading = ref(false)
const captchaImage = ref('')

const refreshCaptcha = async () => {
  try {
    const response = await request.get('/api/v1/auth/captcha')
    form.value.captchaKey = response.data.key
    captchaImage.value = response.data.image
  } catch (error) {
    console.error('获取验证码失败:', error)
  }
}

const handleSubmit = async () => {
  loading.value = true
  try {
    const response = await request.post('/api/v1/auth/login', form.value)
    // 存储 token
    localStorage.setItem('token', response.data.token)
    // 更新请求头
    request.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`
    emit('login-success')
  } catch (error) {
    console.error('登录失败:', error)
    alert(error.response?.data?.message || '登录失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.form-item input {
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
}

.captcha-item {
  position: relative;
}

.captcha-input {
  display: flex;
  gap: 10px;
}

.captcha-input input {
  flex: 1;
}

.captcha-image {
  width: 100px;
  height: 40px;
  cursor: pointer;
  border-radius: 4px;
}

.form-actions {
  margin-top: 10px;
}
</style>
```

#### 5. RegisterForm.vue 注册表单组件
```vue
<template>
  <form @submit.prevent="handleSubmit" class="register-form">
    <div class="form-item">
      <label for="username">用户名</label>
      <input 
        type="text" 
        id="username" 
        v-model="form.username" 
        placeholder="请输入用户名"
        required
        minlength="3"
        maxlength="50"
      />
    </div>
    
    <div class="form-item">
      <label for="email">邮箱</label>
      <input 
        type="email" 
        id="email" 
        v-model="form.email" 
        placeholder="请输入邮箱"
        required
      />
    </div>
    
    <div class="form-item">
      <label for="password">密码</label>
      <input 
        type="password" 
        id="password" 
        v-model="form.password" 
        placeholder="请输入密码"
        required
        minlength="6"
      />
    </div>
    
    <div class="form-item captcha-item">
      <label for="code">验证码</label>
      <div class="captcha-input">
        <input 
          type="text" 
          id="code" 
          v-model="form.code" 
          placeholder="请输入验证码"
          required
        />
        <BaseButton 
          type="primary" 
          :disabled="countdown > 0" 
          @click="sendCode"
        >
          {{ countdown > 0 ? `${countdown}s后重试` : '发送验证码' }}
        </BaseButton>
      </div>
    </div>
    
    <div class="form-item">
      <label for="nickname">昵称（可选）</label>
      <input 
        type="text" 
        id="nickname" 
        v-model="form.nickname" 
        placeholder="请输入昵称"
      />
    </div>
    
    <div class="form-actions">
      <BaseButton type="primary" native-type="submit" :loading="loading">注册</BaseButton>
    </div>
  </form>
</template>

<script setup>
import { ref } from 'vue'
import BaseButton from '../common/BaseButton.vue'
import request from '../../utils/request'

const emit = defineEmits(['register-success'])

const form = ref({
  username: '',
  email: '',
  password: '',
  code: '',
  nickname: ''
})

const loading = ref(false)
const countdown = ref(0)
let countdownTimer = null

const sendCode = async () => {
  if (!form.value.email) {
    alert('请输入邮箱')
    return
  }
  
  try {
    await request.post('/api/v1/auth/send-code', {
      email: form.value.email,
      type: 'register'
    })
    alert('验证码发送成功')
    startCountdown()
  } catch (error) {
    console.error('发送验证码失败:', error)
    alert(error.response?.data?.message || '发送验证码失败')
  }
}

const startCountdown = () => {
  countdown.value = 60
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(countdownTimer)
    }
  }, 1000)
}

const handleSubmit = async () => {
  loading.value = true
  try {
    await request.post('/api/v1/auth/register', form.value)
    emit('register-success')
  } catch (error) {
    console.error('注册失败:', error)
    alert(error.response?.data?.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.form-item input {
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
}

.captcha-item {
  position: relative;
}

.captcha-input {
  display: flex;
  gap: 10px;
}

.captcha-input input {
  flex: 1;
}

.form-actions {
  margin-top: 10px;
}
</style>
```

## 验收标准
1. 登录页面创建成功
2. 注册页面创建成功
3. 个人主页创建成功
4. 登录表单组件创建成功
5. 注册表单组件创建成功
6. 实现了验证码功能
7. 实现了邮箱验证码功能
8. 页面样式美观，交互流畅

## 关联 Skill
前置：Skill-040-SpringSecurity配置

后置：Skill-031-日记列表页