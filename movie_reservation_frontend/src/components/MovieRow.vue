<template>
  <v-card class="movie-row" elevation="3">
    <div class="movie-container">
      <!-- Poster -->
      <div class="movie-poster">
        <v-img
          cover
          max-height="400"
          max-width="200"
          min-height="400"
          min-width="200"
          :src="movie.poster"
        />
      </div>

      <!-- Details -->
      <div class="movie-details">
        <h2 class="text-h5 mb-1">{{ movie.title }}</h2>

        <div class="text-caption mb-3">
          {{ movie.durationMinutes }} min · Released {{ formatDate(movie.releaseDate) }}
          Genre: {{ movie.genre }}
        </div>
        <p class="text-body-1 mb-6">{{ movie.description }}</p>

        <div v-if="Object.keys(groupedTimes).length > 0" class="dates-wrapper">
          <div class="dates-row">
            <div
              v-for="(times, date) in groupedTimes"
              :key="date"
              class="date-container"
            >
              <strong class="date-title">{{ formatDate(date) }}</strong>

              <div class="times-container">
                <v-btn
                  v-for="time in times"
                  :key="time.id"
                  class="mb-2"
                  :color="isSelected(date, time) ? 'secondary' : 'primary'"
                  size="small"
                  variant="outlined"
                  @click="selectTime(date, time)"
                >
                  {{ time.label }}
                </v-btn>

              </div>
            </div>
          </div>

          <!-- Reserve button -->
          <div class="reserve-button">
            <v-btn
              color="success"
              :disabled="!selected.date || !selected.time"
              @click="goToSeatMap"
            >
              Reserve
            </v-btn>
          </div>
        </div>

        <div v-else class="text-disabled">
          No seances available
        </div>
      </div>
    </div>
  </v-card>
</template>

<script setup>
  import { computed, reactive } from 'vue'
  import { useRouter } from 'vue-router'

  const router = useRouter()

  function goToSeatMap () {
    if (!selected.time) return

    router.push({
      name: 'SeatMap',
      params: {
        movieId: props.movie.id,
        displayTimeId: selected.time.id,
      },
      query: {
        title: props.movie.title,
      },
    })
  }

  const props = defineProps({
    movie: {
      type: Object,
      required: true,
    },
  })

  const groupedTimes = computed(() => {
    const result = {}
    const now = new Date()

    for (const dt of props.movie.displayTimes || []) {
      // dt = { id, time }
      if (!dt.time) continue

      const dateObj = new Date(dt.time)
      if (Number.isNaN(dateObj.getTime())) continue

      if (dateObj < now) continue

      const date = dateObj.toISOString().split('T')[0]
      const timeLabel = dateObj.toLocaleTimeString([], {
        hour: '2-digit',
        minute: '2-digit',
      })

      if (!result[date]) result[date] = []

      result[date].push({
        id: dt.id,
        label: timeLabel,
      })
    }

    return Object.fromEntries(Object.entries(result).slice(0, 4))
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
    time: null, // { id, label }
  })

  function isSelected (date, time) {
    return selected.date === date && selected.time?.id === time.id
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
  position: relative;
  padding: 16px;
  margin-bottom: 36px;
}

.reserve-button {
  position: absolute;
  bottom: 16px;
  right: 16px;
}

.movie-container {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.movie-poster {
  flex-shrink: 0;
}

.movie-details {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.dates-row {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.date-container {
  display: flex;
  flex-direction: column;
  min-width: 140px;
  padding: 8px;
  border-radius: 8px;
}

.date-title {
  font-weight: bold;
  margin-bottom: 8px;
}

.times-container {
  display: flex;
  flex-direction: column;
}

.times-container v-btn {
  margin-bottom: 4px;
}

</style>
