<template>
  <app-header />

  <v-container class="mt-12">
    <div v-if="loading" class="text-center my-12">
      Loading movies…
    </div>

    <div v-else>
      <MovieRow
        v-for="movie in movieStore.movies"
        :key="movie.id"
        :movie="movie"
      />
    </div>
  </v-container>
</template>

<script setup>
  import { onBeforeMount, ref } from 'vue'
  import AppHeader from '@/components/AppHeader.vue'
  import MovieRow from '@/components/MovieRow.vue'
  import router from '@/router'
  import { useAuthStore } from '@/stores/authStore'
  import { useMovieStore } from '@/stores/movieStore'

  const movieStore = useMovieStore()
  const authStore = useAuthStore()
  const loading = ref(true)

  onBeforeMount(async () => {
    const token = localStorage.getItem('accessToken')
    if (!token) {
      router.push('/Login')
      return
    }

    authStore.token = token

    try {
      await movieStore.fetchMovies()
    } catch {
      router.push('/Login')
    } finally {
      loading.value = false
    }
  })
</script>
