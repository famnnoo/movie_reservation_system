<template>
  <div class="admin-page">
    <header class="admin-header">
      <h1>Admin Dashboard</h1>
      <button class="logout-btn" @click="logout">Logout</button>
    </header>

    <div class="admin-container">
      <nav class="sidebar">
        <ul>
          <li :class="{ active: activeTab === 'movies' }">
            <button @click="activeTab = 'movies'">Movies</button>
          </li>
          <li :class="{ active: activeTab === 'reservations' }">
            <button @click="activeTab = 'reservations'">Reservations</button>
          </li>
          <li :class="{ active: activeTab === 'users' }">
            <button @click="activeTab = 'users'">Users</button>
          </li>
        </ul>
      </nav>

      <main class="content">
        <section v-if="activeTab === 'movies'" class="tab-content">
          <h2>Manage Movies</h2>
          <button class="add-btn" @click="showMovieForm = true">Add Movie</button>
          <table v-if="movies.length > 0">
            <thead>
              <tr>
                <th>Title</th>
                <th>Genre</th>
                <th>Duration</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="movie in movies" :key="movie.id">
                <td>{{ movie.title }}</td>
                <td>{{ movie.genre }}</td>
                <td>{{ movie.duration }}min</td>
                <td>
                  <button @click="editMovie(movie)">Edit</button>
                  <button @click="deleteMovie(movie.id)">Delete</button>
                </td>
              </tr>
            </tbody>
          </table>
        </section>

        <section v-if="activeTab === 'reservations'" class="tab-content">
          <h2>Reservations</h2>
          <p>Total reservations: {{ reservations.length }}</p>
        </section>

        <section v-if="activeTab === 'users'" class="tab-content">
          <h2>Users</h2>
          <p>Total users: {{ users.length }}</p>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup>
  import { onMounted, ref } from 'vue'

  const activeTab = ref('movies')
  const showMovieForm = ref(false)
  const movies = ref([])
  const reservations = ref([])
  const users = ref([])

  onMounted(() => {
    fetchMovies()
    fetchReservations()
    fetchUsers()
  })

  async function fetchMovies () {
    // Replace with your API call
    movies.value = []
  }

  async function fetchReservations () {
    // Replace with your API call
    reservations.value = []
  }

  async function fetchUsers () {
    // Replace with your API call
    users.value = []
  }

  function editMovie (movie) {
    // Implement edit logic
    console.log('Edit movie:', movie)
  }

  function deleteMovie (id) {
    // Implement delete logic
    console.log('Delete movie:', id)
  }

  function logout () {
    // Implement logout logic
    console.log('Logging out')
  }
</script>

<style scoped>
.admin-page {
    min-height: 100vh;
    background-color: #f5f5f5;
}

.admin-header {
    background-color: #333;
    color: white;
    padding: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.logout-btn {
    background-color: #e74c3c;
    color: white;
    border: none;
    padding: 10px 20px;
    cursor: pointer;
    border-radius: 4px;
}

.admin-container {
    display: flex;
    gap: 20px;
    padding: 20px;
}

.sidebar {
    width: 200px;
    background-color: white;
    border-radius: 4px;
    padding: 0;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.sidebar ul {
    list-style: none;
    padding: 0;
    margin: 0;
}

.sidebar li button {
    width: 100%;
    text-align: left;
    padding: 15px;
    border: none;
    background: none;
    cursor: pointer;
    font-size: 14px;
}

.sidebar li.active button {
    background-color: #3498db;
    color: white;
}

.content {
    flex: 1;
    background-color: white;
    border-radius: 4px;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.tab-content h2 {
    margin-top: 0;
}

.add-btn {
    background-color: #27ae60;
    color: white;
    border: none;
    padding: 10px 20px;
    cursor: pointer;
    border-radius: 4px;
    margin-bottom: 20px;
}

table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
}

table thead {
    background-color: #ecf0f1;
}

table th,
table td {
    padding: 12px;
    text-align: left;
    border-bottom: 1px solid #bdc3c7;
}

table button {
    background-color: #3498db;
    color: white;
    border: none;
    padding: 5px 10px;
    cursor: pointer;
    margin-right: 5px;
    border-radius: 3px;
}

table button:last-child {
    background-color: #e74c3c;
}
</style>
