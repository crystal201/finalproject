<template>
  <div>
    <h2>Manage Occupied Seats</h2>
    <ul>
      <li v-for="seat in occupiedSeats" :key="seat.seat + seat.date + seat.showtime">
        Room ID: {{ seat.roomId }}, Seat: {{ seat.seat }}, Date: {{ seat.date }}, Showtime: {{ seat.showtime }}
      </li>
    </ul>
    <button @click="fetchOccupiedSeats">Refresh Occupied Seats</button>
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