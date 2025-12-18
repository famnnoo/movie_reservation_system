import axios from 'axios'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import router from '@/router'
import { useUserStore } from './userStore'

export const useAuthStore = defineStore('auth', () => {
  const user = ref()
  const accessToken = ref(localStorage.getItem('accessToken'))
  const loading = ref(false)
  const error = ref('')
  const isAuthenticated = computed(() => !!accessToken.value)
  const userStore = useUserStore()

  const register = async ({ name, email, password }) => {
    loading.value = true
    error.value = null
    try {
      const res = await axios.post('http://localhost:8080/auth/register', {
        name,
        email,
        password,
      })

      accessToken.value = res.data.token
      user.value = {
        id: res.data.id,
        name: res.data.name,
        email: res.data.email,
      }
      localStorage.setItem('accessToken', res.data.token)
      localStorage.setItem('refreshToken', res.data.refreshToken)
      localStorage.setItem('userId', res.data.id)

      router.push('/')
    } catch (error) {
      error.value = error.response?.data?.message || error.message
    } finally {
      loading.value = false
    }
  }

  const login = async ({ email, password }) => {
    loading.value = true
    error.value = null
    try {
      const res = await axios.post('http://localhost:8080/auth/login', {
        email,
        password,
      })

      console.log(res.data)

      accessToken.value = res.data.token
      user.value = {
        id: res.data.id,
        name: res.data.name,
        email: res.data.email,
        roles: res.data.roles,
      }

      localStorage.setItem('accessToken', res.data.token)
      localStorage.setItem('refreshToken', res.data.refreshToken)
      localStorage.setItem('userId', res.data.id)

      router.push('/')
    } catch (error) {
      error.value = error.response?.data?.message || error.message
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    user.value = null
    accessToken.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userId')
    router.push('/Login')
  }

  const initAuth = async () => {
    const refreshToken = localStorage.getItem('refreshToken')

    if (!refreshToken) {
      return
    }

    try {
      const res = await axios.post('http://localhost:8080/auth/refresh', {
        refreshToken,
      })

      accessToken.value = res.data.token
      localStorage.setItem('accessToken', res.data.token)

      if (router.currentRoute.value.path === '/Login') {
        router.push('/')
      }
    } catch {
      logout()
    }
  }
  const hasRole = async role => {
    if (!userStore.user || !userStore.user.roles) {
      return false
    }
    return userStore.user.roles.includes(role)
  }

  return {
    user,
    accessToken,
    isAuthenticated,
    loading,
    error,
    register,
    login,
    logout,
    initAuth,
    hasRole,
  }
})
