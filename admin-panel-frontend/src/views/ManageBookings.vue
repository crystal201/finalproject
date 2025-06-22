<template>
  <div>
    <h2>Manage Bookings</h2>
    <ul>
      <li v-for="booking in bookings" :key="booking.id">
        {{ booking.movieTitle }} (Showtime: {{ booking.showtime }}, Date: {{ booking.date }}, Total: {{ booking.total }} VND, Status: {{ booking.status }}, User: {{ booking.username || booking.userId || 'Unknown' }})
        <ul>
          <li v-for="seat in booking.seats" :key="seat">{{ seat }}</li>
        </ul>
        <p>Room: {{ booking.roomName }}</p>
        <div v-if="booking.status === 'WAITING_BOOKING'" class="action-buttons">
          <button @click="acceptBooking(booking.id)" class="accept-btn">Accept</button>
          <button @click="rejectBooking(booking.id)" class="reject-btn">Reject</button>
        </div>
        <div v-if="booking.status === 'WAITING_CANCEL'" class="action-buttons">
          <button @click="acceptCancelBooking(booking.id)" class="accept-btn">Accept Cancel</button>
          <button @click="rejectCancelBooking(booking.id)" class="reject-btn">Reject Cancel</button>
        </div>
      </li>
    </ul>
    <button @click="fetchBookings">Refresh Bookings</button>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      bookings: []
    }
  },
  methods: {
    async fetchBookings() {
      try {
        const response = await axios.get("/api/bookings");
        this.bookings = response.data;
        console.log("Fetched bookings:", this.bookings); // Debug
      } catch (error) {
        console.error("API Error:", error);
        this.bookings = [];
      }
    },
    async acceptBooking(bookingId) {
      try {
        await axios.post(`/api/bookings/accept/${bookingId}`);
        this.$toast.success('Booking accepted!');
        await this.fetchBookings();
      } catch (error) {
        this.$toast.error('Error accepting booking: ' + error.message);
      } 
    },
    async rejectBooking(bookingId) {
      try {
        await axios.post(`/api/bookings/reject/${bookingId}`);
        this.$toast.success('Booking rejected!');
        await this.fetchBookings();
      } catch (error) {
        this.$toast.error('Error rejecting booking: ' + error.message);
      }
    },
    async acceptCancelBooking(bookingId) {
      try {
        await axios.post(`/api/bookings/accept-cancel/${bookingId}`);
        this.$toast.success('Cancellation accepted!');
        await this.fetchBookings();
      } catch (error) {
        this.$toast.error('Error accepting cancellation: ' + error.message);
      }
    },
    async rejectCancelBooking(bookingId) {
      try {
        await axios.post(`/api/bookings/reject-cancel/${bookingId}`);
        this.$toast.success('Cancellation rejected!');
        await this.fetchBookings();
      } catch (error) {
        this.$toast.error('Error rejecting cancellation: ' + error.message);
      }
    }
  },
  mounted() {
    this.fetchBookings();
  }
}
</script>

<style scoped>
.action-buttons {
  margin-top: 10px;
}
.accept-btn {
  background-color: #4CAF50;
  color: white;
  padding: 5px 10px;
  margin-right: 5px;
  border: none;
  cursor: pointer;
}
.reject-btn {
  background-color: #f44336;
  color: white;
  padding: 5px 10px;
  border: none;
  cursor: pointer;
}
</style>