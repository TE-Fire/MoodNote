import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Home.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/Profile.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/diary/create',
      name: 'diary-create',
      component: () => import('../views/DiaryCreate.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/diary/:id',
      name: 'diary-detail',
      component: () => import('../views/DiaryDetail.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/diary/edit/:id',
      name: 'diary-edit',
      component: () => import('../views/DiaryEdit.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/stats/calendar',
      name: 'stats-calendar',
      component: () => import('../views/StatsCalendar.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/stats/trend',
      name: 'stats-trend',
      component: () => import('../views/StatsTrend.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

const whiteList = ['/login', '/register']

router.beforeEach(async (to, from, next) => {
  const hasToken = !!localStorage.getItem('token')

  if (hasToken) {
    if (to.path === '/login' || to.path === '/register') {
      next({ path: '/' })
    } else {
      next()
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      ElMessage.warning('请先登录')
      next('/login')
    }
  }
})

export default router