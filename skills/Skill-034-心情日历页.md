# Skill: 心情日历页

## 触发条件
当前端需要创建心情日历页面，展示月度心情分布时。

## 前置依赖
- Skill-033-日记详情页

## 执行规范

### 文件位置
- 页面文件：`moodnote-web/src/views/`

### 命名规范
- 文件名称：`StatsCalendar.vue`

### 代码规范
- 使用 Vue 3 的 Composition API
- 使用 `<script setup>` 语法
- 集成 ECharts 进行数据可视化
- 实现月份切换功能

### 依赖引入
- Vue 3 依赖
- ECharts 依赖
- Day.js 依赖

## 代码模板

### 模板说明
创建心情日历页面，使用 ECharts 展示月度心情分布热力图。

### 代码示例

#### StatsCalendar.vue 心情日历页
```vue
<template>
  <div class="stats-calendar">
    <div class="stats-calendar__header">
      <h2>心情日历</h2>
      <BaseButton @click="goBack">返回</BaseButton>
    </div>
    
    <div class="stats-calendar__controls">
      <button class="control-btn" @click="changeMonth(-1)">← 上月</button>
      <h3>{{ currentYear }}年{{ currentMonth }}月</h3>
      <button class="control-btn" @click="changeMonth(1)">下月 →</button>
    </div>
    
    <div class="stats-calendar__chart">
      <div ref="chartRef" class="chart-container"></div>
    </div>
    
    <div class="stats-calendar__legend">
      <div class="legend-item">
        <div class="legend-color" :style="{ backgroundColor: '#F56C6C' }"></div>
        <span>生气</span>
      </div>
      <div class="legend-item">
        <div class="legend-color" :style="{ backgroundColor: '#E6A23C' }"></div>
        <span>焦虑</span>
      </div>
      <div class="legend-item">
        <div class="legend-color" :style="{ backgroundColor: '#F56C6C' }"></div>
        <span>难过</span>
      </div>
      <div class="legend-item">
        <div class="legend-color" :style="{ backgroundColor: '#67C23A' }"></div>
        <span>平静</span>
      </div>
      <div class="legend-item">
        <div class="legend-color" :style="{ backgroundColor: '#409EFF' }"></div>
        <span>开心</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import dayjs from 'dayjs'

const router = useRouter()
const chartRef = ref(null)
let chart = null

const currentDate = ref(dayjs())
const currentYear = ref(currentDate.value.year())
const currentMonth = ref(currentDate.value.month() + 1)
const calendarData = ref([])

const goBack = () => {
  router.push('/')
}

const changeMonth = (delta) => {
  currentDate.value = currentDate.value.add(delta, 'month')
  currentYear.value = currentDate.value.year()
  currentMonth.value = currentDate.value.month() + 1
  fetchCalendarData()
}

const fetchCalendarData = async () => {
  // 模拟数据 - 实际应该从 API 获取
  const year = currentYear.value
  const month = currentMonth.value
  
  // 生成模拟数据
  const data = []
  const daysInMonth = dayjs(`${year}-${month}-01`).daysInMonth()
  
  for (let day = 1; day <= daysInMonth; day++) {
    const date = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`
    const moodType = Math.floor(Math.random() * 5) + 1 // 1-5
    data.push([
      date,
      moodType
    ])
  }
  
  calendarData.value = data
  nextTick(() => {
    renderChart()
  })
}

const renderChart = () => {
  if (!chartRef.value) return
  
  if (chart) {
    chart.dispose()
  }
  
  chart = echarts.init(chartRef.value)
  
  const option = {
    tooltip: {
      position: 'top',
      formatter: function(params) {
        const moodMap = {
          1: '开心',
          2: '平静',
          3: '难过',
          4: '焦虑',
          5: '生气'
        }
        return `${params.data[0]}<br/>心情：${moodMap[params.data[1]]}`
      }
    },
    visualMap: {
      min: 1,
      max: 5,
      type: 'piecewise',
      orient: 'horizontal',
      left: 'center',
      bottom: '10px',
      pieces: [
        { min: 5, max: 5, color: '#F56C6C', label: '生气' },
        { min: 4, max: 4, color: '#E6A23C', label: '焦虑' },
        { min: 3, max: 3, color: '#F56C6C', label: '难过' },
        { min: 2, max: 2, color: '#67C23A', label: '平静' },
        { min: 1, max: 1, color: '#409EFF', label: '开心' }
      ],
      show: false
    },
    calendar: {
      range: [`${currentYear.value}-${currentMonth.value.toString().padStart(2, '0')}`],
      cellSize: ['auto', 13],
      top: 50,
      left: 30,
      right: 30,
      bottom: 30,
      itemStyle: {
        borderWidth: 0.5
      },
      yearLabel: {
        show: false
      }
    },
    series: [{
      type: 'heatmap',
      coordinateSystem: 'calendar',
      data: calendarData.value,
      label: {
        show: false
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  
  chart.setOption(option)
  
  // 响应式调整
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

onMounted(() => {
  fetchCalendarData()
})

watch([currentYear, currentMonth], () => {
  renderChart()
})
</script>

<style scoped>
.stats-calendar {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.stats-calendar__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.stats-calendar__header h2 {
  margin: 0;
  color: #303133;
}

.stats-calendar__controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-bottom: 30px;
}

.control-btn {
  padding: 6px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
  font-size: 14px;
}

.control-btn:hover {
  border-color: #409eff;
  color: #409eff;
}

.stats-calendar__chart {
  background-color: white;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.chart-container {
  width: 100%;
  height: 300px;
}

.stats-calendar__legend {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 2px;
}
</style>
```

## 验收标准
1. 心情日历页创建成功
2. 使用 ECharts 展示了月度心情分布热力图
3. 实现了月份切换功能
4. 提供了心情图例说明
5. 页面样式美观，交互流畅

## 关联 Skill
前置：Skill-033-日记详情页

后置：无