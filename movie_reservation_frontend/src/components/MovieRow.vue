<template>
  <v-card class="movie-row" elevation="3">
    <v-row no-gutters>
      <v-col cols="12" md="3">
        <v-img
          cover
          min-height="320"
          :src="movie.poster"
        />
      </v-col>

      <v-col cols="12" md="9">
        <div class="pa-6">
          <h2 class="text-h5 mb-1">
            {{ movie.title }}
          </h2>

          <div class="text-caption mb-3">
            {{ movie.durationMinutes }} min ·
            Released {{ formatDate(movie.releaseDate) }}
          </div>

          <p class="text-body-1 mb-6">
            {{ movie.description }}
          </p>

          <div v-if="Object.keys(groupedTimes).length > 0">
            <div
              v-for="(times, date) in groupedTimes"
              :key="date"
              class="mb-4"
            >
              <strong class="d-block mb-2">
                {{ formatDate(date) }}
              </strong>

              <div class="d-flex flex-wrap gap">
                <v-btn
                  v-for="time in times"
                  :key="time"
                  :color="isSelected(date, time) ? 'secondary' : 'primary'"
                  size="small"
                  variant="outlined"
                  @click="selectTime(date, time)"
                >
                  {{ time }}
                </v-btn>
              </div>
            </div>

            <v-btn
              class="mt-4"
              color="success"
              :disabled="!selected.date || !selected.time"
              @click="reserveSelected"
            >
              Reserve
            </v-btn>
          </div>

          <div v-else class="text-disabled">
            No seances available
          </div>
        </div>
      </v-col>
    </v-row>
  </v-card>
</template>

<script setup>
  import { computed, reactive } from 'vue'

  const props = defineProps({
    movie: {
      type: Object,
      required: true,
    },
  })

  const groupedTimes = computed(() => {
    const result = {}
    for (const iso of props.movie.displayTimes || []) {
      const dateObj = new Date(iso)
      const date = dateObj.toISOString().split('T')[0]
      const time = dateObj.toLocaleTimeString([], {
        hour: '2-digit',
        minute: '2-digit',
      })
      if (!result[date]) result[date] = []
      result[date].push(time)
    }
    return result
  })

  function formatDate (date) {
    return new Date(date).toLocaleDateString(undefined, {
      weekday: 'long',
      day: '2-digit',
      month: 'long',
      year: 'numeric',
    })
  }

  const selected = reactive({
    date: null,
    time: null,
  })

  function isSelected (date, time) {
    return selected.date === date && selected.time === time
  }

  function selectTime (date, time) {
    selected.date = date
    selected.time = time
  }

  function reserveSelected () {
    if (selected.date && selected.time) {
      console.log('Reserve movie', props.movie.id, selected.date, selected.time)
    }
  }
</script>

<style scoped>
.movie-row {
  margin-bottom: 36px;
}

.gap {
  gap: 12px;
}
</style>
