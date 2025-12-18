import { createPinia } from 'pinia'
import { createApp } from 'vue'
import { registerPlugins } from '@/plugins'
import { useAuthStore } from '@/stores/authStore'

import App from './App.vue'

import vuetify from './plugins/vuetify'
import router from './router'
import { useUserStore } from './stores/userStore'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)
app.use(router)
app.use(vuetify)
registerPlugins(app)

const authStore = useAuthStore()
const userStore = useUserStore()

app.mount('#app')

async function initApp () {
  await authStore.initAuth()
  await userStore.fetchUser()
}

initApp()
