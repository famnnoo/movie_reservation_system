<template>
  <v-container>
    <div v-if="loading" class="text-center my-12">
      Loading movies...
    </div>

    <div v-else>
      <v-row>
        <v-col
          v-for="movie in movieStore.movies"
          :key="movie.id"
          cols="12" sm="6" md="4" lg="3"
        >
          <v-card>
            <v-img :src="movie.poster" height="300px" cover></v-img>
            <v-card-title>{{ movie.title }}</v-card-title>
            <v-card-text>{{ movie.description }}</v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </div>
  </v-container>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { useMovieStore } from '@/stores/movieStore'
import router from '@/router'

const authStore = useAuthStore()
const movieStore = useMovieStore()
const loading = ref(true)

onBeforeMount(async () => {
  // 1️⃣ Check for token in localStorage
  const token = localStorage.getItem('token')
  if (!token) {
    router.push('/Login')
    return
  }

  // 2️⃣ Set token in AuthStore
  authStore.token = token
  authStore.user = {} // optional: decode JWT

  // 3️⃣ Fetch movies
  try {
    await movieStore.fetchMovies()
  } catch (err) {
    console.error('Failed to fetch movies', err)
    router.push('/auth/login') // redirect if fetch fails (token invalid)
  } finally {
    loading.value = false
  }
})
</script>
