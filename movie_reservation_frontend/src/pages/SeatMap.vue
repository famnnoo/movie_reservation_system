<template>
  <div>
    <h2>Seat Reservation for {{ movieTitle }}</h2>
    <div class="seat-map">
      <button
        v-for="seat in seats"
        :key="seat"
        :class="['seat', { reserved: reservedSeats.includes(seat), selected: selectedSeats.includes(seat) }]"
        :disabled="reservedSeats.includes(seat)"
        @click="toggleSeat(seat)"
      >
        {{ seat }}
      </button>
    </div>
    <button :disabled="selectedSeats.length === 0" @click="reserveSeats">
      Reserve Selected Seats
    </button>
  </div>
</template>

<script setup>

  import { onMounted, ref } from 'vue'
  import { useRoute } from 'vue-router'
  import api from '@/plugins/axios'

  const route = useRoute()
  const movieId = route.params.movieId
  const displayTimeId = route.params.displayTimeId
  const movieTitle = route.query.title || ''

  const seats = ref([])
  const reservedSeats = ref([])
  const selectedSeats = ref([])

  function toggleSeat (seat) {
    if (selectedSeats.value.includes(seat)) {
      selectedSeats.value = selectedSeats.value.filter(s => s !== seat)
    } else {
      selectedSeats.value.push(seat)
    }
  }
  async function fetchSeats () {
    try {
      const res = await api.get(
        `http://localhost:8080/reservations/seats/${movieId}/${displayTimeId}`,
      )

      seats.value = res.data.availableSeats
      reservedSeats.value = res.data.reservedSeats
    } catch (error) {
      console.error('Failed to fetch seats', error)
    }
  }

  function reserveSeats () {
    console.log('Reserving seats', selectedSeats.value)
  // POST to backend here
  }

  onMounted(fetchSeats)
</script>

<style>
.seat-map {
  display: grid;
  grid-template-columns: repeat(10, 40px);
  gap: 5px;
  margin-bottom: 16px;
}
.seat {
  width: 40px;
  height: 40px;
  cursor: pointer;
}
.seat.reserved {
  background-color: red;
  cursor: not-allowed;
}
.seat.selected {
  background-color: green;
}
</style>
