import { defineStore } from 'pinia'
import api from '@/plugins/axios'

export const useMovieStore = defineStore('movies', {
  state: () => ({
    movies: [],
    loading: false,
    error: null,
  }),
  actions: {
    async fetchMovies () {
      this.loading = true
      this.error = null
      try {
        const res = await api.get('/movies')
        const moviesWithImages = await Promise.all(
          res.data.map(async movie => {
            let posterUrl = ''
            try {
              const imgRes = await api.get(`/movies/${movie.id}/image`, { responseType: 'blob' })
              posterUrl = URL.createObjectURL(imgRes.data)
            } catch (error) {
              console.error(`Failed to load image for movie ${movie.id}`, error)
              posterUrl = ''
            }

            return {
              ...movie,
              poster: posterUrl,
            }
          }),
        )

        this.movies = moviesWithImages
      } catch (error) {
        this.error = error
      } finally {
        this.loading = false
      }
    },
  },
})
