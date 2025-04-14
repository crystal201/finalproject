<!-- vue-movies/pages/booking-history.vue -->
<template>
    <div class="booking-history">
      <h2>Lịch sử đặt vé</h2>
      <div v-if="bookings.length">
        <div v-for="booking in bookings" :key="booking.id" class="booking-item">
          <h3>{{ booking.movieTitle }}</h3>
          <p>Ngày: {{ booking.date }}</p>
          <p>Suất chiếu: {{ booking.showtime }}</p>
          <p>Ghế: {{ booking.seats.join(', ') }}</p>
          <p>Tổng cộng: ${{ booking.total }}</p>
        </div>
      </div>
      <p v-else>Bạn chưa có vé nào.</p>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        bookings: [],
      };
    },
    created() {
      this.fetchBookings();
    },
    methods: {
      async fetchBookings() {
        try {
          const response = await this.$axios.get('/api/bookings', {
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`, // JWT token for auth only
            },
          });
          this.bookings = response.data; // Fetched from cinema_db
        } catch (error) {
          console.error('Lỗi khi lấy lịch sử đặt vé:', error);
          alert('Không thể tải lịch sử đặt vé.');
        }
      },
    },
  };
  </script>
  
  <style scoped>
  .booking-history {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
  }
  .booking-item {
    border: 1px solid #ccc;
    padding: 15px;
    margin-bottom: 10px;
    border-radius: 5px;
  }
  </style>