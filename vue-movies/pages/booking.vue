<template>
  <div class="booking-page">
      <h2 v-if="movie">Đặt vé cho {{ movie.title }}</h2>
      <div v-if="movie">
          <!-- Showtime Selection -->
          <section class="showtimes">
              <h3>Chọn suất chiếu ({{ selectedDate }} - {{ selectedShowtime }})</h3>
              <div class="showtime-list">
                  <button
                      v-for="showtime in showtimes"
                      :key="`${showtime.date}-${showtime.time}`"
                      :class="{
                          selected:
                              selectedShowtime === showtime.time &&
                              selectedDate === showtime.date,
                      }"
                      @click="selectShowtime(showtime)"
                  >
                      {{ showtime.time }} ({{ showtime.date }})
                  </button>
              </div>
          </section>

          <!-- Seat Selection -->
          <section class="seats" :key="seatsKey">
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
              <p>Ghế: {{ selectedSeats.join(", ") || "Chưa chọn" }}</p>
              <p
                  v-if="selectedSeats.some((seatId) => bookedSeats.includes(seatId))"
                  class="text-danger"
              >
                  ⚠ Một số ghế bạn chọn đã được đặt rồi
              </p>
              <p>Tổng cộng: ${{ selectedSeats.length * ticketPrice }}</p>
              <button :disabled="!canBook" @click="confirmBooking">
                  Xác nhận đặt vé
              </button>
          </section>
      </div>
      <p v-else>Đang tải thông tin phim...</p>
  </div>
</template>

<script>
import Vue from "vue";

export default {
  middleware: ["auth"],
  data() {
      return {
          movie: null,
          showtimes: [],
          selectedShowtime: null,
          selectedDate: null,
          seats: [],
          bookedSeats: [],
          selectedSeats: [],
          ticketPrice: 10,
          seatsKey: 0,
      };
  },
  computed: {
      canBook() {
          const hasBookedSeats = this.selectedSeats.some((seatId) =>
              this.bookedSeats.includes(seatId)
          );
          return (
              this.selectedShowtime &&
              this.selectedSeats.length > 0 &&
              !hasBookedSeats
          );
      },
  },
  created() {
      this.fetchMovieDetails();
      this.generateSeats();
  },
  watch: {
      bookedSeats(newVal) {
          console.log("bookedSeats updated:", JSON.stringify(newVal));
      },
      seats(newVal) {
          console.log("seats updated:", JSON.stringify(newVal, null, 2));
      },
  },
  methods: {
      async fetchMovieDetails() {
          const movieId = this.$route.query.movieId;
          if (!movieId) {
              this.$nuxt.error({ statusCode: 404, message: "Phim không tìm thấy" });
              return;
          }
          try {
              const response = await this.$axios.get(
                  `https://api.themoviedb.org/3/movie/${movieId}`,
                  {
                      params: {
                          api_key: this.$config.tmdbApiKey,
                      },
                  }
              );
              this.movie = {
                  id: response.data.id,
                  title: response.data.title,
                  runtime: response.data.runtime || 120,
              };
              this.generateShowtimes();
          } catch (error) {
              console.error("Lỗi khi lấy thông tin phim từ TMDB:", error);
              this.$nuxt.error({
                  statusCode: 500,
                  message: "Lỗi khi tải thông tin phim",
              });
          }
      },
      generateSeats() {
          const rows = ["A", "B", "C", "D", "E"];
          const seatsPerRow = 10;
          this.seats = [];
          rows.forEach((row) => {
              for (let i = 1; i <= seatsPerRow; i++) {
                  this.seats.push({
                      id: `${row}${i}`,
                      taken: false,
                  });
              }
          });
          console.log("Generated seats:", JSON.stringify(this.seats, null, 2));
      },
      generateShowtimes() {
          const today = new Date();
          const showtimes = [];
          const days = 3;
          const fixedHours = ["10:00", "12:00", "14:00", "16:00", "18:00", "20:00", "22:00"];

          for (let i = 0; i < days; i++) {
              const date = new Date(today);
              date.setDate(today.getDate() + i);
              const dateString = date.toISOString().split("T")[0];

              fixedHours.forEach((time) => {
                  showtimes.push({
                      date: dateString,
                      time: time,
                  });
              });
          }
          this.showtimes = showtimes;
          console.log("Generated showtimes:", JSON.stringify(this.showtimes, null, 2));
      },
      async selectShowtime(showtime) {
          this.selectedShowtime = showtime.time;
          this.selectedDate = showtime.date;
          this.selectedSeats = [];
          console.log("Selected showtime:", this.selectedShowtime, "date:", this.selectedDate);
          await this.fetchBookedSeats();
      },
      async fetchBookedSeats() {
          if (!this.movie?.id || !this.selectedDate || !this.selectedShowtime) {
              console.warn("Thiếu thông tin để lấy ghế đã đặt:", {
                  movieId: this.movie?.id,
                  date: this.selectedDate,
                  showtime: this.selectedShowtime,
              });
              return;
          }
          try {
              const response = await this.$axios.get("/api/bookings/check-seats", {
                  params: {
                      movieId: this.movie.id,
                      date: this.selectedDate,
                      showtime: this.selectedShowtime,
                  },
                  headers: { Authorization: `Bearer ${this.$store.state.auth.token}` },
              });
              console.log("Raw response:", JSON.stringify(response.data));
              const bookedSeatsData = Array.isArray(response.data) ? response.data : response.data.data || [];
              this.$set(this, "bookedSeats", bookedSeatsData);
              console.log("Booked seats:", JSON.stringify(this.bookedSeats));
              const updatedSeats = this.seats.map((seat) => {
                  const isTaken = this.bookedSeats.includes(seat.id);
                  console.log(`Checking seat ${seat.id}: isTaken=${isTaken}, bookedSeats=${JSON.stringify(this.bookedSeats)}`);
                  return {
                      ...seat,
                      taken: isTaken,
                  };
              });
              this.$set(this, "seats", updatedSeats);
              this.seatsKey++;
              console.log("Seats after update:", JSON.stringify(this.seats, null, 2));
          } catch (error) {
              console.error("Lỗi lấy ghế đã đặt:", error);
              this.$set(this, "bookedSeats", []);
              this.$set(this, "seats", this.seats.map((seat) => ({ ...seat, taken: false })));
              console.log("Seats after error:", JSON.stringify(this.seats, null, 2));
              this.$toast.error(
                  "Không thể tải danh sách ghế: " +
                      (error.response?.data?.message || error.message)
              );
          }
      },
      toggleSeat(seatId) {
          if (this.bookedSeats.includes(seatId)) {
              this.$toast.warning("Ghế này đã được đặt rồi");
              return;
          }
          if (this.selectedSeats.includes(seatId)) {
              this.selectedSeats = this.selectedSeats.filter((id) => id !== seatId);
          } else {
              this.selectedSeats.push(seatId);
          }
      },
      async confirmBooking() {
          const booking = {
              movieId: this.movie.id.toString(),
              movieTitle: this.movie.title,
              showtime: this.selectedShowtime,
              date: this.selectedDate,
              seats: this.selectedSeats,
              total: this.selectedSeats.length * this.ticketPrice,
          };

          try {
              const response = await this.$axios.post("/api/bookings", booking, {
                  headers: { Authorization: `Bearer ${this.$store.state.auth.token}` },
              });
              this.$toast.success(response.data.message || "Đặt vé thành công!");
              this.selectedSeats = [];
              await this.fetchBookedSeats();
              this.$router.push("/booking-history");
          } catch (error) {
              console.error("Lỗi khi đặt vé:", error);
              const errorMessage =
                  error.response?.data?.message ||
                  error.message ||
                  "Lỗi không xác định khi đặt vé";
              this.$toast.error("Đặt vé thất bại: " + errorMessage);
              await this.fetchBookedSeats();
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
  background: #dc3545 !important;
  color: white !important;
  cursor: not-allowed !important;
  position: relative;
}
.seat.taken::after {
  content: "✗";
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 1.2em;
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
.text-danger {
  color: #dc3545;
}
</style>