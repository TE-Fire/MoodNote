# Skill: 日记详情页

## 触发条件
当前端需要创建日记详情页面，展示单篇日记的完整内容时。

## 前置依赖
- Skill-032-日记创建编辑页

## 执行规范

### 文件位置
- 页面文件：`moodnote-web/src/views/`

### 命名规范
- 文件名称：`DiaryDetail.vue`

### 代码规范
- 使用 Vue 3 的 Composition API
- 使用 `<script setup>` 语法
- 集成 Pinia 状态管理
- 实现返回和编辑功能

### 依赖引入
- Vue 3 依赖
- Pinia 依赖
- Day.js 依赖

## 代码模板

### 模板说明
创建日记详情页面，展示单篇日记的完整内容，支持返回和编辑功能。

### 代码示例

#### DiaryDetail.vue 日记详情页
```vue
<template>
  <div class="diary-detail">
    <div class="diary-detail__header">
      <BaseButton @click="goBack">返回</BaseButton>
      <div class="header-actions">
        <BaseButton @click="navigateToEdit">编辑</BaseButton>
        <BaseButton type="danger" @click="deleteDiary">删除</BaseButton>
      </div>
    </div>
    
    <div class="diary-detail__content" v-if="diaryStore.currentDiary">
      <h1 class="diary-title">{{ diaryStore.currentDiary.title }}</h1>
      
      <div class="diary-meta">
        <span class="meta-item">
          <strong>心情：</strong>{{ getMoodText(diaryStore.currentDiary.moodType) }}
        </span>
        <span class="meta-item">
          <strong>天气：</strong>{{ getWeatherText(diaryStore.currentDiary.weatherType) }}
        </span>
        <span class="meta-item">
          <strong>城市：</strong>{{ diaryStore.currentDiary.city || '未知' }}
        </span>
        <span class="meta-item">
          <strong>创建时间：</strong>{{ formatDate(diaryStore.currentDiary.createTime) }}
        </span>
        <span class="meta-item" v-if="diaryStore.currentDiary.updateTime">
          <strong>更新时间：</strong>{{ formatDate(diaryStore.currentDiary.updateTime) }}
        </span>
        <span class="meta-item private" v-if="diaryStore.currentDiary.isPrivate === 1">
          🔒 私密日记
        </span>
      </div>
      
      <div class="diary-tags" v-if="diaryStore.currentDiary.tags && diaryStore.currentDiary.tags.length > 0">
        <span 
          v-for="tag in diaryStore.currentDiary.tags" 
          :key="tag.id"
          class="tag"
          :style="{ backgroundColor: tag.color + '20', color: tag.color }"
        >
          {{ tag.name }}
        </span>
      </div>
      
      <div class="diary-body">
        {{ diaryStore.currentDiary.content }}
      </div>
    </div>
    
    <BaseEmpty v-else text="加载中..." />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useDiaryStore } from '../stores/diary'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()
const diaryStore = useDiaryStore()

const diaryId = route.params.id

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

const getMoodText = (moodType) => {
  const moodMap = {
    1: '开心',
    2: '平静',
    3: '难过',
    4: '焦虑',
    5: '生气'
  }
  return moodMap[moodType] || '未知'
}

const getWeatherText = (weatherType) => {
  const weatherMap = {
    1: '晴',
    2: '多云',
    3: '阴',
    4: '雨',
    5: '雪'
  }
  return weatherMap[weatherType] || '未知'
}

const goBack = () => {
  router.push('/')
}

const navigateToEdit = () => {
  router.push(`/diary/edit/${diaryId}`)
}

const deleteDiary = async () => {
  if (confirm('确定要删除这篇日记吗？')) {
    try {
      await diaryStore.deleteDiary(diaryId)
      router.push('/')
    } catch (error) {
      console.error('删除日记失败:', error)
    }
  }
}

const loadDiary = async () => {
  await diaryStore.fetchDiaryById(diaryId)
}

onMounted(() => {
  loadDiary()
})
</script>

<style scoped>
.diary-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.diary-detail__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.diary-detail__content {
  background-color: white;
  padding: 30px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.diary-title {
  margin: 0 0 20px 0;
  font-size: 24px;
  font-weight: 500;
  color: #303133;
  text-align: center;
}

.diary-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
}

.meta-item {
  display: flex;
  align-items: center;
}

.meta-item.private {
  color: #f56c6c;
  font-weight: 500;
}

.diary-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
}

.tag {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
}

.diary-body {
  font-size: 16px;
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
}
</style>
```

## 验收标准
1. 日记详情页创建成功
2. 展示了日记的完整内容
3. 展示了日记的元数据（心情、天气、城市、时间等）
4. 展示了日记的标签
5. 实现了返回、编辑和删除功能
6. 页面样式美观，排版合理

## 关联 Skill
前置：Skill-032-日记创建编辑页

后置：Skill-034-心情日历页