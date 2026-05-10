<template>
  <div class="stats-trend-page">
    <div class="container">
      <h1>心情趋势</h1>
      
      <div class="chart-container">
        <h2>近7天心情变化</h2>
        <div class="chart">
          <div class="chart-bars">
            <div 
              v-for="(item, index) in weeklyData" 
              :key="index" 
              class="bar-wrapper"
            >
              <div 
                class="bar" 
                :style="{ height: item.percentage + '%' }"
                :class="getMoodClass(item.mood)"
              ></div>
              <span class="bar-label">{{ item.day }}</span>
              <span class="bar-mood">{{ item.mood }}</span>
            </div>
          </div>
          <div class="chart-y-axis">
            <span>100%</span>
            <span>50%</span>
            <span>0%</span>
          </div>
        </div>
      </div>
      
      <div class="mood-distribution">
        <h2>心情分布</h2>
        <div class="distribution-grid">
          <div 
            v-for="item in moodDistribution" 
            :key="item.mood" 
            class="distribution-item"
          >
            <span class="mood-emoji">{{ item.emoji }}</span>
            <span class="mood-label">{{ item.label }}</span>
            <div class="progress-bar">
              <div 
                class="progress-fill" 
                :style="{ width: item.percentage + '%' }"
                :class="getMoodClass(item.mood)"
              ></div>
            </div>
            <span class="mood-percentage">{{ item.percentage }}%</span>
          </div>
        </div>
      </div>
      
      <div class="insights">
        <h2>洞察分析</h2>
        <div class="insight-cards">
          <div class="insight-card">
            <div class="insight-icon">📊</div>
            <div class="insight-content">
              <h3>本周心情指数</h3>
              <p>您本周的整体心情指数为 <strong>78%</strong>，继续保持！</p>
            </div>
          </div>
          <div class="insight-card">
            <div class="insight-icon">⏰</div>
            <div class="insight-content">
              <h3>最佳记录时间</h3>
              <p>您通常在 <strong>晚上9点</strong> 记录心情，继续保持这个习惯！</p>
            </div>
          </div>
          <div class="insight-card">
            <div class="insight-icon">🎯</div>
            <div class="insight-content">
              <h3>连续记录</h3>
              <p>您已连续记录 <strong>15天</strong>，太棒了！</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const weeklyData = reactive([
  { day: '周一', mood: 'happy', percentage: 85 },
  { day: '周二', mood: 'sad', percentage: 40 },
  { day: '周三', mood: 'peaceful', percentage: 70 },
  { day: '周四', mood: 'excited', percentage: 90 },
  { day: '周五', mood: 'happy', percentage: 80 },
  { day: '周六', mood: 'anxious', percentage: 55 },
  { day: '周日', mood: 'happy', percentage: 88 }
])

const moodDistribution = reactive([
  { mood: 'happy', emoji: '😊', label: '开心', percentage: 45 },
  { mood: 'peaceful', emoji: '😌', label: '平静', percentage: 25 },
  { mood: 'sad', emoji: '😢', label: '难过', percentage: 15 },
  { mood: 'excited', emoji: '🤩', label: '兴奋', percentage: 10 },
  { mood: 'anxious', emoji: '😰', label: '焦虑', percentage: 3 },
  { mood: 'angry', emoji: '😠', label: '生气', percentage: 2 }
])

const getMoodClass = (mood) => {
  const moodClasses = {
    happy: 'mood-happy',
    sad: 'mood-sad',
    angry: 'mood-angry',
    anxious: 'mood-anxious',
    peaceful: 'mood-peaceful',
    excited: 'mood-excited'
  }
  return moodClasses[mood] || 'mood-happy'
}
</script>

<style scoped>
.stats-trend-page {
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

.chart-container, .mood-distribution, .insights {
  background: #fff;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  margin-bottom: 30px;
}

h2 {
  margin: 0 0 25px;
  color: #333;
}

.chart {
  display: flex;
  align-items: flex-end;
  height: 250px;
}

.chart-bars {
  flex: 1;
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 100%;
  padding-right: 20px;
}

.bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.bar {
  width: 40px;
  border-radius: 8px 8px 0 0;
  transition: height 0.3s ease;
  margin-bottom: 10px;
}

.bar-label {
  font-size: 0.8rem;
  color: #666;
}

.bar-mood {
  font-size: 1.2rem;
  margin-top: 5px;
}

.chart-y-axis {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: #888;
  font-size: 0.75rem;
  padding-bottom: 45px;
}

.mood-distribution {
  padding-bottom: 20px;
}

.distribution-grid {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.distribution-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.mood-emoji {
  font-size: 1.2rem;
  width: 24px;
}

.mood-label {
  width: 50px;
  color: #666;
}

.progress-bar {
  flex: 1;
  height: 12px;
  background: #f0f0f0;
  border-radius: 6px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 6px;
  transition: width 0.3s ease;
}

.mood-percentage {
  width: 50px;
  text-align: right;
  font-weight: 600;
  color: #333;
}

/* 心情颜色 */
.mood-happy { background: linear-gradient(180deg, #4CAF50, #8BC34A); }
.mood-sad { background: linear-gradient(180deg, #2196F3, #64B5F6); }
.mood-angry { background: linear-gradient(180deg, #F44336, #EF9A9A); }
.mood-anxious { background: linear-gradient(180deg, #FF9800, #FFCC80); }
.mood-peaceful { background: linear-gradient(180deg, #9C27B0, #CE93D8); }
.mood-excited { background: linear-gradient(180deg, #E91E63, #F48FB1); }

.insights {
  padding-bottom: 20px;
}

.insight-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.insight-card {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 15px;
}

.insight-icon {
  font-size: 1.8rem;
}

.insight-content h3 {
  margin: 0 0 8px;
  color: #333;
  font-size: 1rem;
}

.insight-content p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
  line-height: 1.5;
}

.insight-content strong {
  color: #667eea;
}

@media (max-width: 480px) {
  .chart-container, .mood-distribution, .insights {
    padding: 20px;
  }
  
  .bar {
    width: 30px;
  }
  
  .insight-cards {
    grid-template-columns: 1fr;
  }
}
</style>
