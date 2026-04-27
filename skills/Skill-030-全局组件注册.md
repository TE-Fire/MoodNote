# Skill: 全局组件注册

## 触发条件
当前端需要注册全局组件，方便在整个应用中使用时。

## 前置依赖
- Skill-029-Pinia状态管理

## 执行规范

### 文件位置
- 组件文件：`moodnote-web/src/components/common/`
- 注册文件：`moodnote-web/src/main.js`

### 命名规范
- 组件名称：使用 PascalCase 命名
- 文件名称：使用 kebab-case 命名

### 代码规范
- 使用 Vue 3 的组件定义方式
- 在 main.js 中注册全局组件
- 组件结构清晰，职责明确

### 依赖引入
- Vue 3 依赖

## 代码模板

### 模板说明
创建通用基础组件并在全局注册，方便在整个应用中使用。

### 代码示例

#### 1. BaseButton.vue 基础按钮组件
```vue
<template>
  <button 
    :class="[
      'base-button',
      `base-button--${type}`,
      { 'base-button--disabled': disabled }
    ]"
    :disabled="disabled"
    @click="$emit('click')"
  >
    <slot></slot>
  </button>
</template>

<script setup>
defineProps({
  type: {
    type: String,
    default: 'primary'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

defineEmits(['click'])
</script>

<style scoped>
.base-button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.base-button--primary {
  background-color: #409EFF;
  color: white;
}

.base-button--primary:hover {
  background-color: #66B1FF;
}

.base-button--success {
  background-color: #67C23A;
  color: white;
}

.base-button--success:hover {
  background-color: #85CE61;
}

.base-button--danger {
  background-color: #F56C6C;
  color: white;
}

.base-button--danger:hover {
  background-color: #F78989;
}

.base-button--disabled {
  background-color: #C0C4CC;
  color: white;
  cursor: not-allowed;
}
</style>
```

#### 2. BaseCard.vue 基础卡片组件
```vue
<template>
  <div class="base-card">
    <div class="base-card__header" v-if="title">
      <h3 class="base-card__title">{{ title }}</h3>
    </div>
    <div class="base-card__body">
      <slot></slot>
    </div>
    <div class="base-card__footer" v-if="$slots.footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.base-card {
  border: 1px solid #E4E7ED;
  border-radius: 4px;
  background-color: white;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.base-card__header {
  padding: 12px 16px;
  border-bottom: 1px solid #E4E7ED;
}

.base-card__title {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.base-card__body {
  padding: 16px;
}

.base-card__footer {
  padding: 12px 16px;
  border-top: 1px solid #E4E7ED;
  text-align: right;
}
</style>
```

#### 3. BaseEmpty.vue 空状态组件
```vue
<template>
  <div class="base-empty">
    <div class="base-empty__icon">
      <slot name="icon">
        <svg width="64" height="64" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
          <path fill="#C0C4CC" d="M512 0C229.25 0 0 229.25 0 512s229.25 512 512 512 512-229.25 512-512S794.75 0 512 0zm0 928c-229.9 0-416-186.1-416-416S282.1 96 512 96s416 186.1 416 416-186.1 416-416 416z"/>
          <path fill="#C0C4CC" d="M512 256c-141.16 0-256 114.84-256 256s114.84 256 256 256 256-114.84 256-256-114.84-256-256-256zm0 480c-123.5 0-224-100.5-224-224s100.5-224 224-224 224 100.5 224 224-100.5 224-224 224z"/>
        </svg>
      </slot>
    </div>
    <div class="base-empty__text">
      <slot>
        {{ text }}
      </slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  text: {
    type: String,
    default: '暂无数据'
  }
})
</script>

<style scoped>
.base-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  color: #909399;
}

.base-empty__icon {
  margin-bottom: 16px;
}

.base-empty__text {
  font-size: 14px;
}
</style>
```

#### 4. 在 main.js 中注册全局组件
```js
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// 导入全局组件
import BaseButton from './components/common/BaseButton.vue'
import BaseCard from './components/common/BaseCard.vue'
import BaseEmpty from './components/common/BaseEmpty.vue'

import './assets/styles/global.scss'

const app = createApp(App)

// 注册全局组件
app.component('BaseButton', BaseButton)
app.component('BaseCard', BaseCard)
app.component('BaseEmpty', BaseEmpty)

app.use(createPinia())
app.use(router)

app.mount('#app')
```

## 验收标准
1. 通用基础组件创建成功
2. 组件样式美观，功能完整
3. 在 main.js 中正确注册了全局组件
4. 组件可以在整个应用中使用
5. 代码结构清晰，易于维护

## 关联 Skill
前置：Skill-029-Pinia状态管理

后置：Skill-031-日记列表页