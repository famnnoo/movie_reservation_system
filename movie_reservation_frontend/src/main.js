import { createPinia } from 'pinia'
import { createApp } from 'vue'
import { registerPlugins } from '@/plugins'
import { useAuthStore } from '@/stores/authStore'
import { useMovieStore } from '@/stores/movieStore'

import App from './App.vue'

import vuetify from './plugins/vuetify'
import router from './router'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(vuetify)
registerPlugins(app)

app.mount('#app')

// Auto-login and fetch movies after mounting
const authStore = useAuthStore()
const movieStore = useMovieStore()

async function initApp () {
  const token = localStorage.getItem('token')
  if (token) {
    authStore.token = token
    authStore.user = {} // optional: decode JWT to get user info
    try {
      await movieStore.fetchMovies()
    } catch (error) {
      console.error('Failed to fetch movies', error)
    }
  } else {
    router.push('/Login')
  }
}

initApp()
