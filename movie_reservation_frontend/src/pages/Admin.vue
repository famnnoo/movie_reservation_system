<template>
  <app-header />
  <div class="spacer"></div>

  <v-container class="pa-6" fluid>

    <!-- Tabs -->
    <v-tabs v-model="activeTab" bg-color="white" class="mb-4" color="primary">
      <v-tab value="users">
        <v-icon class="mr-2">mdi-account-multiple</v-icon>
        Users
      </v-tab>
      <v-tab value="reservations">
        <v-icon class="mr-2">mdi-ticket</v-icon>
        Reservations
      </v-tab>
      <v-tab value="movies">
        <v-icon class="mr-2">mdi-movie</v-icon>
        Movies
      </v-tab>
    </v-tabs>

    <!-- Tab Content -->
    <v-window v-model="activeTab">
      <!-- Users Tab -->
      <v-window-item value="users">
        <v-card elevation="2">
          <v-card-title class="d-flex align-center justify-space-between">
            <span>User Management ({{ users.length }} users)</span>
            <v-btn
              color="primary"
              prepend-icon="mdi-account-plus"
              @click="openCreateUserDialog"
            >
              Add User
            </v-btn>
          </v-card-title>

          <v-card-text>
            <v-data-table
              class="elevation-1"
              :headers="userHeaders"
              :items="users"
              :loading="loading"
            >
              <template #item.roles="{ item }">
                <v-chip
                  v-for="role in item.roles"
                  :key="role"
                  class="mr-1"
                  :color="role === 'ADMIN' ? 'error' : 'primary'"
                  size="small"
                >
                  {{ role }}
                </v-chip>
              </template>

              <template #item.createdAt="{ item }">
                {{ formatDate(item.createdAt) }}
              </template>

              <template #item.actions="{ item }">
                <v-btn
                  color="primary"
                  icon="mdi-pencil"
                  size="small"
                  variant="text"
                  @click="openEditUserDialog(item)"
                />
                <v-tooltip :text="isCurrentUser(item) && item.roles.includes('ADMIN') ? 'Cannot remove your own admin role' : item.roles.includes('ADMIN') ? 'Remove admin role' : 'Grant admin role'" location="top">
                  <template #activator="{ props }">
                    <v-btn
                      v-bind="props"
                      :color="item.roles.includes('ADMIN') ? 'warning' : 'success'"
                      :icon="item.roles.includes('ADMIN') ? 'mdi-shield-off' : 'mdi-shield-account'"
                      size="small"
                      variant="text"
                      :disabled="isCurrentUser(item) && item.roles.includes('ADMIN')"
                      @click="toggleAdminRole(item)"
                    />
                  </template>
                </v-tooltip>
                <v-btn
                  color="info"
                  icon="mdi-eye"
                  size="small"
                  variant="text"
                  @click="viewUserReservations(item)"
                />
                <v-tooltip :text="isCurrentUser(item) ? 'Cannot delete your own account' : 'Delete user'" location="top">
                  <template #activator="{ props }">
                    <v-btn
                      v-bind="props"
                      color="error"
                      icon="mdi-delete"
                      size="small"
                      variant="text"
                      :disabled="isCurrentUser(item)"
                      @click="confirmDeleteUser(item)"
                    />
                  </template>
                </v-tooltip>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-window-item>

      <!-- Reservations Tab -->
      <v-window-item value="reservations">
        <v-card elevation="2">
          <v-card-title>
            Reservation Management ({{ reservations.length }} reservations)
          </v-card-title>

          <v-card-text>
            <v-data-table
              class="elevation-1"
              :headers="reservationHeaders"
              :items="reservations"
              :loading="loading"
            >
              <template #item.movieTitle="{ item }">
                <div class="d-flex align-center">
                  <v-icon class="mr-2">mdi-movie</v-icon>
                  {{ item.movieTitle }}
    </div>
              </template>

              <template #item.displayTime="{ item }">
                {{ formatDateTime(item.displayTime) }}
              </template>

              <template #item.seats="{ item }">
                <v-chip color="primary" size="small">
                  {{ Array.from(item.seats).join(', ') }}
                </v-chip>
              </template>

              <template #item.reservationDate="{ item }">
                {{ formatDate(item.reservationDate) }}
              </template>

              <template #item.actions="{ item }">
                <v-btn
                  color="error"
                  icon="mdi-delete"
                  size="small"
                  variant="text"
                  @click="confirmDeleteReservation(item)"
                />
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-window-item>

      <!-- Movies Tab -->
      <v-window-item value="movies">
        <v-card elevation="2">
          <v-card-title>
            Movie Management ({{ movieStore.movies.length }} movies)
          </v-card-title>

          <v-card-text>
            <p class="text-subtitle-1">
              Movie management is available in the main Movies section.
              Navigate to the home page to view and manage movies.
            </p>
            <v-btn color="primary" prepend-icon="mdi-home" to="/">
              Go to Movies
            </v-btn>
          </v-card-text>
        </v-card>
      </v-window-item>
    </v-window>
  </v-container>

  <!-- Create/Edit User Dialog -->
  <v-dialog v-model="userDialog" max-width="600">
    <v-card>
      <v-card-title class="bg-gradient">
        <span class="text-h5">
          {{ editingUser ? 'Edit User' : 'Create New User' }}
        </span>
      </v-card-title>

      <v-card-text class="pt-6">
        <v-text-field
          v-model="userForm.name"
          label="Name"
          prepend-icon="mdi-account"
          required
          variant="outlined"
        />
        <v-text-field
          v-model="userForm.email"
          label="Email"
          prepend-icon="mdi-email"
          required
          type="email"
          variant="outlined"
        />
        <v-text-field
          v-model="userForm.password"
          :hint="editingUser ? 'Leave blank to keep current password' : ''"
          label="Password"
          prepend-icon="mdi-lock"
          :required="!editingUser"
          type="password"
          variant="outlined"
        />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="grey" variant="text" @click="userDialog = false">
          Cancel
        </v-btn>
        <v-btn color="primary" variant="elevated" @click="saveUser">
          {{ editingUser ? 'Update' : 'Create' }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- Delete Confirmation Dialog -->
  <v-dialog v-model="deleteDialog" max-width="500">
    <v-card>
      <v-card-title class="text-h5 text-error">
        Confirm Deletion
      </v-card-title>

      <v-card-text>
        {{ deleteMessage }}
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="grey" variant="text" @click="deleteDialog = false">
          Cancel
        </v-btn>
        <v-btn color="error" variant="elevated" @click="confirmDelete">
          Delete
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- User Reservations Dialog -->
  <v-dialog v-model="userReservationsDialog" max-width="800">
    <v-card>
      <v-card-title class="bg-gradient">
        <span class="text-h5">
          Reservations for {{ selectedUserName }}
        </span>
      </v-card-title>

      <v-card-text class="pt-4">
        <v-list v-if="userReservations.length > 0">
          <v-list-item
            v-for="reservation in userReservations"
            :key="reservation.id"
            class="mb-2"
          >
            <v-list-item-title>
              {{ reservation.movieTitle }}
            </v-list-item-title>
            <v-list-item-subtitle>
              {{ formatDateTime(reservation.displayTime) }} -
              Seats: {{ Array.from(reservation.seats).join(', ') }}
            </v-list-item-subtitle>
          </v-list-item>
        </v-list>
        <p v-else class="text-center text-grey">No reservations found</p>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="primary" variant="text" @click="userReservationsDialog = false">
          Close
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
  import { onMounted, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import AppHeader from '@/components/AppHeader.vue'
  import api from '@/plugins/axios'
  import { useAuthStore } from '@/stores/authStore'
  import { useMovieStore } from '@/stores/movieStore'

  const router = useRouter()
  const authStore = useAuthStore()
  const movieStore = useMovieStore()

  const activeTab = ref('users')
  const loading = ref(false)
  const currentUserId = ref(null)

  // User Management
  const users = ref([])
  const userDialog = ref(false)
  const editingUser = ref(null)
  const userForm = ref({
    name: '',
    email: '',
    password: '',
  })

  const userHeaders = [
    { title: 'ID', key: 'id', sortable: true },
    { title: 'Name', key: 'name', sortable: true },
    { title: 'Email', key: 'email', sortable: true },
    { title: 'Roles', key: 'roles' },
    { title: 'Created', key: 'createdAt', sortable: true },
    { title: 'Actions', key: 'actions', sortable: false },
  ]

  // Reservation Management
  const reservations = ref([])
  const userReservations = ref([])
  const userReservationsDialog = ref(false)
  const selectedUserName = ref('')

  const reservationHeaders = [
    { title: 'ID', key: 'id', sortable: true },
    { title: 'Movie', key: 'movieTitle', sortable: true },
    { title: 'Show Time', key: 'displayTime', sortable: true },
    { title: 'Seats', key: 'seats' },
    { title: 'Reserved', key: 'reservationDate', sortable: true },
    { title: 'Actions', key: 'actions', sortable: false },
  ]

  // Delete Dialog
  const deleteDialog = ref(false)
  const deleteMessage = ref('')
  const deleteAction = ref(null)

  onMounted(async () => {
    // Get current user ID from localStorage
    currentUserId.value = parseInt(localStorage.getItem('userId'))
    
    await fetchUsers()
    await fetchReservations()
  })

  function isCurrentUser(user) {
    return user.id === currentUserId.value
  }

  // ==================== User Management ====================

  async function fetchUsers () {
    loading.value = true
    try {
      const response = await api.get('/admin/users')
      users.value = response.data
    } catch (error) {
      console.error('Failed to fetch users:', error)
      alert('Failed to load users')
    } finally {
      loading.value = false
    }
  }

  function openCreateUserDialog () {
    editingUser.value = null
    userForm.value = { name: '', email: '', password: '' }
    userDialog.value = true
  }

  function openEditUserDialog (user) {
    editingUser.value = user
    userForm.value = {
      name: user.name,
      email: user.email,
      password: '',
    }
    userDialog.value = true
  }

  async function saveUser () {
    try {
      if (editingUser.value) {
        // Update
        await api.put(`/admin/users/${editingUser.value.id}`, userForm.value)
        alert('User updated successfully')
      } else {
        // Create
        await api.post('/admin/users', userForm.value)
        alert('User created successfully')
      }
      userDialog.value = false
      await fetchUsers()
    } catch (error) {
      console.error('Failed to save user:', error)
      alert(error.response?.data?.message || 'Failed to save user')
    }
  }

  async function toggleAdminRole (user) {
    try {
      await api.post(`/admin/users/${user.id}/toggle-admin`)
      alert(`Admin role ${user.roles.includes('ADMIN') ? 'removed from' : 'granted to'} ${user.name}`)
      await fetchUsers()
    } catch (error) {
      console.error('Failed to toggle admin role:', error)
      const errorMessage = error.response?.data?.message || error.response?.data || 'Failed to update user role'
      alert(errorMessage)
    }
  }

  async function viewUserReservations (user) {
    try {
      selectedUserName.value = user.name
      const response = await api.get(`/admin/users/${user.id}/reservations`)
      userReservations.value = response.data
      userReservationsDialog.value = true
    } catch (error) {
      console.error('Failed to fetch user reservations:', error)
      alert('Failed to load user reservations')
    }
  }

  function confirmDeleteUser (user) {
    deleteMessage.value = `Are you sure you want to delete user "${user.name}"? This action cannot be undone.`
    deleteAction.value = () => deleteUser(user.id)
    deleteDialog.value = true
  }

  async function deleteUser (userId) {
    try {
      await api.delete(`/admin/users/${userId}`)
      alert('User deleted successfully')
      await fetchUsers()
    } catch (error) {
      console.error('Failed to delete user:', error)
      alert('Failed to delete user')
    }
  }

  // ==================== Reservation Management ====================

  async function fetchReservations () {
    loading.value = true
    try {
      const response = await api.get('/admin/reservations')
      reservations.value = response.data
    } catch (error) {
      console.error('Failed to fetch reservations:', error)
      alert('Failed to load reservations')
    } finally {
      loading.value = false
    }
  }

  function confirmDeleteReservation (reservation) {
    deleteMessage.value = `Are you sure you want to delete this reservation for "${reservation.movieTitle}"?`
    deleteAction.value = () => deleteReservation(reservation.id)
    deleteDialog.value = true
  }

  async function deleteReservation (reservationId) {
    try {
      await api.delete(`/admin/reservations/${reservationId}`)
      alert('Reservation deleted successfully')
      await fetchReservations()
    } catch (error) {
      console.error('Failed to delete reservation:', error)
      alert('Failed to delete reservation')
    }
  }

  // ==================== Common ====================

  function confirmDelete () {
    if (deleteAction.value) {
      deleteAction.value()
    }
    deleteDialog.value = false
  }

  function handleLogout () {
    authStore.logout()
    router.push('/Login')
  }

  function formatDate (date) {
    if (!date) return 'N/A'
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    })
  }

  function formatDateTime (date) {
    if (!date) return 'N/A'
    return new Date(date).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    })
  }
</script>

<style scoped>
.bg-gradient {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
}

.spacer {
  height: 64px;
}
</style>
