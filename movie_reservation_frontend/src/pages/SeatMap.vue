<template>
  <app-header />
  <div class="spacer">
  </div>
  <div class="reservation-container">
    <h2 class="title">Seat Reservation for {{ movieTitle }}</h2>

    <!-- Legend -->
    <div class="legend">
      <div class="legend-item">
        <div class="seat-sample available"></div>
        <span>Available</span>
      </div>
      <div class="legend-item">
        <div class="seat-sample selected"></div>
        <span>Selected</span>
      </div>
      <div class="legend-item">
        <div class="seat-sample reserved"></div>
        <span>Reserved</span>
      </div>
      <div class="legend-item">
        <div class="seat-sample premium"></div>
        <span>Premium</span>
      </div>
      <div class="legend-item">
        <div class="seat-sample vip"></div>
        <span>VIP</span>
      </div>
    </div>

    <!-- Screen -->
    <div class="screen-container">
      <div class="screen">SCREEN</div>
    </div>

    <!-- Seat Map with Labels -->
    <div class="seat-map-container">
      <!-- Column Numbers -->
      <div class="column-labels">
        <div class="row-label-spacer"></div>
        <div
          v-for="col in seatsPerRow"
          :key="'col-' + col"
          class="column-label"
        >
          {{ col }}
        </div>
      </div>

      <!-- Rows with Row Labels -->
      <div
        v-for="(row, rowIndex) in seatRows"
        :key="'row-' + rowIndex"
        class="seat-row"
      >
        <!-- Row Letter -->
        <div class="row-label">{{ String.fromCharCode(65 + rowIndex) }}</div>

        <!-- Seats in Row -->
        <button
          v-for="seat in row"
          :key="seat.seatNumber"
          :class="getSeatClass(seat)"
          :disabled="seat.reserved"
          @click="toggleSeat(seat.seatNumber)"
        >
          {{ seat.seatNumber }}
        </button>
      </div>
    </div>

    <!-- Summary and Reserve Button -->
    <div class="reservation-summary">
      <div v-if="selectedSeats.length > 0" class="selected-seats-info">
        <strong>Selected Seats:</strong> {{ selectedSeats.join(', ') }}
      </div>
      <button
        class="reserve-button"
        :disabled="selectedSeats.length === 0"
        @click="reserveSeats"
      >
        Reserve {{ selectedSeats.length }} Seat{{ selectedSeats.length !== 1 ? 's' : '' }}
      </button>
    </div>
  </div>
</template>

<script setup>

  import { computed, onMounted, ref } from 'vue'
  import { useRoute } from 'vue-router'
  import AppHeader from '@/components/AppHeader.vue'
  import api from '@/plugins/axios'

  const route = useRoute()
  const movieId = route.params.movieId
  const displayTimeId = route.params.displayTimeId
  const movieTitle = route.query.title || ''

  const allSeats = ref([])
  const selectedSeats = ref([])
  const rows = ref(10)
  const seatsPerRow = ref(15)

  // Organize seats into rows
  const seatRows = computed(() => {
    const rowsArray = []
    for (let i = 0; i < rows.value; i++) {
      const startIdx = i * seatsPerRow.value
      const endIdx = startIdx + seatsPerRow.value
      rowsArray.push(allSeats.value.slice(startIdx, endIdx))
    }
    return rowsArray
  })

  function getSeatClass(seat) {
    const classes = ['seat']
    
    // Add type class
    if (seat.type === 'VIP') {
      classes.push('vip')
    } else if (seat.type === 'PREMIUM') {
      classes.push('premium')
    } else {
      classes.push('regular')
    }

    // Add status class
    if (seat.reserved) {
      classes.push('reserved')
    } else if (selectedSeats.value.includes(seat.seatNumber)) {
      classes.push('selected')
    }

    return classes
  }

  function toggleSeat(seatNumber) {
    if (selectedSeats.value.includes(seatNumber)) {
      selectedSeats.value = selectedSeats.value.filter(s => s !== seatNumber)
    } else {
      selectedSeats.value.push(seatNumber)
    }
  }

  async function fetchSeats() {
    try {
      const res = await api.get(
        `http://localhost:8080/reservations/seats/${movieId}/${displayTimeId}`,
      )

      allSeats.value = res.data.seats
      rows.value = res.data.rows
      seatsPerRow.value = res.data.seatsPerRow
      console.log('Fetched seats', allSeats.value)
    } catch (error) {
      console.error('Failed to fetch seats', error)
    }
  }

  async function reserveSeats() {
    try {
      const payload = {
        movieId: movieId,
        displayTimeId: displayTimeId,
        seatNumbers: selectedSeats.value,
      }

      const res = await api.post(
        'http://localhost:8080/reservations/create',
        payload,
      )

      // Update UI: mark selected seats as reserved
      allSeats.value = allSeats.value.map(seat => {
        if (selectedSeats.value.includes(seat.seatNumber)) {
          return { ...seat, reserved: true }
        }
        return seat
      })
      
      selectedSeats.value = []

      alert('Seats reserved successfully!')
    } catch (error) {
      console.error('Reservation failed', error)
      alert(
        error.response?.data?.message
        || 'Some seats were already reserved. Please refresh.',
      )
    }
  }

  onMounted(fetchSeats)
</script>

<style scoped>
.reservation-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 90vh;
  padding: 32px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.title {
  margin-bottom: 32px;
  font-size: 32px;
  font-weight: 700;
  color: white;
  text-align: center;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.spacer {
  height: 64px;
}

/* Legend */
.legend {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  padding: 16px 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  flex-wrap: wrap;
  justify-content: center;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.seat-sample {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: 2px solid #ddd;
}

.seat-sample.available {
  background-color: #f5f5f5;
  border-color: #bbb;
}

.seat-sample.selected {
  background-color: #2e7d32;
  border-color: #1b5e20;
}

.seat-sample.reserved {
  background-color: #c62828;
  border-color: #8e0000;
}

.seat-sample.premium {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  border-color: #daa520;
}

.seat-sample.vip {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  border-color: #c92a2a;
}

/* Screen */
.screen-container {
  margin-bottom: 32px;
  width: 100%;
  max-width: 900px;
  display: flex;
  justify-content: center;
}

.screen {
  width: 70%;
  padding: 12px;
  background: linear-gradient(to bottom, #444, #222);
  color: white;
  text-align: center;
  font-weight: 700;
  font-size: 18px;
  border-radius: 8px 8px 40px 40px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.5);
  letter-spacing: 4px;
}

/* Seat Map Container */
.seat-map-container {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  margin-bottom: 24px;
}

/* Column Labels */
.column-labels {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.row-label-spacer {
  width: 40px;
  flex-shrink: 0;
}

.column-label {
  width: 48px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #666;
  font-size: 14px;
}

/* Seat Rows */
.seat-row {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.row-label {
  width: 40px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: #666;
  font-size: 18px;
  flex-shrink: 0;
}

/* Seats */
.seat {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  border: 2px solid #ccc;
  font-size: 11px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Regular seats */
.seat.regular {
  background-color: #f5f5f5;
  color: #333;
}

.seat.regular:hover:not(.reserved) {
  background-color: #e0e0e0;
  transform: scale(1.08);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* Premium seats */
.seat.premium {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  color: #333;
  border-color: #daa520;
  font-weight: 700;
}

.seat.premium:hover:not(.reserved) {
  background: linear-gradient(135deg, #ffed4e 0%, #ffd700 100%);
  transform: scale(1.08);
  box-shadow: 0 2px 12px rgba(255, 215, 0, 0.4);
}

/* VIP seats */
.seat.vip {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  color: white;
  border-color: #c92a2a;
  font-weight: 700;
}

.seat.vip:hover:not(.reserved) {
  background: linear-gradient(135deg, #ee5a6f 0%, #ff6b6b 100%);
  transform: scale(1.08);
  box-shadow: 0 2px 12px rgba(255, 107, 107, 0.4);
}

/* Selected state */
.seat.selected {
  background: #2e7d32 !important;
  color: white !important;
  border-color: #1b5e20 !important;
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(46, 125, 50, 0.4);
}

/* Reserved state */
.seat.reserved {
  background: #c62828 !important;
  color: white !important;
  border-color: #8e0000 !important;
  cursor: not-allowed;
  opacity: 0.7;
}

.seat.reserved:hover {
  transform: none;
}

/* Reservation Summary */
.reservation-summary {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  background: white;
  padding: 20px 32px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 400px;
}

.selected-seats-info {
  font-size: 16px;
  color: #333;
  text-align: center;
}

.reserve-button {
  padding: 14px 40px;
  font-size: 18px;
  font-weight: 700;
  border-radius: 8px;
  border: none;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.reserve-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

.reserve-button:disabled {
  background: #9e9e9e;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

/* Responsive */
@media (max-width: 1024px) {
  .seat-map-container {
    overflow-x: auto;
  }
  
  .reservation-summary {
    min-width: 300px;
  }
}
</style>
