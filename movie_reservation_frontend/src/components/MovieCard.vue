<template>
  <v-card width="200">
    <v-img cover height="300" :src="poster" />

    <v-card-title class="text-subtitle-1">
      {{ title }}
    </v-card-title>

    <v-card-actions>
      <v-btn block color="primary">
        Reserve
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

  <script setup>
  defineProps({
    title: String,
    poster: String,
  })
  </script>
