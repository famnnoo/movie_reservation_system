import axios from 'axios'
import router from '@/router'

const api = axios.create({
  baseURL: 'http://localhost:8080',
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      const refreshToken = localStorage.getItem('refreshToken')
      if (!refreshToken) {
        router.push('/Login')
        throw error
      }

      const res = await axios.post(
        'http://localhost:8080/auth/refresh',
        { refreshToken },
      )

      localStorage.setItem('accessToken', res.data.token)
      originalRequest.headers.Authorization
        = `Bearer ${res.data.token}`

      return api(originalRequest)
    }
  },
)

export default api
