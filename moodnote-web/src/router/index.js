import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Home.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue')
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/Profile.vue')
    },
    {
      path: '/diary/create',
      name: 'diaryCreate',
      component: () => import('../views/DiaryCreate.vue')
    },
    {
      path: '/diary/:id',
      name: 'diaryDetail',
      component: () => import('../views/DiaryDetail.vue')
    },
    {
      path: '/diary/edit/:id',
      name: 'diaryEdit',
      component: () => import('../views/DiaryEdit.vue')
    },
    {
      path: '/stats/calendar',
      name: 'statsCalendar',
      component: () => import('../views/StatsCalendar.vue')
    },
    {
      path: '/stats/trend',
      name: 'statsTrend',
      component: () => import('../views/StatsTrend.vue')
    }
  ]
})

export default router