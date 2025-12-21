import { createRouter, createWebHistory } from 'vue-router'
import Account from '@/pages/Account.vue'
import Admin from '@/pages/Admin.vue'
import Home from '@/pages/Home.vue'
import Login from '@/pages/Login.vue'
import SeatMap from '@/pages/SeatMap.vue'
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
    path: '/account',
    name: 'Account',
    component: Account,
    meta: { requiresAuth: true },
  },
  {
    path: '/admin',
    name: 'Admin',
    component: Admin,
    meta: { requiresAuth: true, role: 'ADMIN' },
  },
  {
    path: '/seatmap/:movieId/:displayTimeId',
    name: 'SeatMap',
    component: SeatMap,
    props: route => ({
      movieId: Number(route.params.movieId),
      displayTimeId: Number(route.params.displayTimeId),
      movieTitle: route.query.title || '',
    }),
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
