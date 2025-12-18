import { createRouter, createWebHistory } from 'vue-router'
import Admin from '@/pages/Admin.vue'
import Home from '@/pages/Home.vue'
import Login from '@/pages/Login.vue'
import { useAuthStore } from '@/stores/authStore'
import { useUserStore } from '@/stores/userStore'

const routes = [
  {
    path: '/Login',
    name: 'Login',
    component: Login,
  },
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true },
  },
  {
    path: '/admin',
    name: 'Admin',
    component: Admin,
    meta: { requiresAuth: true, role: 'ADMIN' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async to => {
  const authStore = useAuthStore()
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return '/Login'
  }

  if (to.path === '/Login' && authStore.isAuthenticated) {
    return '/'
  }

  if (to.meta.role) {
    if (!userStore.user) {
      await userStore.fetchUser()
    }
    if (!authStore.hasRole(to.meta.role)) {
      return '/'
    }
  }
})

export default router
