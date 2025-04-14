<!-- vue-movies/pages/booking.vue -->
<template>
  <div class="booking-page">
    <h2 v-if="movie">Đặt vé cho {{ movie.title }}</h2>
    <div v-if="movie">
      <!-- Showtime Selection -->
      <section class="showtimes">
        <h3>Chọn suất chiếu</h3>
        <div class="showtime-list">
          <button
            v-for="showtime in showtimes"
            :key="showtime.time"
            :class="{ selected: selectedShowtime === showtime.time }"
            @click="selectShowtime(showtime)"
          >
            {{ showtime.time }} ({{ showtime.date }})
          </button>
        </div>
      </section>

      <!-- Seat Selection -->
      <section class="seats">
        <h3>Chọn ghế</h3>
        <div class="seat-grid">
          <div
            v-for="seat in seats"
            :key="seat.id"
            :class="{
              seat: true,
              taken: seat.taken,
              selected: selectedSeats.includes(seat.id),
            }"
            @click="toggleSeat(seat.id)"
          >
            {{ seat.id }}
          </div>
        </div>
      </section>

      <!-- Booking Summary -->
      <section class="summary">
        <h3>Tóm tắt đặt vé</h3>
        <p>Phim: {{ movie.title }}</p>
        <p v-if="selectedShowtime">
          Suất chiếu: {{ selectedShowtime }} ngày {{ selectedDate }}
        </p>
        <p>Ghế: {{ selectedSeats.join(', ') || 'Chưa chọn' }}</p>
        <p>Tổng cộng: ${{ selectedSeats.length * ticketPrice }}</p>
        <button :disabled="!canBook" @click="confirmBooking">Xác nhận đặt vé</button>
      </section>
    </div>
    <p v-else>Đang tải thông tin phim...</p>
  </div>
</template>

<script>

export default {
  middleware: ['auth'],
  data() {
    return {
      movie: null,
      showtimes: [],
      selectedShowtime: null,
      selectedDate: null,
      seats: [],
      selectedSeats: [],
      ticketPrice: 10,
    };
  },
  computed: {
    canBook() {
      return this.selectedShowtime && this.selectedSeats.length > 0;
    },
  },
  created() {
    this.fetchMovieDetails();
    this.generateSeats();
  },
  methods: {
    async fetchMovieDetails() {
      const movieId = this.$route.query.movieId;
      if (!movieId) {
        this.$nuxt.error({ statusCode: 404, message: 'Phim không tìm thấy' });
        return;
      }
      try {
        const response = await this.$axios.get(`/api/movies/${movieId}`);
        this.movie = response.data;
        this.generateShowtimes();
      } catch (error) {
        console.error('Lỗi khi lấy thông tin phim:', error);
        this.$nuxt.error({ statusCode: 500, message: 'Lỗi server' });
      }
    },
    generateSeats() {
      const rows = ['A', 'B', 'C', 'D', 'E'];
      const seatsPerRow = 10;
      this.seats = [];
      rows.forEach((row) => {
        for (let i = 1; i <= seatsPerRow; i++) {
          this.seats.push({
            id: `${row}${i}`,
            taken: Math.random() < 0.3, // Simulate taken seats
          });
        }
      });
    },
    generateShowtimes() {
      if (!this.movie || !this.movie.runtime) {
        console.warn('No runtime, defaulting to 120 minutes');
        this.movie = { ...this.movie, runtime: 120 };
      }
      const today = new Date();
      const showtimes = [];
      const days = 3;
      const hours = [10, 12, 14, 16, 18, 20, 22];

      for (let i = 0; i < days; i++) {
        const date = new Date(today);
        date.setDate(today.getDate() + i);
        const dateString = date.toISOString().split('T')[0];

        const numShowtimes = Math.floor(Math.random() * 3) + 2;
        const selectedHours = hours
          .sort(() => Math.random() - 0.5)
          .slice(0, numShowtimes);

        selectedHours.forEach((hour) => {
          const showtime = new Date(date);
          showtime.setHours(hour, 0, 0, 0);
          const endTime = new Date(showtime);
          endTime.setMinutes(endTime.getMinutes() + this.movie.runtime);

          if (endTime.getHours() < 23 || (endTime.getHours() === 23 && endTime.getMinutes() <= 30)) {
            showtimes.push({
              date: dateString,
              time: showtime.toTimeString().slice(0, 5),
            });
          }
        });
      }
      this.showtimes = showtimes;
    },
    selectShowtime(showtime) {
      this.selectedShowtime = showtime.time; // Select only one showtime
      this.selectedDate = showtime.date;
    },
    toggleSeat(seatId) {
      const seat = this.seats.find((s) => s.id === seatId);
      if (seat.taken) return;

      if (this.selectedSeats.includes(seatId)) {
        this.selectedSeats = this.selectedSeats.filter((id) => id !== seatId);
      } else {
        this.selectedSeats.push(seatId);
      }
    },
    async confirmBooking() {
      const booking = {
        movieId: this.movie.id,
        movieTitle: this.movie.title,
        showtime: this.selectedShowtime,
        date: this.selectedDate,
        seats: this.selectedSeats,
        total: this.selectedSeats.length * this.ticketPrice,
      };

      try {
        await this.$axios.post('/api/bookings', booking, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`, // JWT token for auth only
          },
        });
        alert('Đặt vé thành công! Dữ liệu đã được lưu vào cơ sở dữ liệu.');
        this.$router.push('/booking-history');
      } catch (error) {
        console.error('Lỗi khi đặt vé:', error);
        alert('Đặt vé thất bại.');
      }
    },
  },
};
</script>

<style scoped>
.booking-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.showtimes,
.seats,
.summary {
  margin-bottom: 20px;
}
.showtime-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.showtime-list button {
  padding: 10px;
  border: 1px solid #ccc;
  background: #fff;
  cursor: pointer;
}
.showtime-list button.selected {
  background: #007bff;
  color: white;
}
.seat-grid {
  display: grid;
  grid-template-columns: repeat(10, 40px);
  gap: 5px;
}
.seat {
  width: 40px;
  height: 40px;
  border: 1px solid #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background: #fff;
}
.seat.taken {
  background: #ccc;
  cursor: not-allowed;
}
.seat.selected {
  background: #28a745;
  color: white;
}
.summary button {
  padding: 10px 20px;
  background: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}
.summary button:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>