<template>
  <div class="diary-edit-page">
    <div class="container">
      <h1>编辑日记</h1>
      <form @submit.prevent="handleSubmit" class="diary-form" v-if="diary">
        <div class="form-group">
          <input
            v-model="form.title"
            type="text"
            placeholder="日记标题"
            class="form-input"
          />
        </div>
        
        <div class="form-group">
          <textarea
            v-model="form.content"
            placeholder="写下今天的心情..."
            class="form-textarea"
            rows="8"
          ></textarea>
        </div>
        
        <div class="form-group">
          <label class="label">心情标签</label>
          <div class="mood-tags">
            <button
              v-for="tag in moodTags"
              :key="tag.value"
              type="button"
              class="tag-btn"
              :class="{ active: form.mood === tag.value }"
              @click="form.mood = tag.value"
            >
              {{ tag.emoji }} {{ tag.label }}
            </button>
          </div>
        </div>
        
        <div class="form-group">
          <label class="label">天气</label>
          <select v-model="form.weather" class="form-select">
            <option value="">请选择天气</option>
            <option value="sunny">☀️ 晴天</option>
            <option value="cloudy">☁️ 多云</option>
            <option value="rainy">🌧️ 雨天</option>
            <option value="snowy">❄️ 雪天</option>
            <option value="foggy">🌫️ 雾天</option>
          </select>
        </div>
        
        <div class="actions">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            <span v-if="loading" class="spinner"></span>
            <span>{{ loading ? '保存中...' : '保存修改' }}</span>
          </button>
          <router-link :to="`/diary/${diary.id}`" class="btn btn-secondary">取消</router-link>
        </div>
      </form>
      
      <div v-else class="loading">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../utils/request'

const route = useRoute()
const router = useRouter()

const diary = ref(null)
const loading = ref(false)

const form = reactive({
  title: '',
  content: '',
  mood: 'happy',
  weather: ''
})

const moodTags = [
  { value: 'happy', label: '开心', emoji: '😊' },
  { value: 'sad', label: '难过', emoji: '😢' },
  { value: 'angry', label: '生气', emoji: '😠' },
  { value: 'anxious', label: '焦虑', emoji: '😰' },
  { value: 'peaceful', label: '平静', emoji: '😌' },
  { value: 'excited', label: '兴奋', emoji: '🤩' }
]

onMounted(async () => {
  const id = route.params.id
  try {
    const response = await api.get(`/api/diary/${id}`)
    diary.value = response.data
    form.title = diary.value.title || ''
    form.content = diary.value.content || ''
    form.mood = diary.value.mood || 'happy'
    form.weather = diary.value.weather || ''
  } catch (error) {
    console.error('获取日记失败:', error)
  }
})

const handleSubmit = async () => {
  if (!form.content.trim()) {
    alert('请输入日记内容')
    return
  }
  
  loading.value = true
  
  try {
    await api.put(`/api/diary/${diary.value.id}`, form)
    alert('修改成功')
    router.push(`/diary/${diary.value.id}`)
  } catch (error) {
    console.error('修改失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.diary-edit-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.container {
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  color: #fff;
  text-align: center;
  margin-bottom: 30px;
  font-size: 2rem;
}

.diary-form {
  background: #fff;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.form-group {
  margin-bottom: 25px;
}

.form-input {
  width: 100%;
  padding: 15px;
  border: 2px solid #eee;
  border-radius: 10px;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus {
  border-color: #667eea;
}

.form-textarea {
  width: 100%;
  padding: 15px;
  border: 2px solid #eee;
  border-radius: 10px;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.3s ease;
  resize: vertical;
  box-sizing: border-box;
  font-family: inherit;
}

.form-textarea:focus {
  border-color: #667eea;
}

.label {
  display: block;
  margin-bottom: 12px;
  font-weight: 600;
  color: #333;
}

.mood-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-btn {
  padding: 10px 18px;
  border: 2px solid #eee;
  border-radius: 25px;
  background: #fff;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.tag-btn:hover {
  border-color: #667eea;
}

.tag-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: #fff;
}

.form-select {
  width: 100%;
  padding: 15px;
  border: 2px solid #eee;
  border-radius: 10px;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.3s ease;
  background: #fff;
  cursor: pointer;
}

.form-select:focus {
  border-color: #667eea;
}

.actions {
  display: flex;
  gap: 15px;
}

.btn {
  flex: 1;
  padding: 15px;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
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

.btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading {
  text-align: center;
  padding: 100px 0;
  color: #fff;
}

.loading .spinner {
  width: 40px;
  height: 40px;
  margin: 0 auto 20px;
  border-width: 4px;
}

@media (max-width: 480px) {
  .diary-form {
    padding: 25px;
  }
  
  .actions {
    flex-direction: column;
  }
}
</style>
