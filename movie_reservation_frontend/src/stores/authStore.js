import axios from 'axios'
import { defineStore } from 'pinia'
import { ref } from 'vue'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  const user = ref()
  const token = ref(localStorage.getItem('token'))
  const loading = ref(false)
  const error = ref('')

  const register = async ({ name, email, password }) => {
    loading.value = true
    error.value = null
    try {
      const res = await axios.post('http://localhost:8080/auth/register', {
        name,
        email,
        password,
      })

      token.value = res.data.token
      user.value = res.data.user

      localStorage.setItem('token', token.value)
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

      token.value = res.data.token
      user.value = res.data.user

      localStorage.setItem('token', token.value)
      router.push('/')
    } catch (error) {
      error.value = error.response?.data?.message || error.message
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    router.push('/auth/login')
  }

  return {
    user,
    token,
    loading,
    error,
    register,
    login,
    logout,
  }
})
