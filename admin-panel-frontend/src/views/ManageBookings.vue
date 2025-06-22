<template>
  <div class="manage-bookings">
    <h2 class="section-title">Manage Bookings</h2>
    <div class="bookings-list">
      <div v-for="booking in bookings" :key="booking.id" class="booking-card">
        <div class="booking-info">
          <p><strong>Movie:</strong> {{ booking.movieTitle }}</p>
          <p><strong>Showtime:</strong> {{ booking.showtime }}</p>
          <p><strong>Date:</strong> {{ booking.date }}</p>
          <p><strong>Total:</strong> {{ booking.total }} VND</p>
          <p><strong>Status:</strong> {{ booking.status }}</p>
          <p><strong>User:</strong> {{ booking.username || booking.userId || 'Unknown' }}</p>
          <p><strong>Room:</strong> {{ booking.roomName }}</p>
        </div>
        <div class="seats-list">
          <p><strong>Seats:</strong></p>
          <ul>
            <li v-for="seat in booking.seats" :key="seat">{{ seat }}</li>
          </ul>
        </div>
        <div v-if="booking.status === 'WAITING_BOOKING'" class="action-buttons">
          <button @click="acceptBooking(booking.id)" class="action-btn accept">Accept</button>
          <button @click="rejectBooking(booking.id)" class="action-btn reject">Reject</button>
        </div>
        <div v-if="booking.status === 'WAITING_CANCEL'" class="action-buttons">
          <button @click="acceptCancelBooking(booking.id)" class="action-btn accept">Accept Cancel</button>
          <button @click="rejectCancelBooking(booking.id)" class="action-btn reject">Reject Cancel</button>
        </div>
      </div>
    </div>
    <button @click="fetchBookings" class="refresh-btn">Refresh Bookings</button>
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
        console.log("Fetched bookings:", this.bookings);
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
.manage-bookings {
  max-width: 100%;
  padding: 20px;
}

.section-title {
  color: #ffcc00;
  font-size: 24px;
  margin-bottom: 20px;
}

.bookings-list {
  display: grid;
  gap: 20px;
  max-width: 100%;
}

.booking-card {
  background-color: #2c2c2c;
  padding: 15px;
  border-radius: 8px;
  border-left: 4px solid #ffcc00;
}

.booking-info, .seats-list {
  margin-bottom: 10px;
}

.booking-info p, .seats-list p {
  margin: 5px 0;
  color: #e0e0e0;
}

.seats-list ul {
  list-style: none;
  padding-left: 0;
}

.seats-list li {
  color: #b0b0b0;
  margin-left: 20px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 8px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.action-btn.accept {
  background-color: #4CAF50;
  color: white;
}

.action-btn.accept:hover {
  background-color: #45a049;
}

.action-btn.reject {
  background-color: #f44336;
  color: white;
}

.action-btn.reject:hover {
  background-color: #da190b;
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