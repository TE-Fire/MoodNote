import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
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
      name: 'diary-create',
      component: () => import('../views/DiaryCreate.vue')
    },
    {
      path: '/diary/:id',
      name: 'diary-detail',
      component: () => import('../views/DiaryDetail.vue')
    },
    {
      path: '/diary/edit/:id',
      name: 'diary-edit',
      component: () => import('../views/DiaryEdit.vue')
    },
    {
      path: '/stats/calendar',
      name: 'stats-calendar',
      component: () => import('../views/StatsCalendar.vue')
    },
    {
      path: '/stats/trend',
      name: 'stats-trend',
      component: () => import('../views/StatsTrend.vue')
    }
  ]
})

export default router