<template>
  <div class="stats-calendar-page">
    <div class="container">
      <h1>心情日历</h1>
      
      <div class="calendar-container">
        <div class="month-nav">
          <button class="nav-btn" @click="prevMonth">‹</button>
          <span class="month-title">{{ currentMonth }}</span>
          <button class="nav-btn" @click="nextMonth">›</button>
        </div>
        
        <div class="weekdays">
          <div v-for="day in weekdays" :key="day" class="weekday">{{ day }}</div>
        </div>
        
        <div class="days-grid">
          <div 
            v-for="(day, index) in calendarDays" 
            :key="index" 
            class="day-cell"
            :class="{ 
              'other-month': !day.currentMonth,
              'today': day.isToday,
              'has-diary': day.hasDiary
            }"
          >
            <span class="day-number">{{ day.date }}</span>
            <span v-if="day.hasDiary" class="mood-indicator">{{ day.mood }}</span>
          </div>
        </div>
      </div>
      
      <div class="stats-summary">
        <h2>本月统计</h2>
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalDays }}</div>
            <div class="stat-label">记录天数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.happyDays }}</div>
            <div class="stat-label">开心天数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.sadDays }}</div>
            <div class="stat-label">难过天数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.avgMood }}</div>
            <div class="stat-label">平均心情</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'

const weekdays = ['日', '一', '二', '三', '四', '五', '六']

const currentDate = ref(new Date())

const currentMonth = computed(() => {
  return currentDate.value.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long' })
})

const calendarDays = computed(() => {
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth()
  
  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  
  const days = []
  
  // 上月填充
  const startDay = firstDay.getDay()
  const prevMonthLastDay = new Date(year, month, 0).getDate()
  for (let i = startDay - 1; i >= 0; i--) {
    days.push({
      date: prevMonthLastDay - i,
      currentMonth: false,
      isToday: false,
      hasDiary: Math.random() > 0.7,
      mood: getRandomMood()
    })
  }
  
  // 当月天数
  const today = new Date()
  for (let i = 1; i <= lastDay.getDate(); i++) {
    const isToday = today.getFullYear() === year && 
                    today.getMonth() === month && 
                    today.getDate() === i
    days.push({
      date: i,
      currentMonth: true,
      isToday,
      hasDiary: Math.random() > 0.4,
      mood: getRandomMood()
    })
  }
  
  // 下月填充
  const remainingDays = 42 - days.length
  for (let i = 1; i <= remainingDays; i++) {
    days.push({
      date: i,
      currentMonth: false,
      isToday: false,
      hasDiary: Math.random() > 0.7,
      mood: getRandomMood()
    })
  }
  
  return days
})

const stats = reactive({
  totalDays: 15,
  happyDays: 8,
  sadDays: 3,
  avgMood: '😊'
})

const getRandomMood = () => {
  const moods = ['😊', '😢', '😠', '😰', '😌', '🤩']
  return moods[Math.floor(Math.random() * moods.length)]
}

const prevMonth = () => {
  const newDate = new Date(currentDate.value)
  newDate.setMonth(newDate.getMonth() - 1)
  currentDate.value = newDate
}

const nextMonth = () => {
  const newDate = new Date(currentDate.value)
  newDate.setMonth(newDate.getMonth() + 1)
  currentDate.value = newDate
}
</script>

<style scoped>
.stats-calendar-page {
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

.calendar-container {
  background: #fff;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  margin-bottom: 30px;
}

.month-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.nav-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: #f5f5f5;
  border-radius: 50%;
  font-size: 1.5rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.nav-btn:hover {
  background: #eee;
}

.month-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #333;
}

.weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 15px;
}

.weekday {
  text-align: center;
  padding: 10px 0;
  color: #888;
  font-weight: 500;
}

.days-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 5px;
}

.day-cell {
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.day-cell:hover {
  background: #f5f5f5;
}

.day-cell.other-month {
  color: #ccc;
}

.day-cell.today {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.day-cell.has-diary {
  background: #e8f5e9;
}

.day-cell.today.has-diary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.day-number {
  font-size: 0.9rem;
}

.mood-indicator {
  font-size: 0.8rem;
  margin-top: 2px;
}

.stats-summary {
  background: #fff;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.stats-summary h2 {
  margin: 0 0 25px;
  color: #333;
  text-align: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 0.9rem;
}

@media (max-width: 480px) {
  .calendar-container, .stats-summary {
    padding: 20px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
