# Skill: 日记列表页

## 触发条件
当前端需要创建日记列表页面，展示所有日记并支持筛选和分页时。

## 前置依赖
- Skill-030-全局组件注册

## 执行规范

### 文件位置
- 页面文件：`moodnote-web/src/views/`

### 命名规范
- 文件名称：`Home.vue`

### 代码规范
- 使用 Vue 3 的 Composition API
- 使用 `<script setup>` 语法
- 集成 Pinia 状态管理
- 实现分页和筛选功能

### 依赖引入
- Vue 3 依赖
- Pinia 依赖
- Day.js 依赖

## 代码模板

### 模板说明
创建日记列表页面，展示所有日记，支持分页、筛选和搜索功能。

### 代码示例

#### Home.vue 日记列表页
```vue
<template>
  <div class="home">
    <div class="home__header">
      <h1>晚风记事</h1>
      <BaseButton type="primary" @click="navigateToCreate">
        写日记
      </BaseButton>
    </div>
    
    <div class="home__filter">
      <div class="filter-item">
        <label>心情：</label>
        <select v-model="filterForm.moodType">
          <option value="">全部</option>
          <option value="1">开心</option>
          <option value="2">平静</option>
          <option value="3">难过</option>
          <option value="4">焦虑</option>
          <option value="5">生气</option>
        </select>
      </div>
      <div class="filter-item">
        <label>关键词：</label>
        <input type="text" v-model="filterForm.keyword" placeholder="请输入标题或内容关键词" />
      </div>
      <div class="filter-item">
        <BaseButton type="primary" @click="searchDiaries">搜索</BaseButton>
        <BaseButton @click="resetFilter">重置</BaseButton>
      </div>
    </div>
    
    <div class="home__list" v-if="diaryStore.diaries.length > 0">
      <div 
        v-for="diary in diaryStore.diaries" 
        :key="diary.id"
        class="diary-card"
        @click="navigateToDetail(diary.id)"
      >
        <div class="diary-card__header">
          <h3 class="diary-card__title">{{ diary.title }}</h3>
          <span class="diary-card__date">{{ formatDate(diary.createTime) }}</span>
        </div>
        <div class="diary-card__content">{{ truncateContent(diary.content) }}</div>
        <div class="diary-card__footer">
          <div class="diary-card__mood">
            {{ getMoodText(diary.moodType) }}
          </div>
          <div class="diary-card__tags">
            <span 
              v-for="tag in diary.tags" 
              :key="tag.id"
              class="tag"
              :style="{ backgroundColor: tag.color + '20', color: tag.color }"
            >
              {{ tag.name }}
            </span>
          </div>
        </div>
      </div>
    </div>
    
    <BaseEmpty v-else text="暂无日记" />
    
    <div class="home__pagination" v-if="diaryStore.total > 0">
      <span>共 {{ diaryStore.total }} 条记录</span>
      <div class="pagination">
        <button 
          class="page-btn" 
          :disabled="currentPage === 1"
          @click="changePage(currentPage - 1)"
        >
          上一页
        </button>
        <span class="page-info">
          {{ currentPage }} / {{ totalPages }}
        </span>
        <button 
          class="page-btn" 
          :disabled="currentPage === totalPages"
          @click="changePage(currentPage + 1)"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useDiaryStore } from '../stores/diary'
import dayjs from 'dayjs'

const router = useRouter()
const diaryStore = useDiaryStore()

const currentPage = ref(1)
const pageSize = ref(10)

const filterForm = ref({
  moodType: '',
  keyword: ''
})

const totalPages = computed(() => {
  return Math.ceil(diaryStore.total / pageSize.value)
})

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const truncateContent = (content) => {
  if (content.length > 100) {
    return content.substring(0, 100) + '...'
  }
  return content
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

const navigateToCreate = () => {
  router.push('/diary/create')
}

const navigateToDetail = (id) => {
  router.push(`/diary/${id}`)
}

const searchDiaries = () => {
  currentPage.value = 1
  fetchDiaries()
}

const resetFilter = () => {
  filterForm.value = {
    moodType: '',
    keyword: ''
  }
  currentPage.value = 1
  fetchDiaries()
}

const changePage = (page) => {
  currentPage.value = page
  fetchDiaries()
}

const fetchDiaries = async () => {
  await diaryStore.fetchDiaries({
    page: currentPage.value,
    pageSize: pageSize.value,
    moodType: filterForm.value.moodType,
    keyword: filterForm.value.keyword
  })
}

onMounted(() => {
  fetchDiaries()
})
</script>

<style scoped>
.home {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.home__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.home__header h1 {
  margin: 0;
  color: #303133;
}

.home__filter {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-size: 14px;
  color: #606266;
}

.filter-item select,
.filter-item input {
  padding: 6px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
}

.home__list {
  margin-bottom: 20px;
}

.diary-card {
  padding: 16px;
  margin-bottom: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
  transition: all 0.3s;
}

.diary-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.diary-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.diary-card__title {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.diary-card__date {
  font-size: 12px;
  color: #909399;
}

.diary-card__content {
  margin-bottom: 12px;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.diary-card__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.diary-card__mood {
  font-size: 14px;
  color: #409eff;
}

.diary-card__tags {
  display: flex;
  gap: 8px;
}

.tag {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.home__pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-btn {
  padding: 4px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
  font-size: 14px;
}

.page-btn:disabled {
  color: #c0c4cc;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #606266;
}
</style>
```

## 验收标准
1. 日记列表页创建成功
2. 展示了日记列表
3. 实现了分页功能
4. 实现了筛选和搜索功能
5. 页面样式美观，交互流畅

## 关联 Skill
前置：Skill-030-全局组件注册

后置：Skill-032-日记创建编辑页