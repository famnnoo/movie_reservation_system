<template>
  <v-card
    class="mx-auto my-12"
    :disabled="loading"
    :loading="loading"
    max-width="374"
  >
    <template #loader="{ isActive }">
      <v-progress-linear
        :active="isActive"
        color="deep-purple"
        height="4"
        indeterminate
      />
    </template>

    <v-img
      cover
      height="250"
      :src="image"
    />

    <v-card-item>
      <v-card-title>{{ title }}</v-card-title>

      <v-card-subtitle>
        <span class="me-1">{{ subtitle }}</span>

        <v-icon
          v-if="highlight"
          color="error"
          icon="mdi-fire-circle"
          size="small"
        />
      </v-card-subtitle>
    </v-card-item>

    <v-card-text>
      <v-row align="center" class="mx-0">
        <v-rating
          color="amber"
          density="compact"
          half-increments
          :model-value="rating"
          readonly
          size="small"
        />
        <div class="text-grey ms-4">
          {{ rating }} ({{ reviews }})
        </div>
      </v-row>

      <div class="my-4 text-subtitle-1">
        {{ price }} • {{ cuisine }}
      </div>

      <div>{{ description }}</div>
    </v-card-text>

    <v-divider class="mx-4 mb-1" />

    <v-card-title>Tonight's availability</v-card-title>

    <div class="px-4 mb-2">
      <v-chip-group
        v-model="selection"
        selected-class="bg-deep-purple-lighten-2"
      >
        <v-chip
          v-for="time in times"
          :key="time"
        >
          {{ time }}
        </v-chip>
      </v-chip-group>
    </div>

    <v-card-actions>
      <v-btn
        block
        border
        color="deep-purple-lighten-2"
        text="Reserve"
        @click="reserve"
      />
    </v-card-actions>
  </v-card>
</template>

  <script setup>
  import { ref } from 'vue'

  defineProps({
    title: String,
    subtitle: String,
    image: String,
    rating: Number,
    reviews: Number,
    price: String,
    cuisine: String,
    description: String,
    times: Array,
    highlight: Boolean,
  })

  const loading = ref(false)
  const selection = ref(null)
  // eslint-disable-next-line @stylistic/no-trailing-spaces
  
  function reserve () {
    loading.value = true
    setTimeout(() => {
      loading.value = false
    }, 2000)
  }
  </script>
