<template>
  <div class="home">
    <h1>欢迎使用 MoodNote - 晚风记事</h1>
    <p>一个轻量级的心情日记记录应用</p>
    
    <div v-if="userInfo" class="user-info">
      <p>当前用户: {{ userInfo.username }}</p>
      <p>邮箱: {{ userInfo.email }}</p>
    </div>
    <div v-else class="loading">
      <p>加载中...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../utils/request'

const userInfo = ref(null)

onMounted(async () => {
  try {
    // 发起需要认证的 API 请求
    const response = await api.get('/api/user/info')
    userInfo.value = response.data
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
})
</script>

<style scoped>
.home {
  padding: 2rem;
  text-align: center;
}

h1 {
  color: #333;
  margin-bottom: 1rem;
}

p {
  color: #666;
  font-size: 1.1rem;
}
</style>