<template>
  <div class="manage-seats">
    <h2 class="section-title">Manage Occupied Seats</h2>
    <div class="seats-list">
      <div v-for="seat in occupiedSeats" :key="seat.seat + seat.date + seat.showtime" class="seat-card">
        <p><strong>Room ID:</strong> {{ seat.roomId }}</p>
        <p><strong>Seat:</strong> {{ seat.seat }}</p>
        <p><strong>Date:</strong> {{ seat.date }}</p>
        <p><strong>Showtime:</strong> {{ seat.showtime }}</p>
      </div>
    </div>
    <button @click="fetchOccupiedSeats" class="refresh-btn">Refresh Occupied Seats</button>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      occupiedSeats: []
    }
  },
  methods: {
    async fetchOccupiedSeats() {
      try {
        const response = await this.axios.get('/api/bookings/occupied-seats');
        this.occupiedSeats = response.data;
      } catch (error) {
        console.error("API Error:", error);
        this.occupiedSeats = [];
      }
    }
  },
  mounted() {
    this.fetchOccupiedSeats();
  }
}
</script>

<style scoped>
.manage-seats {
  max-width: 100%;
  padding: 20px;
}

.section-title {
  color: #ffcc00;
  font-size: 24px;
  margin-bottom: 20px;
}

.seats-list {
  display: grid;
  gap: 15px;
  max-width: 100%;
}

.seat-card {
  background-color: #2c2c2c;
  padding: 15px;
  border-radius: 8px;
  border-left: 4px solid #ff4444;
}

.seat-card p {
  margin: 5px 0;
  color: #e0e0e0;
}

.refresh-btn {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #555;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.refresh-btn:hover {
  background-color: #666;
}
</style>