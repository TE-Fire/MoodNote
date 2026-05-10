<template>
  <div class="profile-page">
    <div class="container">
      <h1>个人资料</h1>
      <div class="profile-card">
        <div class="avatar-section">
          <div class="avatar">
            <span>{{ user.username?.charAt(0).toUpperCase() }}</span>
          </div>
          <h2>{{ user.username }}</h2>
          <p>{{ user.email }}</p>
        </div>
        
        <div class="info-section">
          <div class="info-item">
            <label>昵称</label>
            <span>{{ user.nickname || '未设置' }}</span>
          </div>
          <div class="info-item">
            <label>邮箱</label>
            <span>{{ user.email }}</span>
          </div>
          <div class="info-item">
            <label>手机号</label>
            <span>{{ user.phone || '未绑定' }}</span>
          </div>
          <div class="info-item">
            <label>注册时间</label>
            <span>{{ formatDate(user.createTime) }}</span>
          </div>
        </div>
        
        <div class="actions">
          <button class="btn btn-primary">编辑资料</button>
          <button class="btn btn-secondary" @click="handleLogout">退出登录</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../utils/request'

const router = useRouter()

const user = reactive({
  username: '',
  email: '',
  nickname: '',
  phone: '',
  createTime: ''
})

onMounted(async () => {
  try {
    const response = await api.get('/api/user/profile')
    if (response.data) {
      Object.assign(user, response.data)
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
})

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.container {
  max-width: 600px;
  margin: 0 auto;
}

h1 {
  color: #fff;
  text-align: center;
  margin-bottom: 30px;
  font-size: 2rem;
}

.profile-card {
  background: #fff;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.avatar-section {
  text-align: center;
  padding-bottom: 30px;
  border-bottom: 1px solid #eee;
  margin-bottom: 30px;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 2.5rem;
  color: #fff;
}

.avatar-section h2 {
  margin: 0 0 10px;
  color: #333;
}

.avatar-section p {
  margin: 0;
  color: #666;
}

.info-section {
  margin-bottom: 30px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item label {
  color: #888;
  font-weight: 500;
}

.info-item span {
  color: #333;
}

.actions {
  display: flex;
  gap: 15px;
}

.btn {
  flex: 1;
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background: #f5f5f5;
  color: #666;
}

.btn-secondary:hover {
  background: #eee;
}

@media (max-width: 480px) {
  .actions {
    flex-direction: column;
  }
  
  .profile-card {
    padding: 25px;
  }
}
</style>
