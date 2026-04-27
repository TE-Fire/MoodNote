# Skill: Pinia状态管理

## 触发条件
当前端需要使用 Pinia 进行状态管理时。

## 前置依赖
- Skill-028-路由配置

## 执行规范

### 文件位置
- 状态管理文件：`moodnote-web/src/stores/`

### 命名规范
- 文件名称：`diary.js`、`tag.js`

### 代码规范
- 使用 defineStore 创建 store
- 定义状态、getters 和 actions
- 使用 async/await 处理异步操作

### 依赖引入
- Pinia 依赖

## 代码模板

### 模板说明
创建 Pinia 状态管理 store，管理日记和标签的状态。

### 代码示例

#### 1. diary.js 日记状态管理
```js
import { defineStore } from 'pinia'
import request from '../utils/request'

export const useDiaryStore = defineStore('diary', {
  state: () => ({
    diaries: [],
    currentDiary: null,
    loading: false,
    total: 0
  }),
  
  getters: {
    getDiaryById: (state) => (id) => {
      return state.diaries.find(diary => diary.id === id)
    }
  },
  
  actions: {
    async fetchDiaries(params) {
      this.loading = true
      try {
        const response = await request.get('/api/v1/diaries', { params })
        this.diaries = response.data.records
        this.total = response.data.total
      } catch (error) {
        console.error('获取日记列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    async fetchDiaryById(id) {
      this.loading = true
      try {
        const response = await request.get(`/api/v1/diaries/${id}`)
        this.currentDiary = response.data
      } catch (error) {
        console.error('获取日记详情失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    async createDiary(data) {
      this.loading = true
      try {
        const response = await request.post('/api/v1/diaries', data)
        return response.data
      } catch (error) {
        console.error('创建日记失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    },
    
    async updateDiary(id, data) {
      this.loading = true
      try {
        await request.put(`/api/v1/diaries/${id}`, data)
      } catch (error) {
        console.error('更新日记失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    },
    
    async deleteDiary(id) {
      this.loading = true
      try {
        await request.delete(`/api/v1/diaries/${id}`)
        this.diaries = this.diaries.filter(diary => diary.id !== id)
      } catch (error) {
        console.error('删除日记失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    }
  }
})
```

#### 2. tag.js 标签状态管理
```js
import { defineStore } from 'pinia'
import request from '../utils/request'

export const useTagStore = defineStore('tag', {
  state: () => ({
    tags: [],
    loading: false
  }),
  
  getters: {
    getTagById: (state) => (id) => {
      return state.tags.find(tag => tag.id === id)
    }
  },
  
  actions: {
    async fetchTags() {
      this.loading = true
      try {
        const response = await request.get('/api/v1/tags')
        this.tags = response.data
      } catch (error) {
        console.error('获取标签列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    async createTag(data) {
      this.loading = true
      try {
        const response = await request.post('/api/v1/tags', data)
        const newTag = {
          id: response.data,
          ...data
        }
        this.tags.push(newTag)
        return response.data
      } catch (error) {
        console.error('创建标签失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    },
    
    async updateTag(id, data) {
      this.loading = true
      try {
        await request.put(`/api/v1/tags/${id}`, data)
        const index = this.tags.findIndex(tag => tag.id === id)
        if (index !== -1) {
          this.tags[index] = { ...this.tags[index], ...data }
        }
      } catch (error) {
        console.error('更新标签失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    },
    
    async deleteTag(id) {
      this.loading = true
      try {
        await request.delete(`/api/v1/tags/${id}`)
        this.tags = this.tags.filter(tag => tag.id !== id)
      } catch (error) {
        console.error('删除标签失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    }
  }
})
```

## 验收标准
1. Pinia 状态管理文件创建成功
2. 定义了必要的状态、getters 和 actions
3. 实现了异步操作的处理
4. 代码结构清晰，易于维护
5. 与 API 接口集成良好

## 关联 Skill
前置：Skill-028-路由配置

后置：Skill-030-全局组件注册