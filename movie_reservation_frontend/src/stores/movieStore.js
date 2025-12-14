import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/plugins/axios'

export const useMovieStore = defineStore('movies', () => {
  const movies = ref([])
  const loading = ref(false)
  const error = ref('')

  const fetchMovies = async () => {
    loading.value = true
    error.value = null

    try {
      const res = await api.get('/movies')

      const moviesWithImages = await Promise.all(
        res.data.map(async movie => {
          let poster = ''

          try {
            const imgRes = await api.get(
              `/movies/${movie.id}/image`,
              { responseType: 'blob' },
            )
            poster = URL.createObjectURL(imgRes.data)
          } catch {
            console.warn(`Image load failed for movie ${movie.id}`)
          }

          return {
            ...movie,
            poster,
          }
        }),
      )

      movies.value = moviesWithImages
    } catch (error) {
      error.value = error
    } finally {
      loading.value = false
    }
  }

  return {
    movies,
    loading,
    error,
    fetchMovies,
  }
})
