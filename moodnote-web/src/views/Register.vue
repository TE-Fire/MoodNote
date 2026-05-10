<template>
  <div class="auth-page">
    <div class="background-image"></div>
    <div class="overlay"></div>
    <div class="auth-card">
      <div class="title-section">
        <h1>Register</h1>
      </div>
      <div class="steps-indicator">
        <div class="step" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
          <span class="step-number">1</span>
          <span class="step-label">注册</span>
        </div>
        <div class="step-line" :class="{ active: currentStep > 1 }"></div>
        <div class="step" :class="{ active: currentStep >= 2 }">
          <span class="step-number">2</span>
          <span class="step-label">完成</span>
        </div>
      </div>
      <form @submit.prevent="handleRegister" class="auth-form" v-if="currentStep === 1">
        <div class="form-group">
          <input
            v-model="form.username"
            type="text"
            placeholder="Enter your username"
            class="form-input"
            :class="{ 'error': errors.username }"
          />
          <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
        </div>
        <div class="form-group">
          <input
            v-model="form.email"
            type="email"
            placeholder="Enter your email"
            class="form-input"
            :class="{ 'error': errors.email }"
          />
          <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
        </div>
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
        <div class="form-group">
          <input
            v-model="form.confirmPassword"
            type="password"
            placeholder="Confirm your password"
            class="form-input"
            :class="{ 'error': errors.confirmPassword }"
          />
          <span v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</span>
        </div>
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
        <div class="form-group email-code-group">
          <div class="email-code-input-wrapper">
            <input
              v-model="form.emailCode"
              type="text"
              placeholder="Enter email verification code"
              class="form-input"
              :class="{ 'error': errors.emailCode }"
              maxlength="6"
              :disabled="!emailCodeSent"
            />
            <span v-if="errors.emailCode" class="error-message">{{ errors.emailCode }}</span>
          </div>
          <button
            type="button"
            class="send-code-btn"
            :disabled="countdown > 0 || loading.sendCode"
            @click="handleSendCode"
          >
            <span v-if="loading.sendCode" class="spinner small"></span>
            <span>{{ countdown > 0 ? `Resend in ${countdown}s` : 'Get Code' }}</span>
          </button>
        </div>
        <button type="submit" class="submit-btn" :disabled="loading.register || !emailCodeSent">
          <span v-if="loading.register" class="spinner"></span>
          <span>{{ loading.register ? 'Registering...' : 'Register' }}</span>
        </button>
      </form>
      <div class="success-section" v-if="currentStep === 2">
        <div class="success-icon">✓</div>
        <h2>Registration Successful!</h2>
        <p>Your account has been created successfully.</p>
        <router-link to="/login" class="submit-btn">Go to Login</router-link>
      </div>
      <div class="login-link" v-if="currentStep !== 2">
        <span>Already have an account?</span>
        <router-link to="/login" class="link">Login</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCaptcha, sendCode, register } from '../api/auth'

const router = useRouter()

const currentStep = ref(1)
const emailCodeSent = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  captcha: '',
  captchaKey: '',
  emailCode: ''
})

const errors = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  captcha: '',
  emailCode: ''
})

const captchaImage = ref('')

const loading = reactive({
  sendCode: false,
  register: false
})

const countdown = ref(0)
let countdownTimer = null

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

const refreshCaptcha = () => {
  form.captcha = ''
  errors.captcha = ''
  fetchCaptcha()
}

const isValidEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

const validateForSendCode = () => {
  errors.username = ''
  errors.email = ''
  errors.password = ''
  errors.confirmPassword = ''
  errors.captcha = ''

  let isValid = true

  if (!form.username.trim()) {
    errors.username = 'Please enter your username'
    isValid = false
  } else if (form.username.length < 3 || form.username.length > 20) {
    errors.username = 'Username must be between 3 and 20 characters'
    isValid = false
  } else if (!/^[a-zA-Z0-9_]+$/.test(form.username)) {
    errors.username = 'Username can only contain letters, numbers and underscores'
    isValid = false
  }

  if (!form.email.trim()) {
    errors.email = 'Please enter your email'
    isValid = false
  } else if (!isValidEmail(form.email)) {
    errors.email = 'Please enter a valid email address'
    isValid = false
  }

  if (!form.password) {
    errors.password = 'Please enter your password'
    isValid = false
  } else if (form.password.length < 6) {
    errors.password = 'Password must be at least 6 characters'
    isValid = false
  }

  if (!form.confirmPassword) {
    errors.confirmPassword = 'Please confirm your password'
    isValid = false
  } else if (form.password !== form.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match'
    isValid = false
  }

  if (!form.captcha.trim()) {
    errors.captcha = 'Please enter captcha code'
    isValid = false
  } else if (form.captcha.length !== 4) {
    errors.captcha = 'Captcha must be 4 characters'
    isValid = false
  }

  return isValid
}

const validateForRegister = () => {
  if (!validateForSendCode()) return false

  errors.emailCode = ''

  if (!form.emailCode.trim()) {
    errors.emailCode = 'Please enter email verification code'
    return false
  } else if (form.emailCode.length !== 6) {
    errors.emailCode = 'Email code must be 6 characters'
    return false
  }

  return true
}

const handleSendCode = async () => {
  if (!validateForSendCode()) return

  loading.sendCode = true

  try {
    await sendCode({
      email: form.email,
      type: 'register',
      captcha: form.captcha,
      captchaKey: form.captchaKey
    })

    emailCodeSent.value = true
    form.emailCode = ''
    startCountdown()
  } catch (error) {
    console.error('发送验证码失败:', error)
    errors.email = ''
    errors.password = ''
    errors.confirmPassword = ''
    if (error.response?.data?.code === 1004) {
      errors.captcha = error.response.data.message || 'Invalid captcha code'
      refreshCaptcha()
    } else {
      errors.email = error.response?.data?.message || 'Failed to send verification code'
    }
  } finally {
    loading.sendCode = false
  }
}

const handleRegister = async () => {
  if (!validateForRegister()) return

  loading.register = true

  try {
    await register({
      username: form.username,
      email: form.email,
      password: form.password,
      code: form.emailCode
    })

    currentStep.value = 2
    stopCountdown()
  } catch (error) {
    console.error('注册失败:', error)
    errors.username = ''
    errors.password = ''
    errors.confirmPassword = ''
    errors.captcha = ''
    
    const errorCode = error.response?.data?.code
    const errorMessage = error.response?.data?.message
    
    if (errorCode === 1005) {
      errors.emailCode = errorMessage || 'Invalid email verification code'
    } else {
      errors.email = errorMessage || 'Registration failed'
    }
  } finally {
    loading.register = false
  }
}

const startCountdown = () => {
  countdown.value = 60
  stopCountdown()
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      stopCountdown()
    }
  }, 1000)
}

const stopCountdown = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
}

onUnmounted(() => {
  stopCountdown()
})

onMounted(() => {
  fetchCaptcha()
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

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  z-index: 1;
}

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

.title-section {
  text-align: center;
  margin-bottom: 25px;
}

.title-section h1 {
  font-size: 2.5rem;
  font-weight: 300;
  color: #fff;
  margin: 0;
  letter-spacing: 2px;
}

.steps-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.step-number {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  color: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.step.active .step-number {
  background: #fff;
  color: #667eea;
}

.step.completed .step-number {
  background: #4CAF50;
  color: #fff;
}

.step-label {
  font-size: 0.7rem;
  color: rgba(255, 255, 255, 0.6);
}

.step.active .step-label {
  color: #fff;
}

.step-line {
  width: 50px;
  height: 2px;
  background: rgba(255, 255, 255, 0.3);
  margin: 0 10px;
  margin-bottom: 20px;
  transition: background 0.3s ease;
}

.step-line.active {
  background: #4CAF50;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-group {
  position: relative;
}

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

.form-input:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.error-message {
  display: block;
  color: #ff4444;
  font-size: 0.8rem;
  margin-top: 8px;
}

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

.email-code-group {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.email-code-input-wrapper {
  flex: 1;
}

.send-code-btn {
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  color: #fff;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.send-code-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.3);
  border-color: #fff;
}

.send-code-btn:disabled {
  background: rgba(255, 255, 255, 0.1);
  cursor: not-allowed;
  opacity: 0.6;
}

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
  margin-top: 5px;
  text-decoration: none;
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

.back-btn {
  width: 100%;
  padding: 12px;
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 25px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 10px;
}

.back-btn:hover {
  border-color: #fff;
  color: #fff;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(0, 0, 0, 0.1);
  border-top-color: #333;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.spinner.small {
  width: 14px;
  height: 14px;
  border-width: 1.5px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.success-section {
  text-align: center;
  padding: 30px 0;
}

.success-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #4CAF50;
  color: #fff;
  font-size: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 25px;
}

.success-section h2 {
  color: #fff;
  margin: 0 0 10px;
}

.success-section p {
  color: rgba(255, 255, 255, 0.8);
  margin: 0 0 25px;
}

.login-link {
  text-align: center;
  margin-top: 25px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
}

.login-link .link {
  color: #fff;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
  transition: color 0.3s ease;
}

.login-link .link:hover {
  color: rgba(255, 255, 255, 0.8);
}

@media (max-width: 480px) {
  .auth-card {
    margin: 0 20px;
    padding: 30px 25px;
  }

  .title-section h1 {
    font-size: 2rem;
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

  .steps-indicator {
    flex-wrap: wrap;
    gap: 5px;
  }

  .step-line {
    width: 30px;
    margin: 0 5px;
    margin-bottom: 15px;
  }

  .email-code-group {
    flex-direction: column;
  }

  .send-code-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
