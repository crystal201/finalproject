<template>
  <div>
    <h2>Manage Bookings</h2>
    <ul>
      <li v-for="booking in bookings" :key="booking.id">
        {{ booking.movieTitle }} (Showtime: {{ booking.showtime }}, Date: {{ booking.date }}, Total: {{ booking.total }} VND, Status: {{ booking.status }})
        <ul>
          <li v-for="seat in booking.seats" :key="seat">{{ seat }}</li>
        </ul>
        <p>Room: {{ booking.roomName }}</p>
      </li>
    </ul>
    <button @click="fetchBookings">Refresh Bookings</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      bookings: []
    }
  },
  methods: {
    async fetchBookings() {
      try {
        const response = await this.$axios.get('/api/bookings');
        this.bookings = response.data;
      } catch (error) {
        console.error("API Error:", error);
        this.bookings = [];
      }
    }
  },
  mounted() {
    this.fetchBookings();
  }
}
</script>