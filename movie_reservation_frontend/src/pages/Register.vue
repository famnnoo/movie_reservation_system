<template>
  <v-container class="d-flex justify-center">
    <v-card class="pa-4" max-width="400">
      <v-card-title>Register</v-card-title>

      <v-card-text>
        <v-form ref="form" @submit.prevent="onSubmit">
          <v-text-field v-model="name" label="Name" required />
          <v-text-field v-model="email" label="Email" required type="email" />
          <v-text-field v-model="password" label="Password" required type="password" />

          <v-btn block color="primary" :loading="authStore.loading" type="submit">
            Register
          </v-btn>
        </v-form>
        <div v-if="authStore.error" class="text-error mt-2">
          {{ authStore.error }}
        </div>
      </v-card-text>

      <v-card-actions>
        <v-btn text to="/auth/login">Already have an account? Login</v-btn>
      </v-card-actions>
    </v-card>
  </v-container>
</template>

  <script setup>
  import { ref } from 'vue'
  import { useAuthStore } from '@/stores/authStore'

  const authStore = useAuthStore()
  const name = ref('')
  const email = ref('')
  const password = ref('')

  function onSubmit () {
    authStore.register({ name: name.value, email: email.value, password: password.value })
  }
  </script>
