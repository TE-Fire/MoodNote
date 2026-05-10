<template>
  <div class="diary-detail-page">
    <div class="container">
      <div v-if="diary" class="diary-card">
        <div class="header">
          <h1>{{ diary.title || '无标题' }}</h1>
          <div class="meta">
            <span class="mood">{{ getMoodEmoji(diary.mood) }}</span>
            <span class="date">{{ formatDate(diary.createTime) }}</span>
            <span v-if="diary.weather" class="weather">{{ getWeatherEmoji(diary.weather) }}</span>
          </div>
        </div>
        
        <div class="content">
          <p>{{ diary.content }}</p>
        </div>
        
        <div class="actions">
          <router-link :to="`/diary/edit/${diary.id}`" class="btn btn-primary">编辑</router-link>
          <button class="btn btn-danger" @click="handleDelete">删除</button>
        </div>
      </div>
      
      <div v-else class="loading">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../utils/request'

const route = useRoute()
const router = useRouter()

const diary = ref(null)

onMounted(async () => {
  const id = route.params.id
  try {
    const response = await api.get(`/api/diary/${id}`)
    diary.value = response.data
  } catch (error) {
    console.error('获取日记失败:', error)
  }
})

const getMoodEmoji = (mood) => {
  const moodMap = {
    happy: '😊',
    sad: '😢',
    angry: '😠',
    anxious: '😰',
    peaceful: '😌',
    excited: '🤩'
  }
  return moodMap[mood] || '😐'
}

const getWeatherEmoji = (weather) => {
  const weatherMap = {
    sunny: '☀️',
    cloudy: '☁️',
    rainy: '🌧️',
    snowy: '❄️',
    foggy: '🌫️'
  }
  return weatherMap[weather] || ''
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const handleDelete = async () => {
  if (!confirm('确定要删除这篇日记吗？')) return
  
  try {
    await api.delete(`/api/diary/${diary.value.id}`)
    alert('删除成功')
    router.push('/')
  } catch (error) {
    console.error('删除失败:', error)
  }
}
</script>

<style scoped>
.diary-detail-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.container {
  max-width: 800px;
  margin: 0 auto;
}

.diary-card {
  background: #fff;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.header h1 {
  margin: 0 0 15px;
  color: #333;
  font-size: 1.8rem;
}

.meta {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #666;
  font-size: 0.9rem;
}

.mood, .weather {
  font-size: 1.2rem;
}

.content {
  margin: 30px 0;
  padding-bottom: 30px;
  border-bottom: 1px solid #eee;
}

.content p {
  margin: 0;
  line-height: 1.8;
  color: #444;
  font-size: 1.1rem;
  white-space: pre-wrap;
}

.actions {
  display: flex;
  gap: 15px;
}

.btn {
  padding: 12px 25px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-danger {
  background: #ff4444;
  color: #fff;
}

.btn-danger:hover {
  background: #cc0000;
}

.loading {
  text-align: center;
  padding: 100px 0;
  color: #fff;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 480px) {
  .diary-card {
    padding: 25px;
  }
  
  .header h1 {
    font-size: 1.5rem;
  }
  
  .actions {
    flex-direction: column;
  }
}
</style>
