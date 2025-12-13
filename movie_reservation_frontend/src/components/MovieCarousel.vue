<template>
  <v-container>
    <div class="d-flex align-center mb-2">
      <v-spacer />
      <h2 class="text-h6">{{ title }}</h2>
      <v-spacer />
    </div>

    <div class="d-flex align-center">
      <v-slide-group
        ref="group"
        class="flex-grow-1"
        show-arrows="false"
      >
        <v-slide-group-item
          v-for="movie in movies"
          :key="movie.id"
        >
          <MovieCard
            class="ma-2"
            :poster="movie.poster"
            :title="movie.title"
          />
        </v-slide-group-item>
      </v-slide-group>

    </div>
  </v-container>
</template>

  <script setup>
  import { ref } from 'vue'
  import MovieCard from './MovieCard.vue'

  defineProps({
    title: String,
    movies: Array,
  })

  const group = ref(null)

  function scroll (direction) {
    group.value?.scrollTo(direction === 1 ? 'next' : 'prev')
  }
  </script>
