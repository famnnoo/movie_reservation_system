import { defineStore } from 'pinia'
import api from '@/plugins/axios'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
    loading: false,
    error: null,
  }),
  actions: {
    setUser (user) {
      this.user = user
    },
    async fetchUser () {
      this.loading = true
      this.error = null
      try {
        const res = await api.get(`/auth/${localStorage.getItem('userId')}`)
        this.user = res.data
      } catch (error) {
        this.error = error
      } finally {
        this.loading = false
      }
    },
    clearUser () {
      this.user = null
    },

    getRoles () {
      return this.user?.roles || []
    },
  },
})
