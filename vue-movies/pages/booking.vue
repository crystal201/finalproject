<template>
    <div class="modern-booking-page">
      <!-- Movie Header -->
      <div class="movie-header" v-if="movie">
        <h2 class="movie-title">Đặt vé cho <span>{{ movie.title }}</span></h2>
        <div class="movie-meta">
          <span class="runtime">{{ movie.runtime }} phút</span>
        </div>
      </div>
  
      <!-- Booking Sections -->
      <div class="booking-sections">
        <!-- Showtime Selection -->
        <section class="booking-section showtime-section">
          <h3 class="section-title">
            <i class="fas fa-clock"></i> Chọn suất chiếu
            <span v-if="selectedDate && selectedShowtime" class="selected-info">
              ({{ selectedDate }} - {{ selectedShowtime }})
            </span>
          </h3>
          <div class="showtime-grid">
            <button
              v-for="showtime in showtimes"
              :key="`${showtime.date}-${showtime.time}`"
              class="showtime-btn"
              :class="{
                selected: selectedShowtime === showtime.time && selectedDate === showtime.date,
              }"
              @click="selectShowtime(showtime)"
            >
              <span class="time">{{ showtime.time }}</span>
              <span class="date">{{ showtime.date }}</span>
            </button>
          </div>
        </section>
  
        <!-- Seat Selection -->
        <section class="booking-section seat-section" :key="seatsKey">
          <h3 class="section-title"><i class="fas fa-chair"></i> Chọn ghế</h3>
          <div class="cinema-screen">MÀN HÌNH</div>
          <div class="seat-map">
            <div class="seat-grid">
              <div
                v-for="seat in seats"
                :key="seat.id"
                class="seat"
                :class="{
                  taken: seat.taken,
                  selected: selectedSeats.includes(seat.id),
                }"
                @click="toggleSeat(seat.id)"
              >
                {{ seat.id }}
              </div>
            </div>
          </div>
          <div class="seat-legend">
            <div class="legend-item">
              <div class="seat-sample available"></div>
              <span>Có sẵn</span>
            </div>
            <div class="legend-item">
              <div class="seat-sample selected"></div>
              <span>Đã chọn</span>
            </div>
            <div class="legend-item">
              <div class="seat-sample taken"></div>
              <span>Đã đặt</span>
            </div>
          </div>
        </section>
  
        <!-- Booking Summary -->
        <section class="booking-section summary-section">
          <h3 class="section-title"><i class="fas fa-receipt"></i> Tóm tắt đặt vé</h3>
          <div class="summary-content">
            <div class="summary-item">
              <span class="label">Phim:</span>
              <span class="value">{{ movie?.title || '--' }}</span>
            </div>
            <div class="summary-item">
              <span class="label">Suất chiếu:</span>
              <span class="value">
                {{ selectedShowtime || '--' }} 
                <span v-if="selectedDate">({{ selectedDate }})</span>
              </span>
            </div>
            <div class="summary-item">
              <span class="label">Ghế:</span>
              <span class="value">{{ selectedSeats.join(", ") || "Chưa chọn" }}</span>
            </div>
            <div class="summary-item total">
              <span class="label">Tổng cộng:</span>
              <span class="value">{{ selectedSeats.length * ticketPrice }}.000đ</span>
            </div>
  
            <div v-if="selectedSeats.some((seatId) => bookedSeats.includes(seatId))" class="error-message">
              <i class="fas fa-exclamation-triangle"></i>
              <span>Một số ghế bạn chọn đã được đặt rồi</span>
            </div>
  
            <button 
              class="confirm-btn" 
              :disabled="!canBook"
              @click="confirmBooking"
            >
              <span v-if="loading">
                <i class="fas fa-spinner fa-spin"></i> Đang xử lý...
              </span>
              <span v-else>XÁC NHẬN ĐẶT VÉ</span>
            </button>
          </div>
        </section>
      </div>
  
      <div v-if="!movie" class="loading-message">
        <i class="fas fa-spinner fa-spin"></i> Đang tải thông tin phim...
      </div>
    </div>
  </template>
  
  <script>
  // Giữ nguyên toàn bộ phần script từ file gốc
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
  .modern-booking-page {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
    color: #333;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  }
  
  .movie-header {
    margin-bottom: 2.5rem;
    text-align: center;
  }
  
  .movie-title {
    font-size: 1.8rem;
    color: #2d3748;
    margin-bottom: 0.5rem;
  }
  
  .movie-title span {
    color: #3b82f6;
    font-weight: 600;
  }
  
  .movie-meta {
    display: flex;
    justify-content: center;
    gap: 1rem;
    color: #64748b;
    font-size: 0.9rem;
  }
  
  .booking-sections {
    display: grid;
    gap: 2rem;
  }
  
  .booking-section {
    background: white;
    border-radius: 12px;
    padding: 1.5rem;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  }
  
  .section-title {
    font-size: 1.2rem;
    color: #2d3748;
    margin-bottom: 1.5rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
  
  .section-title i {
    color: #3b82f6;
  }
  
  .selected-info {
    font-size: 0.9rem;
    color: #64748b;
    margin-left: 0.5rem;
  }
  
  /* Showtime Section */
  .showtime-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 0.8rem;
  }
  
  .showtime-btn {
    padding: 0.8rem;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    background: white;
    cursor: pointer;
    transition: all 0.2s;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .showtime-btn:hover {
    border-color: #3b82f6;
    transform: translateY(-2px);
  }
  
  .showtime-btn.selected {
    background: #3b82f6;
    color: white;
    border-color: #3b82f6;
  }
  
  .showtime-btn .time {
    font-weight: 600;
    font-size: 1rem;
  }
  
  .showtime-btn .date {
    font-size: 0.8rem;
    opacity: 0.8;
  }
  
  /* Seat Section */
  .cinema-screen {
    text-align: center;
    margin: 1rem 0 2rem;
    padding: 0.5rem;
    background: #f1f5f9;
    color: #475569;
    font-weight: 600;
    letter-spacing: 1px;
    position: relative;
  }
  
  .cinema-screen:before,
  .cinema-screen:after {
    content: "";
    position: absolute;
    top: 50%;
    width: 30%;
    height: 2px;
    background: linear-gradient(to right, transparent, #cbd5e1, transparent);
  }
  
  .cinema-screen:before {
    left: 0;
  }
  
  .cinema-screen:after {
    right: 0;
  }
  
  .seat-map {
    margin: 0 auto;
    max-width: 600px;
  }
  
  .seat-grid {
    display: grid;
    grid-template-columns: repeat(10, 1fr);
    gap: 0.5rem;
    margin-bottom: 2rem;
  }
  
  .seat {
    aspect-ratio: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 6px;
    background: #e2e8f0;
    cursor: pointer;
    font-size: 0.7rem;
    font-weight: 600;
    transition: all 0.2s;
  }
  
  .seat:hover {
    transform: scale(1.05);
  }
  
  .seat.taken {
    background: #fecaca;
    color: #dc2626;
    cursor: not-allowed;
    position: relative;
  }
  
  .seat.taken:after {
    content: "✗";
    position: absolute;
    font-size: 1rem;
  }
  
  .seat.selected {
    background: #3b82f6;
    color: white;
  }
  
  .seat-legend {
    display: flex;
    justify-content: center;
    gap: 1.5rem;
    flex-wrap: wrap;
  }
  
  .legend-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.9rem;
    color: #64748b;
  }
  
  .seat-sample {
    width: 20px;
    height: 20px;
    border-radius: 4px;
  }
  
  .seat-sample.available {
    background: #e2e8f0;
  }
  
  .seat-sample.selected {
    background: #3b82f6;
  }
  
  .seat-sample.taken {
    background: #fecaca;
  }
  
  /* Summary Section */
  .summary-content {
    max-width: 400px;
    margin: 0 auto;
  }
  
  .summary-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 1rem;
    padding-bottom: 1rem;
    border-bottom: 1px dashed #e2e8f0;
  }
  
  .summary-item .label {
    color: #64748b;
  }
  
  .summary-item .value {
    font-weight: 500;
  }
  
  .summary-item.total {
    margin-top: 1.5rem;
  }
  
  .summary-item.total .value {
    color: #3b82f6;
    font-weight: 600;
    font-size: 1.1rem;
  }
  
  .error-message {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.8rem;
    background: #fef2f2;
    color: #dc2626;
    border-radius: 6px;
    margin: 1.5rem 0;
    font-size: 0.9rem;
  }
  
  .error-message i {
    font-size: 1rem;
  }
  
  .confirm-btn {
    width: 100%;
    padding: 1rem;
    background: #3b82f6;
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }
  
  .confirm-btn:hover {
    background: #2563eb;
    transform: translateY(-2px);
    box-shadow: 0 4px 6px -1px rgba(59, 130, 246, 0.5), 0 2px 4px -1px rgba(59, 130, 246, 0.06);
  }
  
  .confirm-btn:disabled {
    background: #cbd5e1;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
  
  .loading-message {
    text-align: center;
    padding: 2rem;
    color: #64748b;
    font-size: 1.1rem;
  }
  
  .loading-message i {
    margin-right: 0.5rem;
  }
  
  @media (max-width: 768px) {
    .modern-booking-page {
      padding: 1rem;
    }
    
    .showtime-grid {
      grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    }
    
    .seat {
      font-size: 0.6rem;
    }
    
    .seat-legend {
      gap: 1rem;
    }
  }
  </style>