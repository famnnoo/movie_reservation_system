<template>
  <app-header />

  <v-container class="mt-8">
    <v-row justify="center">
      <v-col cols="12" md="10" lg="8">
        <!-- User Information Card -->
        <v-card class="mb-6">
          <v-card-title class="text-h4 d-flex align-center justify-space-between">
            <span>My Account</span>
            <v-btn
              color="error"
              variant="elevated"
              @click="handleLogout"
            >
              <v-icon left>mdi-logout</v-icon>
              Logout
            </v-btn>
          </v-card-title>

          <v-divider />

          <v-card-text class="pa-6">
            <div v-if="userStore.loading" class="text-center my-8">
              <v-progress-circular indeterminate color="primary" />
            </div>

            <div v-else-if="userStore.user">
              <v-row>
                <v-col cols="12" sm="6">
                  <div class="mb-4">
                    <div class="text-subtitle-2 text-grey-darken-1">Name</div>
                    <div class="text-h6">{{ userStore.user.name }}</div>
                  </div>
                </v-col>
                <v-col cols="12" sm="6">
                  <div class="mb-4">
                    <div class="text-subtitle-2 text-grey-darken-1">Email</div>
                    <div class="text-h6">{{ userStore.user.email }}</div>
                  </div>
                </v-col>
                <v-col v-if="userStore.user.roles && userStore.user.roles.length > 0" cols="12">
                  <div class="mb-4">
                    <div class="text-subtitle-2 text-grey-darken-1">Roles</div>
                    <v-chip
                      v-for="role in userStore.user.roles"
                      :key="role"
                      class="mr-2 mt-2"
                      color="primary"
                      variant="outlined"
                    >
                      {{ role }}
                    </v-chip>
                  </div>
                </v-col>
              </v-row>
            </div>
          </v-card-text>
        </v-card>

        <!-- Reservations Card -->
        <v-card>
          <v-card-title class="text-h4 d-flex align-center justify-space-between">
            <span>My Reservations</span>
            <v-btn
              color="primary"
              variant="text"
              @click="fetchReservations"
            >
              <v-icon left>mdi-refresh</v-icon>
              Refresh
            </v-btn>
          </v-card-title>

          <v-divider />

          <v-card-text class="pa-6">
            <div v-if="reservationsLoading" class="text-center my-8">
              <v-progress-circular indeterminate color="primary" />
            </div>

            <div v-else-if="reservations.length === 0" class="text-center my-8">
              <v-icon size="64" color="grey">mdi-ticket-outline</v-icon>
              <div class="text-h6 text-grey-darken-1 mt-4">No reservations yet</div>
              <div class="text-subtitle-1 text-grey">Start booking your favorite movies!</div>
              <v-btn color="primary" variant="elevated" to="/" class="mt-4">
                Browse Movies
              </v-btn>
            </div>

            <div v-else>
              <v-list lines="three">
                <v-list-item
                  v-for="(reservation, index) in reservations"
                  :key="reservation.id"
                  class="mb-2"
                >
                  <template #prepend>
                    <v-avatar color="primary" size="56">
                      <v-icon size="32">mdi-movie-open</v-icon>
                    </v-avatar>
                  </template>

                  <v-list-item-title class="text-h6 mb-1">
                    {{ reservation.movieTitle || 'Movie' }}
                  </v-list-item-title>

                  <v-list-item-subtitle>
                    <div class="mb-1">
                      <v-icon size="small" class="mr-1">mdi-calendar</v-icon>
                      {{ formatDateTime(reservation.displayTime) }}
                    </div>
                    <div>
                      <v-icon size="small" class="mr-1">mdi-seat</v-icon>
                      Seats: {{ formatSeats(reservation.seats) }}
                    </div>
                  </v-list-item-subtitle>

                  <template #append>
                    <v-chip
                      :color="getStatusColor(reservation.status)"
                      variant="flat"
                    >
                      {{ reservation.status || 'Confirmed' }}
                    </v-chip>
                  </template>

                  <v-divider v-if="index < reservations.length - 1" class="mt-4" />
                </v-list-item>
              </v-list>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
  import { onMounted, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import AppHeader from '@/components/AppHeader.vue'
  import { useAuthStore } from '@/stores/authStore'
  import { useUserStore } from '@/stores/userStore'
  import api from '@/plugins/axios'

  const router = useRouter()
  const authStore = useAuthStore()
  const userStore = useUserStore()

  const reservations = ref([])
  const reservationsLoading = ref(false)

  onMounted(async () => {
    // Fetch user data if not already loaded
    if (!userStore.user) {
      await userStore.fetchUser()
    }
    
    // Fetch user's reservations
    await fetchReservations()
  })

  async function fetchReservations() {
    reservationsLoading.value = true
    try {
      const res = await api.get('/reservations')
      reservations.value = res.data
    } catch (error) {
      console.error('Failed to fetch reservations:', error)
      reservations.value = []
    } finally {
      reservationsLoading.value = false
    }
  }

  function handleLogout() {
    authStore.logout()
  }

  function formatDateTime(dateTime) {
    if (!dateTime) return 'N/A'
    try {
      const date = new Date(dateTime)
      return date.toLocaleString('en-US', {
        weekday: 'short',
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      })
    } catch {
      return dateTime
    }
  }

  function formatSeats(seats) {
    if (!seats) return 'N/A'
    if (Array.isArray(seats)) {
      return seats.join(', ')
    }
    return seats
  }

  function getStatusColor(status) {
    switch (status?.toLowerCase()) {
      case 'confirmed':
        return 'success'
      case 'cancelled':
        return 'error'
      case 'pending':
        return 'warning'
      default:
        return 'success'
    }
  }
</script>

