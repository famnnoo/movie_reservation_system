<template>
  <v-container>
    <div v-if="loading" class="text-center my-12">Loading movies...</div>

    <div v-else class="movies-grid">
      <template v-for="movie in movieStore.movies" :key="movie.id">
        <!-- Movie card -->
        <div class="movie-item" @click="toggleExpand(movie)">
          <v-card class="hover-card">
            <v-img contain height="150" :src="movie.poster" />
            <v-card-title>{{ movie.title }}</v-card-title>
          </v-card>
        </div>
        <div
          v-if="expandedMovie && expandedMovie.id === movie.id"
          class="movie-expanded"
        >
          <v-card class="pa-4" elevation="6">
            <v-row>
              <v-col cols="12" md="4">
                <v-img contain height="200" :src="movie.poster" />
              </v-col>
              <v-col cols="12" md="8">
                <v-card-title>{{ movie.title }}</v-card-title>
                <v-card-text>{{ movie.description }}</v-card-text>

                <div class="mt-2">
                  <strong>Next seances:</strong>
                  <v-chip
                    v-for="time in getNextSeances(movie)"
                    :key="time"
                    class="ma-1"
                    color="deep-purple lighten-3"
                  >
                    {{ time }}
                  </v-chip>
                </div>

                <div class="mt-4 text-right">
                  <v-btn color="primary">Reserve</v-btn>
                </div>
              </v-col>
            </v-row>
          </v-card>
        </div>
      </template>
    </div>
  </v-container>
</template>

<script setup>
  import { onBeforeMount, ref } from 'vue'
  import router from '@/router'
  import { useAuthStore } from '@/stores/authStore'
  import { useMovieStore } from '@/stores/movieStore'

  const authStore = useAuthStore()
  const movieStore = useMovieStore()
  const loading = ref(true)
  const expandedMovie = ref(null)

  function toggleExpand (movie) {
    expandedMovie.value
      = expandedMovie.value && expandedMovie.value.id === movie.id
        ? null
        : movie
  }
  function getNextSeances (movie) {
    const base = new Date()
    return Array.from({ length: 4 }, (_, i) => {
      const d = new Date(base)
      d.setHours(18 + i)
      return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    })
  }

  onBeforeMount(async () => {
    const token = localStorage.getItem('token')
    if (!token) {
      router.push('/Login')
      return
    }

    authStore.token = token
    authStore.user = {}

    try {
      await movieStore.fetchMovies()
    } catch (error) {
      console.error('Failed to fetch movies', error)
      router.push('/Login')
    } finally {
      loading.value = false
    }
  })
</script>

<style scoped>
.movies-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.movie-item {
  flex: 1 1 calc(25% - 16px);
  min-width: 200px;
  cursor: pointer;
}

.movie-expanded {
  flex: 1 1 100%;
  margin-top: 8px;
}

.hover-card {
  transition: transform 0.2s;
}

.hover-card:hover {
  transform: scale(1.03);
}
</style>
