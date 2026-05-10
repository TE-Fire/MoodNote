<template>
  <div class="auth-page">
    <!-- 背景图片 -->
    <div class="background-image"></div>

    <!-- 半透明遮罩 -->
    <div class="overlay"></div>

    <!-- 登录卡片 -->
    <div class="auth-card">
      <!-- 标题 -->
      <div class="title-section">
        <h1>Login</h1>
      </div>

      <!-- 表单 -->
      <form @submit.prevent="handleLogin" class="auth-form">
        <!-- 用户名/邮箱输入框 -->
        <div class="form-group">
          <input
            v-model="form.username"
            type="text"
            placeholder="Enter your email"
            class="form-input"
            :class="{ 'error': errors.username }"
          />
          <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
        </div>

        <!-- 密码输入框 -->
        <div class="form-group">
          <input
            v-model="form.password"
            type="password"
            placeholder="Enter your password"
            class="form-input"
            :class="{ 'error': errors.password }"
          />
          <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
        </div>

        <!-- 图形验证码 -->
        <div class="form-group captcha-group">
          <div class="captcha-input-wrapper">
            <input
              v-model="form.captcha"
              type="text"
              placeholder="Enter captcha"
              class="form-input captcha-input"
              :class="{ 'error': errors.captcha }"
              maxlength="4"
            />
            <span v-if="errors.captcha" class="error-message">{{ errors.captcha }}</span>
          </div>
          <div class="captcha-image-wrapper">
            <img
              v-if="captchaImage"
              :src="captchaImage"
              alt="Captcha"
              class="captcha-image"
              @click="refreshCaptcha"
            />
            <div v-else class="captcha-placeholder" @click="refreshCaptcha">
              <span class="loading-text">Loading...</span>
            </div>
          </div>
        </div>

        <!-- 记住我和忘记密码 -->
        <div class="form-options">
          <label class="remember-me">
            <input type="checkbox" v-model="form.rememberMe" />
            <span class="checkmark"></span>
            <span class="label-text">Remember me</span>
          </label>
          <a href="#" class="forgot-password" @click.prevent="handleForgotPassword">
            Forgot password?
          </a>
        </div>

        <!-- 登录按钮 -->
        <button
          type="submit"
          class="submit-btn"
          :disabled="loading"
        >
          <span v-if="loading" class="spinner"></span>
          <span>{{ loading ? 'Logging in...' : 'Log In' }}</span>
        </button>
      </form>

      <!-- 注册链接 -->
      <div class="register-link">
        <span>Don't have an account?</span>
        <router-link to="/register" class="link">Register</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCaptcha, login } from '../api/auth'

const router = useRouter()

// 表单数据
const form = reactive({
  username: '',
  password: '',
  captcha: '',
  captchaKey: '',
  rememberMe: false
})

// 错误信息
const errors = reactive({
  username: '',
  password: '',
  captcha: ''
})

// 图形验证码
const captchaImage = ref('')
const loading = ref(false)

/**
 * 获取图形验证码
 */
const fetchCaptcha = async () => {
  try {
    const response = await getCaptcha()
    if (response.data) {
      form.captchaKey = response.data.captchaKey
      captchaImage.value = response.data.captchaImg
    }
  } catch (error) {
    console.error('获取验证码失败:', error)
  }
}

/**
 * 刷新图形验证码
 */
const refreshCaptcha = () => {
  form.captcha = ''
  errors.captcha = ''
  fetchCaptcha()
}

/**
 * 表单验证
 */
const validateForm = () => {
  // 重置错误
  errors.username = ''
  errors.password = ''
  errors.captcha = ''

  let isValid = true

  // 验证用户名/邮箱
  if (!form.username.trim()) {
    errors.username = 'Please enter your email or username'
    isValid = false
  }

  // 验证密码
  if (!form.password) {
    errors.password = 'Please enter your password'
    isValid = false
  } else if (form.password.length < 6) {
    errors.password = 'Password must be at least 6 characters'
    isValid = false
  }

  // 验证图形验证码
  if (!form.captcha.trim()) {
    errors.captcha = 'Please enter captcha code'
    isValid = false
  } else if (form.captcha.length !== 4) {
    errors.captcha = 'Captcha must be 4 characters'
    isValid = false
  }

  return isValid
}

/**
 * 处理登录
 */
const handleLogin = async () => {
  // 表单验证
  if (!validateForm()) return

  // 设置加载状态
  loading.value = true

  try {
    // 调用登录接口
    const response = await login({
      username: form.username,
      password: form.password,
      captcha: form.captcha,
      captchaKey: form.captchaKey
    })

    // 存储token和用户信息
    if (response.data && response.data.token) {
      localStorage.setItem('token', response.data.token)
      if (response.data.user) {
        localStorage.setItem('user', JSON.stringify(response.data.user))
      }

      // 记住我功能（可选）
      if (form.rememberMe) {
        localStorage.setItem('username', form.username)
      } else {
        localStorage.removeItem('username')
      }

      // 跳转到首页
      router.push('/')
    }
  } catch (error) {
    console.error('登录失败:', error)
    // 错误处理由request.js中的响应拦截器处理
    // 刷新验证码
    if (error.response?.data?.code === 1004) {
      refreshCaptcha()
    }
  } finally {
    // 重置加载状态
    loading.value = false
  }
}

/**
 * 处理忘记密码
 */
const handleForgotPassword = () => {
  // 可扩展：跳转到忘记密码页面或弹出重置密码对话框
  alert('Forgot password feature is under development')
}

// 组件挂载时获取图形验证码
onMounted(() => {
  fetchCaptcha()
  // 如果有记住的用户名，自动填充
  const savedUsername = localStorage.getItem('username')
  if (savedUsername) {
    form.username = savedUsername
    form.rememberMe = true
  }
})
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

/* 背景图片 */
.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('../assets/images/background.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  z-index: 0;
}

/* 半透明遮罩 */
.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  z-index: 1;
}

/* 登录卡片 */
.auth-card {
  position: relative;
  z-index: 2;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

/* 标题区域 */
.title-section {
  text-align: center;
  margin-bottom: 30px;
}

.title-section h1 {
  font-size: 2.5rem;
  font-weight: 300;
  color: #fff;
  margin: 0;
  letter-spacing: 2px;
}

/* 表单 */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 表单组 */
.form-group {
  position: relative;
}

/* 输入框 */
.form-input {
  width: 100%;
  padding: 12px 0;
  background: transparent;
  border: none;
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
  color: #fff;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.form-input:focus {
  border-color: #fff;
}

.form-input.error {
  border-color: #ff4444;
}

/* 错误信息 */
.error-message {
  display: block;
  color: #ff4444;
  font-size: 0.8rem;
  margin-top: 8px;
}

/* 验证码组 */
.captcha-group {
  display: flex;
  gap: 15px;
  align-items: flex-start;
}

.captcha-input-wrapper {
  flex: 1;
}

.captcha-input {
  width: 100%;
}

.captcha-image-wrapper {
  flex-shrink: 0;
}

.captcha-image {
  height: 44px;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: border-color 0.3s ease;
}

.captcha-image:hover {
  border-color: #fff;
}

.captcha-placeholder {
  height: 44px;
  width: 100px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.loading-text {
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.8rem;
}

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

/* 记住我 */
.remember-me {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.remember-me input {
  display: none;
}

.checkmark {
  width: 18px;
  height: 18px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 4px;
  margin-right: 8px;
  position: relative;
  transition: all 0.3s ease;
}

.remember-me input:checked + .checkmark {
  background: rgba(255, 255, 255, 0.2);
  border-color: #fff;
}

.remember-me input:checked + .checkmark::after {
  content: '';
  position: absolute;
  left: 6px;
  top: 2px;
  width: 5px;
  height: 10px;
  border: solid #fff;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.label-text {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
}

/* 忘记密码链接 */
.forgot-password {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.forgot-password:hover {
  color: #fff;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  padding: 14px;
  background: #fff;
  border: none;
  border-radius: 25px;
  color: #333;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 10px;
}

.submit-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.9);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.submit-btn:disabled {
  background: rgba(255, 255, 255, 0.5);
  cursor: not-allowed;
}

/* 加载动画 */
.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(0, 0, 0, 0.1);
  border-top-color: #333;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 注册链接 */
.register-link {
  text-align: center;
  margin-top: 25px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
}

.register-link .link {
  color: #fff;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
  transition: color 0.3s ease;
}

.register-link .link:hover {
  color: rgba(255, 255, 255, 0.8);
}

/* 响应式设计 */
@media (max-width: 480px) {
  .auth-card {
    margin: 0 20px;
    padding: 30px 25px;
  }

  .title-section h1 {
    font-size: 2rem;
  }

  .form-options {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .captcha-group {
    flex-direction: column;
  }

  .captcha-image-wrapper {
    width: 100%;
  }

  .captcha-image,
  .captcha-placeholder {
    width: 100%;
  }
}
</style>
