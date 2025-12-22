<template>
  <app-header />
  <div class="spacer">
  </div>
  <v-container class="mt-8">
    <!-- Search and Filter Section -->
    <v-card class="mb-6" elevation="2">
      <v-card-title class="bg-gradient">
        <v-icon class="mr-2">mdi-filter-variant</v-icon>
        Search & Filter Movies
      </v-card-title>
      
      <v-card-text class="pa-6">
        <!-- Search Bar -->
        <v-text-field
          v-model="searchQuery"
          label="Search movies by title or description..."
          prepend-inner-icon="mdi-magnify"
          variant="outlined"
          clearable
          hide-details
          class="mb-4"
          @input="applyFilters"
        />

        <!-- Filters Row -->
        <v-row>
          <!-- Genre Filter -->
          <v-col cols="12" sm="6" md="3">
            <v-select
              v-model="selectedGenre"
              :items="genreOptions"
              label="Genre"
              variant="outlined"
              prepend-inner-icon="mdi-movie-open"
              clearable
              hide-details
              @update:model-value="applyFilters"
            />
          </v-col>

          <!-- Date Filter -->
          <v-col cols="12" sm="6" md="3">
            <v-text-field
              v-model="selectedDate"
              type="date"
              label="Show Date"
              variant="outlined"
              prepend-inner-icon="mdi-calendar"
              clearable
              hide-details
              @update:model-value="applyFilters"
            />
          </v-col>

          <!-- Location Filter -->
          <v-col cols="12" sm="6" md="3">
            <v-select
              v-model="selectedLocation"
              :items="locationOptions"
              label="Location"
              variant="outlined"
              prepend-inner-icon="mdi-map-marker"
              clearable
              hide-details
              @update:model-value="applyFilters"
            />
          </v-col>

          <!-- Cinema Filter -->
          <v-col cols="12" sm="6" md="3">
            <v-select
              v-model="selectedCinema"
              :items="cinemaOptions"
              label="Cinema"
              variant="outlined"
              prepend-inner-icon="mdi-theater"
              clearable
              hide-details
              @update:model-value="applyFilters"
            />
          </v-col>
        </v-row>

        <!-- Clear All Button -->
        <div class="d-flex justify-end mt-4">
          <v-btn
            color="secondary"
            variant="text"
            prepend-icon="mdi-filter-remove"
            @click="clearAllFilters"
          >
            Clear All Filters
          </v-btn>
        </div>
      </v-card-text>
    </v-card>

    <!-- Results Count -->
    <div class="text-subtitle-1 mb-4" v-if="!loading">
      <v-icon class="mr-1">mdi-movie-filter</v-icon>
      Showing {{ filteredMovies.length }} movie{{ filteredMovies.length !== 1 ? 's' : '' }}
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center my-12">
      <v-progress-circular indeterminate color="primary" size="64" />
      <p class="mt-4 text-h6">Loading movies...</p>
    </div>

    <!-- No Results -->
    <v-card v-else-if="filteredMovies.length === 0" class="text-center pa-12">
      <v-icon size="80" color="grey-lighten-1">mdi-movie-off</v-icon>
      <h3 class="text-h5 mt-4 mb-2">No Movies Found</h3>
      <p class="text-subtitle-1 text-grey">Try adjusting your filters or search query</p>
    </v-card>

    <!-- Movies List -->
    <div v-else>
      <MovieRow
        v-for="movie in filteredMovies"
        :key="movie.id"
        :movie="movie"
      />
    </div>
  </v-container>
</template>

<script setup>
  import { computed, onBeforeMount, ref } from 'vue'
  import AppHeader from '@/components/AppHeader.vue'
  import MovieRow from '@/components/MovieRow.vue'
  import router from '@/router'
  import { useAuthStore } from '@/stores/authStore'
  import { useMovieStore } from '@/stores/movieStore'
  import api from '@/plugins/axios'

  const movieStore = useMovieStore()
  const authStore = useAuthStore()
  const loading = ref(true)
  
  // Filter states
  const searchQuery = ref('')
  const selectedGenre = ref(null)
  const selectedDate = ref(null)
  const selectedLocation = ref(null)
  const selectedCinema = ref(null)
  
  const filteredMovies = ref([])

  // Extract unique filter options from movies
  const genreOptions = computed(() => {
    const genres = new Set()
    movieStore.movies.forEach(movie => {
      if (movie.genre) {
        movie.genre.split(',').forEach(g => genres.add(g.trim()))
      }
    })
    return ['All', ...Array.from(genres).sort()]
  })

  const locationOptions = computed(() => {
    const locations = new Set()
    movieStore.movies.forEach(movie => {
      if (movie.locations) {
        movie.locations.forEach(loc => locations.add(loc))
      }
    })
    return ['All', ...Array.from(locations).sort()]
  })

  const cinemaOptions = computed(() => {
    const cinemas = new Set()
    movieStore.movies.forEach(movie => {
      if (movie.cinemas) {
        movie.cinemas.forEach(cinema => cinemas.add(cinema))
      }
    })
    return ['All', ...Array.from(cinemas).sort()]
  })

  async function applyFilters() {
    try {
      const params = {}
      
      if (searchQuery.value) {
        params.search = searchQuery.value
      }
      if (selectedGenre.value && selectedGenre.value !== 'All') {
        params.genre = selectedGenre.value
      }
      if (selectedDate.value) {
        params.date = selectedDate.value
      }
      if (selectedLocation.value && selectedLocation.value !== 'All') {
        params.location = selectedLocation.value
      }
      if (selectedCinema.value && selectedCinema.value !== 'All') {
        params.cinema = selectedCinema.value
      }

      // If no filters, show all movies
      if (Object.keys(params).length === 0) {
        filteredMovies.value = movieStore.movies
        return
      }

      // Call API with filters
      const response = await api.get('/movies', { params })
      
      // Load images for filtered movies (same as movieStore does)
      const moviesWithImages = await Promise.all(
        response.data.map(async movie => {
          let poster = ''
          
          try {
            const imgRes = await api.get(
              `/movies/${movie.id}/image`,
              { responseType: 'blob' }
            )
            poster = URL.createObjectURL(imgRes.data)
          } catch {
            console.warn(`Image load failed for movie ${movie.id}`)
          }
          
          return {
            ...movie,
            poster
          }
        })
      )
      
      filteredMovies.value = moviesWithImages
    } catch (error) {
      console.error('Failed to filter movies:', error)
      filteredMovies.value = []
    }
  }

  function clearAllFilters() {
    searchQuery.value = ''
    selectedGenre.value = null
    selectedDate.value = null
    selectedLocation.value = null
    selectedCinema.value = null
    filteredMovies.value = movieStore.movies
  }

  onBeforeMount(async () => {
    const token = localStorage.getItem('accessToken')
    if (!token) {
      router.push('/Login')
      return
    }

    authStore.token = token

    try {
      await movieStore.fetchMovies()
      filteredMovies.value = movieStore.movies
    } catch {
      router.push('/Login')
    } finally {
      loading.value = false
    }
  })
</script>

<style scoped>
.bg-gradient {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.gap-3 {
  gap: 12px;
}
.spacer {
  height: 64px;
}
</style>
