# Skill: 日记创建编辑页

## 触发条件
当前端需要创建日记创建和编辑页面时。

## 前置依赖
- Skill-031-日记列表页

## 执行规范

### 文件位置
- 页面文件：`moodnote-web/src/views/`

### 命名规范
- 创建页面：`DiaryCreate.vue`
- 编辑页面：`DiaryEdit.vue`

### 代码规范
- 使用 Vue 3 的 Composition API
- 使用 `<script setup>` 语法
- 集成 Pinia 状态管理
- 实现表单验证
- 实现标签选择功能

### 依赖引入
- Vue 3 依赖
- Pinia 依赖

## 代码模板

### 模板说明
创建日记创建和编辑页面，支持表单提交和标签选择。

### 代码示例

#### 1. DiaryCreate.vue 日记创建页
```vue
<template>
  <div class="diary-create">
    <div class="diary-create__header">
      <h2>写日记</h2>
      <BaseButton @click="goBack">返回</BaseButton>
    </div>
    
    <div class="diary-create__form">
      <div class="form-item">
        <label for="title">标题</label>
        <input 
          type="text" 
          id="title" 
          v-model="form.title" 
          placeholder="请输入日记标题"
          maxlength="100"
        />
      </div>
      
      <div class="form-item">
        <label for="content">内容</label>
        <textarea 
          id="content" 
          v-model="form.content" 
          placeholder="请输入日记内容"
          rows="10"
        ></textarea>
      </div>
      
      <div class="form-item">
        <label>心情</label>
        <div class="mood-selector">
          <div 
            v-for="mood in moods" 
            :key="mood.value"
            class="mood-item"
            :class="{ active: form.moodType === mood.value }"
            @click="form.moodType = mood.value"
          >
            {{ mood.label }}
          </div>
        </div>
      </div>
      
      <div class="form-item">
        <label>天气</label>
        <div class="weather-selector">
          <div 
            v-for="weather in weathers" 
            :key="weather.value"
            class="weather-item"
            :class="{ active: form.weatherType === weather.value }"
            @click="form.weatherType = weather.value"
          >
            {{ weather.label }}
          </div>
        </div>
      </div>
      
      <div class="form-item">
        <label for="city">城市</label>
        <input 
          type="text" 
          id="city" 
          v-model="form.city" 
          placeholder="请输入所在城市"
        />
      </div>
      
      <div class="form-item">
        <label>标签</label>
        <div class="tag-selector">
          <div 
            v-for="tag in tagStore.tags" 
            :key="tag.id"
            class="tag-item"
            :class="{ active: selectedTagIds.includes(tag.id) }"
            :style="{ borderColor: tag.color, color: form.tags.includes(tag.id) ? tag.color : '#606266' }"
            @click="toggleTag(tag.id)"
          >
            {{ tag.name }}
          </div>
          <div class="tag-item add-tag" @click="showAddTagDialog = true">
            + 添加标签
          </div>
        </div>
      </div>
      
      <div class="form-item">
        <label>
          <input type="checkbox" v-model="form.isPrivate" />
          私密日记
        </label>
      </div>
      
      <div class="form-actions">
        <BaseButton @click="goBack">取消</BaseButton>
        <BaseButton type="primary" @click="submitForm">保存</BaseButton>
      </div>
    </div>
    
    <!-- 添加标签弹窗 -->
    <div v-if="showAddTagDialog" class="dialog-overlay" @click="showAddTagDialog = false">
      <div class="dialog" @click.stop>
        <div class="dialog-header">
          <h3>添加标签</h3>
          <button class="close-btn" @click="showAddTagDialog = false">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label for="tagName">标签名称</label>
            <input 
              type="text" 
              id="tagName" 
              v-model="newTag.name" 
              placeholder="请输入标签名称"
            />
          </div>
          <div class="form-item">
            <label for="tagColor">标签颜色</label>
            <input 
              type="color" 
              id="tagColor" 
              v-model="newTag.color"
            />
          </div>
        </div>
        <div class="dialog-footer">
          <BaseButton @click="showAddTagDialog = false">取消</BaseButton>
          <BaseButton type="primary" @click="addTag">确定</BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useDiaryStore } from '../stores/diary'
import { useTagStore } from '../stores/tag'

const router = useRouter()
const diaryStore = useDiaryStore()
const tagStore = useTagStore()

const form = ref({
  title: '',
  content: '',
  moodType: 1,
  weatherType: 1,
  city: '',
  isPrivate: 1,
  tags: []
})

const showAddTagDialog = ref(false)
const newTag = ref({
  name: '',
  color: '#409EFF'
})

const moods = [
  { value: 1, label: '开心' },
  { value: 2, label: '平静' },
  { value: 3, label: '难过' },
  { value: 4, label: '焦虑' },
  { value: 5, label: '生气' }
]

const weathers = [
  { value: 1, label: '晴' },
  { value: 2, label: '多云' },
  { value: 3, label: '阴' },
  { value: 4, label: '雨' },
  { value: 5, label: '雪' }
]

const selectedTagIds = ref([])

const toggleTag = (tagId) => {
  const index = form.value.tags.indexOf(tagId)
  if (index === -1) {
    form.value.tags.push(tagId)
  } else {
    form.value.tags.splice(index, 1)
  }
}

const addTag = async () => {
  if (!newTag.value.name) return
  
  try {
    await tagStore.createTag(newTag.value)
    await tagStore.fetchTags()
    showAddTagDialog.value = false
    newTag.value = {
      name: '',
      color: '#409EFF'
    }
  } catch (error) {
    console.error('添加标签失败:', error)
  }
}

const submitForm = async () => {
  if (!form.value.title || !form.value.content) {
    alert('标题和内容不能为空')
    return
  }
  
  try {
    await diaryStore.createDiary({
      ...form.value,
      tagIds: form.value.tags
    })
    router.push('/')
  } catch (error) {
    console.error('创建日记失败:', error)
  }
}

const goBack = () => {
  router.push('/')
}

onMounted(async () => {
  await tagStore.fetchTags()
})
</script>

<style scoped>
.diary-create {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.diary-create__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.diary-create__header h2 {
  margin: 0;
  color: #303133;
}

.diary-create__form {
  background-color: white;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.form-item input[type="text"],
.form-item textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
}

.form-item textarea {
  min-height: 200px;
}

.mood-selector,
.weather-selector {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.mood-item,
.weather-item {
  padding: 8px 16px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.mood-item:hover,
.weather-item:hover {
  border-color: #409eff;
  color: #409eff;
}

.mood-item.active,
.weather-item.active {
  background-color: #409eff;
  color: white;
  border-color: #409eff;
}

.tag-selector {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.tag-item {
  padding: 6px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 16px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.tag-item:hover {
  border-color: #409eff;
  color: #409eff;
}

.tag-item.active {
  background-color: #409eff;
  color: white;
  border-color: #409eff;
}

.tag-item.add-tag {
  color: #409eff;
  border-style: dashed;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 30px;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background-color: white;
  border-radius: 4px;
  width: 400px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.dialog-header h3 {
  margin: 0;
  color: #303133;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #909399;
}

.dialog-body {
  padding: 16px;
}

.dialog-footer {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  padding: 16px;
  border-top: 1px solid #e4e7ed;
}
</style>
```

#### 2. DiaryEdit.vue 日记编辑页
```vue
<template>
  <div class="diary-edit">
    <div class="diary-edit__header">
      <h2>编辑日记</h2>
      <BaseButton @click="goBack">返回</BaseButton>
    </div>
    
    <div class="diary-edit__form" v-if="diaryStore.currentDiary">
      <div class="form-item">
        <label for="title">标题</label>
        <input 
          type="text" 
          id="title" 
          v-model="form.title" 
          placeholder="请输入日记标题"
          maxlength="100"
        />
      </div>
      
      <div class="form-item">
        <label for="content">内容</label>
        <textarea 
          id="content" 
          v-model="form.content" 
          placeholder="请输入日记内容"
          rows="10"
        ></textarea>
      </div>
      
      <div class="form-item">
        <label>心情</label>
        <div class="mood-selector">
          <div 
            v-for="mood in moods" 
            :key="mood.value"
            class="mood-item"
            :class="{ active: form.moodType === mood.value }"
            @click="form.moodType = mood.value"
          >
            {{ mood.label }}
          </div>
        </div>
      </div>
      
      <div class="form-item">
        <label>天气</label>
        <div class="weather-selector">
          <div 
            v-for="weather in weathers" 
            :key="weather.value"
            class="weather-item"
            :class="{ active: form.weatherType === weather.value }"
            @click="form.weatherType = weather.value"
          >
            {{ weather.label }}
          </div>
        </div>
      </div>
      
      <div class="form-item">
        <label for="city">城市</label>
        <input 
          type="text" 
          id="city" 
          v-model="form.city" 
          placeholder="请输入所在城市"
        />
      </div>
      
      <div class="form-item">
        <label>标签</label>
        <div class="tag-selector">
          <div 
            v-for="tag in tagStore.tags" 
            :key="tag.id"
            class="tag-item"
            :class="{ active: form.tags.includes(tag.id) }"
            :style="{ borderColor: tag.color, color: form.tags.includes(tag.id) ? tag.color : '#606266' }"
            @click="toggleTag(tag.id)"
          >
            {{ tag.name }}
          </div>
          <div class="tag-item add-tag" @click="showAddTagDialog = true">
            + 添加标签
          </div>
        </div>
      </div>
      
      <div class="form-item">
        <label>
          <input type="checkbox" v-model="form.isPrivate" />
          私密日记
        </label>
      </div>
      
      <div class="form-actions">
        <BaseButton @click="goBack">取消</BaseButton>
        <BaseButton type="primary" @click="submitForm">保存</BaseButton>
      </div>
    </div>
    
    <BaseEmpty v-else text="加载中..." />
    
    <!-- 添加标签弹窗 -->
    <div v-if="showAddTagDialog" class="dialog-overlay" @click="showAddTagDialog = false">
      <div class="dialog" @click.stop>
        <div class="dialog-header">
          <h3>添加标签</h3>
          <button class="close-btn" @click="showAddTagDialog = false">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label for="tagName">标签名称</label>
            <input 
              type="text" 
              id="tagName" 
              v-model="newTag.name" 
              placeholder="请输入标签名称"
            />
          </div>
          <div class="form-item">
            <label for="tagColor">标签颜色</label>
            <input 
              type="color" 
              id="tagColor" 
              v-model="newTag.color"
            />
          </div>
        </div>
        <div class="dialog-footer">
          <BaseButton @click="showAddTagDialog = false">取消</BaseButton>
          <BaseButton type="primary" @click="addTag">确定</BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useDiaryStore } from '../stores/diary'
import { useTagStore } from '../stores/tag'

const router = useRouter()
const route = useRoute()
const diaryStore = useDiaryStore()
const tagStore = useTagStore()

const diaryId = route.params.id

const form = ref({
  title: '',
  content: '',
  moodType: 1,
  weatherType: 1,
  city: '',
  isPrivate: 1,
  tags: []
})

const showAddTagDialog = ref(false)
const newTag = ref({
  name: '',
  color: '#409EFF'
})

const moods = [
  { value: 1, label: '开心' },
  { value: 2, label: '平静' },
  { value: 3, label: '难过' },
  { value: 4, label: '焦虑' },
  { value: 5, label: '生气' }
]

const weathers = [
  { value: 1, label: '晴' },
  { value: 2, label: '多云' },
  { value: 3, label: '阴' },
  { value: 4, label: '雨' },
  { value: 5, label: '雪' }
]

const toggleTag = (tagId) => {
  const index = form.value.tags.indexOf(tagId)
  if (index === -1) {
    form.value.tags.push(tagId)
  } else {
    form.value.tags.splice(index, 1)
  }
}

const addTag = async () => {
  if (!newTag.value.name) return
  
  try {
    await tagStore.createTag(newTag.value)
    await tagStore.fetchTags()
    showAddTagDialog.value = false
    newTag.value = {
      name: '',
      color: '#409EFF'
    }
  } catch (error) {
    console.error('添加标签失败:', error)
  }
}

const submitForm = async () => {
  if (!form.value.title || !form.value.content) {
    alert('标题和内容不能为空')
    return
  }
  
  try {
    await diaryStore.updateDiary(diaryId, {
      ...form.value,
      tagIds: form.value.tags
    })
    router.push(`/diary/${diaryId}`)
  } catch (error) {
    console.error('更新日记失败:', error)
  }
}

const goBack = () => {
  router.push(`/diary/${diaryId}`)
}

const loadDiary = async () => {
  await diaryStore.fetchDiaryById(diaryId)
  if (diaryStore.currentDiary) {
    const diary = diaryStore.currentDiary
    form.value = {
      title: diary.title,
      content: diary.content,
      moodType: diary.moodType,
      weatherType: diary.weatherType,
      city: diary.city,
      isPrivate: diary.isPrivate,
      tags: diary.tags ? diary.tags.map(tag => tag.id) : []
    }
  }
}

onMounted(async () => {
  await tagStore.fetchTags()
  await loadDiary()
})
</script>

<style scoped>
/* 与 DiaryCreate.vue 相同的样式 */
.diary-edit {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.diary-edit__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.diary-edit__header h2 {
  margin: 0;
  color: #303133;
}

.diary-edit__form {
  background-color: white;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.form-item input[type="text"],
.form-item textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
}

.form-item textarea {
  min-height: 200px;
}

.mood-selector,
.weather-selector {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.mood-item,
.weather-item {
  padding: 8px 16px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.mood-item:hover,
.weather-item:hover {
  border-color: #409eff;
  color: #409eff;
}

.mood-item.active,
.weather-item.active {
  background-color: #409eff;
  color: white;
  border-color: #409eff;
}

.tag-selector {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.tag-item {
  padding: 6px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 16px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.tag-item:hover {
  border-color: #409eff;
  color: #409eff;
}

.tag-item.active {
  background-color: #409eff;
  color: white;
  border-color: #409eff;
}

.tag-item.add-tag {
  color: #409eff;
  border-style: dashed;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 30px;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background-color: white;
  border-radius: 4px;
  width: 400px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.dialog-header h3 {
  margin: 0;
  color: #303133;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #909399;
}

.dialog-body {
  padding: 16px;
}

.dialog-footer {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  padding: 16px;
  border-top: 1px solid #e4e7ed;
}
</style>
```

## 验收标准
1. 日记创建和编辑页面创建成功
2. 实现了表单提交功能
3. 实现了标签选择功能
4. 实现了心情和天气选择功能
5. 页面样式美观，交互流畅

## 关联 Skill
前置：Skill-031-日记列表页

后置：Skill-033-日记详情页