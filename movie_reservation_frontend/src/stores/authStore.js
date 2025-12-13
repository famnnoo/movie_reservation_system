import axios from 'axios'
import { defineStore } from 'pinia'
import router from '@/router'


export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: localStorage.getItem('token') || null,
    loading: false,
    error: null,
  }),
  actions: {
    async register ({ name, email, password }) {
      this.loading = true
      this.error = null
      try {
        const res = await axios.post('http://localhost:8080/auth/register', {
          name, email, password,
        })
        const { token, user } = res.data
        this.user = user
        this.token = token
        localStorage.setItem('token', token)
        router.push('/')
      } catch (error) {
        this.error = error.response?.data?.message || error.message
      } finally {
        this.loading = false
      }
    },

    async login ({ email, password }) {
      this.loading = true
      this.error = null
      try {
        const res = await axios.post('http://localhost:8080/auth/login', {
          email, password,
        })
        const { token, user } = res.data
        this.user = user
        this.token = token
        localStorage.setItem('token', token)
        router.push('/') 
      } catch (error) {
        this.error = error.response?.data?.message || error.message
      } finally {
        this.loading = false
      }
    },

    logout () {
      this.user = null
      this.token = null
      localStorage.removeItem('token')
      router.push('/auth/login')
    },
  },
  
})
