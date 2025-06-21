<template>
  <div class="modern-booking-page">
    <!-- Movie Header -->
    <div class="movie-header" v-if="movie">
      <h2 class="movie-title">
        Booking ticket for <span>{{ movie.title }}</span>
      </h2>
      <div class="movie-meta">
        <span class="runtime">{{ movie.runtime }} minutes</span>
      </div>
    </div>

    <!-- Booking Sections -->
    <div class="booking-sections">
      <!-- Room Selection -->
      <section class="booking-section room-section">
        <h3 class="section-title">
          <i class="fas fa-door-open"></i> Select a room
          <span v-if="selectedRoom" class="selected-info"
            >({{ rooms.find((r) => r.id === selectedRoom)?.roomName }} - Capacity: {{ rooms.find((r) => r.id === selectedRoom)?.capacity }} seats)</span
          >
        </h3>
        <div class="room-grid">
          <button
            v-for="room in rooms"
            :key="room.id"
            class="room-btn"
            :class="{ selected: selectedRoom === room.id }"
            @click="selectRoom(room.id)"
          >
            {{ room.roomName }} ({{ room.capacity }} seats)
          </button>
        </div>
      </section>

      <!-- Showtime Selection -->
      <section class="booking-section showtime-section">
        <h3 class="section-title">
          <i class="fas fa-clock"></i> Select showtime
          <span v-if="selectedDate && selectedShowtime" class="selected-info">
            ({{ formatDateDisplay(selectedDate) }} - {{ selectedShowtime }})
          </span>
        </h3>
        <div class="day-tabs">
          <button
            v-for="day in availableDays"
            :key="day.date"
            class="day-btn"
            :class="{ selected: selectedDate === day.date }"
            @click="selectDay(day.date)"
          >
            {{ day.label }}<br />{{ formatDateDisplay(day.date) }}
          </button>
        </div>
        <div class="showtime-grid" v-if="selectedDate">
          <button
            v-for="time in availableShowtimes"
            :key="time"
            class="showtime-btn"
            :class="{ selected: selectedShowtime === time }"
            @click="selectShowtime(time)"
          >
            <span class="time">{{ time }}</span>
          </button>
        </div>
      </section>

      <!-- Seat Selection -->
      <section class="booking-section seat-section" :key="seatsKey">
        <h3 class="section-title"><i class="fas fa-chair"></i> Choose seats</h3>
        <div class="cinema-screen">SCREEN</div>
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
            <span>Available</span>
          </div>
          <div class="legend-item">
            <div class="seat-sample selected"></div>
            <span>Selected</span>
          </div>
          <div class="legend-item">
            <div class="seat-sample taken"></div>
            <span>Booked</span>
          </div>
        </div>
      </section>

      <!-- Booking Summary -->
      <section class="booking-section summary-section">
        <h3 class="section-title">
          <i class="fas fa-receipt"></i> Booking summary
        </h3>
        <div class="summary-content">
          <div class="summary-item">
            <span class="label">Movie:</span>
            <span class="value">{{ movie?.title || "--" }}</span>
          </div>
          <div class="summary-item">
            <span class="label">Room:</span>
            <span class="value">{{
              selectedRoom
                ? rooms.find((r) => r.id === selectedRoom)?.roomName
                : "--"
            }} ({{ selectedRoom ? rooms.find((r) => r.id === selectedRoom)?.capacity : "--" }} seats)</span>
          </div>
          <div class="summary-item">
            <span class="label">Showtime:</span>
            <span class="value">
              {{ selectedShowtime || "--" }}
              <span v-if="selectedDate"
                >({{ formatDateDisplay(selectedDate) }})</span
              >
            </span>
          </div>
          <div class="summary-item">
            <span class="label">Seats:</span>
            <span class="value">{{
              selectedSeats.join(", ") || "No seats selected"
            }}</span>
          </div>
          <div class="summary-item total">
            <span class="label">Total price:</span>
            <span class="value"
              >{{ selectedSeats.length * ticketPrice }}.000 VND</span
            >
          </div>
          <div
            v-if="selectedSeats.some((seatId) => bookedSeats.includes(seatId))"
            class="error-message"
          >
            <i class="fas fa-exclamation-triangle"></i>
            <span>Some seats are already booked</span>
          </div>
          <button
            class="confirm-btn"
            :disabled="!canBook"
            @click="openConfirmModal"
          >
            <span v-if="loading">
              <i class="fas fa-spinner fa-spin"></i> Processing...
            </span>
            <span v-else>CONFIRM BOOKING</span>
          </button>
        </div>
      </section>
    </div>

    <div v-if="!movie" class="loading-message">
      <i class="fas fa-spinner fa-spin"></i> Loading movie information...
    </div>

    <ConfirmBookingModal
      v-if="showConfirmModal"
      :is-open="showConfirmModal"
      :booking-summary="bookingSummary"
      @confirm="handleConfirm"
      @cancel="closeConfirmModal"
    />
  </div>
</template>

<script>
import axios from "axios";
import ConfirmBookingModal from "../components/modal/ConfirmBookingModal.vue";

export default {
  components: { ConfirmBookingModal },
  data() {
    return {
      movie: null,
      rooms: [],
      selectedRoom: null,
      availableDays: [],
      selectedDate: null,
      selectedShowtime: null,
      availableShowtimes: [],
      seats: [],
      bookedSeats: [],
      selectedSeats: [],
      ticketPrice: 55,
      seatsKey: 0,
      loading: false,
      showConfirmModal: false,
      bookingSummary: {},
    };
  },
  computed: {
    canBook() {
      const hasBookedSeats = this.selectedSeats.some((seatId) =>
        this.bookedSeats.includes(seatId)
      );
      const isFutureShowtime = this.isShowtimeValid();
      return (
        this.selectedRoom &&
        this.selectedShowtime &&
        this.selectedDate &&
        this.selectedSeats.length > 0 &&
        !hasBookedSeats &&
        isFutureShowtime
      );
    },
  },
  created() {
    this.fetchMovieDetails();
    this.fetchRooms();
    this.generateSeats();
  },
  methods: {
    async fetchRooms() {
      try {
        const response = await axios.get("/api/rooms");
        this.rooms = response.data.map((room) => ({
          id: room.id,
          roomName: room.roomName,
          capacity: room.capacity,
        }));
        this.selectedRoom = this.rooms.length > 0 ? this.rooms[0].id : null;
        if (this.selectedRoom) {
          this.fetchBookedSeats();
        }
      } catch (error) {
        console.error("Error fetching rooms:", error);
        this.$toast.error("Error loading rooms: " + error.message);
        this.rooms = [];
      }
    },
    selectRoom(roomId) {
      this.selectedRoom = roomId;
      this.selectedSeats = [];
      this.fetchBookedSeats();
    },
    async fetchMovieDetails() {
      const movieId = this.$route.query.movieId;
      if (!movieId) {
        this.$nuxt.error({ statusCode: 404, message: "Movie not found!" });
        return;
      }
      try {
        const response = await axios.get(
          `https://api.themoviedb.org/3/movie/${movieId}`,
          {
            params: { api_key: this.$config.tmdbApiKey },
          }
        );
        this.movie = {
          id: response.data.id,
          title: response.data.title,
          runtime: response.data.runtime || 120,
        };
        this.generateAvailableDays();
      } catch (error) {
        console.error("Error fetching movie from TMDB:", error);
        this.$nuxt.error({
          statusCode: 500,
          message: "Error loading movie information",
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
    },
    generateAvailableDays() {
      const now = new Date();
      const dayLabels = [
        "Sunday",
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
      ];
      const availableDays = [];

      for (let i = 0; i < 5; i++) {
        const date = new Date(now);
        date.setDate(now.getDate() + i);
        const dateString = date.toISOString().split("T")[0];
        availableDays.push({
          date: dateString,
          label: dayLabels[date.getDay()],
        });
      }

      this.availableDays = availableDays;
      this.selectedDate = availableDays[0].date;
      this.updateShowtimes();
    },
    updateShowtimes() {
      const now = new Date();
      const isToday = this.selectedDate === now.toISOString().split("T")[0];
      const runtime = this.movie?.runtime || 120;
      const interval = runtime + 30;
      const showtimes = [];
      let currentTime = new Date(this.selectedDate);
      currentTime.setHours(15, 30, 0, 0);

      const endTime = new Date(this.selectedDate);
      endTime.setDate(endTime.getDate() + 1);
      endTime.setHours(2, 0, 0, 0);

      while (currentTime < endTime) {
        if (!isToday || currentTime > now) {
          const hours = String(currentTime.getHours()).padStart(2, "0");
          const minutes = String(currentTime.getMinutes()).padStart(2, "0");
          showtimes.push(`${hours}:${minutes}`);
        }
        currentTime.setMinutes(currentTime.getMinutes() + interval);
      }

      this.availableShowtimes = showtimes;
      this.selectedShowtime = showtimes.length > 0 ? showtimes[0] : null;
      if (this.selectedShowtime) {
        this.fetchBookedSeats();
      }
    },
    selectDay(date) {
      this.selectedDate = date;
      this.selectedShowtime = null;
      this.selectedSeats = [];
      this.updateShowtimes();
    },
    selectShowtime(time) {
      this.selectedShowtime = time;
      this.selectedSeats = [];
      this.fetchBookedSeats();
    },
    formatDateDisplay(dateString) {
      const date = new Date(dateString);
      return `${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`;
    },
    isShowtimeValid() {
      if (!this.selectedDate || !this.selectedShowtime) return false;
      const now = new Date();
      const [hours, minutes] = this.selectedShowtime.split(":").map(Number);
      const showtimeDate = new Date(this.selectedDate);
      showtimeDate.setHours(hours, minutes, 0, 0);
      return showtimeDate > now;
    },
    async fetchBookedSeats() {
      if (
        !this.movie?.id ||
        !this.selectedDate ||
        !this.selectedShowtime ||
        !this.selectedRoom
      ) {
        console.warn("Missing information to fetch booked seats:", {
          movieId: this.movie?.id,
          date: this.selectedDate,
          showtime: this.selectedShowtime,
          roomId: this.selectedRoom,
        });
        return;
      }
      try {
        const response = await axios.get("/api/bookings/check-seats", {
          params: {
            movieId: this.movie.id,
            date: this.selectedDate,
            showtime: this.selectedShowtime,
            roomId: this.selectedRoom,
          },
        });
        const bookedSeatsData = Array.isArray(response.data)
          ? response.data
          : response.data.data || [];
        this.$set(this, "bookedSeats", bookedSeatsData);
        const updatedSeats = this.seats.map((seat) => ({
          ...seat,
          taken: this.bookedSeats.includes(seat.id),
        }));
        this.$set(this, "seats", updatedSeats);
        this.seatsKey++;
      } catch (error) {
        console.error("Error fetching booked seats:", error);
        this.$set(this, "bookedSeats", []);
        this.$set(
          this,
          "seats",
          this.seats.map((seat) => ({ ...seat, taken: false }))
        );
        this.$toast.error(
          "Error loading seats: " +
            (error.response?.data?.message || error.message)
        );
      }
    },
    toggleSeat(seatId) {
      if (this.bookedSeats.includes(seatId)) {
        this.$toast.warning("This seat is already booked");
        return;
      }
      if (this.selectedSeats.includes(seatId)) {
        this.selectedSeats = this.selectedSeats.filter((id) => id !== seatId);
      } else {
        this.selectedSeats.push(seatId);
      }
    },
    openConfirmModal() {
      if (!this.canBook) return;
      this.bookingSummary = {
        movieTitle: this.movie?.title || "--",
        room: this.rooms.find((r) => r.id === this.selectedRoom)?.roomName || "--",
        showtime: this.selectedShowtime || "--",
        date: this.selectedDate,
        seats: [...this.selectedSeats],
        total: this.selectedSeats.length * this.ticketPrice,
      };
      this.showConfirmModal = true;
    },
    async handleConfirm() {
      this.closeConfirmModal();
      this.loading = true;
      const booking = {
        movieId: this.movie.id.toString(),
        movieTitle: this.movie.title,
        showtime: this.selectedShowtime,
        date: this.selectedDate,
        seats: this.selectedSeats,
        total: this.selectedSeats.length * this.ticketPrice,
        roomId: this.selectedRoom,
      };
      try {
        const response = await axios.post("/api/bookings", booking);
        this.$toast.success(response.data.message || "Booking request submitted successfully!");
        this.selectedSeats = [];
        await this.fetchBookedSeats();
        this.$router.push("/booking-history");
      } catch (error) {
        console.error("Error booking seats:", error);
        const errorMessage =
          error.response?.data?.message ||
          error.message ||
          "Unknown error while booking";
        this.$toast.error("Failed to submit booking request: " + errorMessage);
        await this.fetchBookedSeats();
      } finally {
        this.loading = false;
      }
    },
    closeConfirmModal() {
      this.showConfirmModal = false;
    },
  },
};
</script>

<style scoped>
/* Keep existing styles, update text where needed */
.day-tabs {
  display: flex;
  gap: 0.8rem;
  margin-bottom: 1rem;
  overflow-x: auto;
  padding-bottom: 0.5rem;
}

.day-btn {
  padding: 0.8rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
  text-align: center;
  min-width: 80px;
  font-size: 0.9rem;
}

.day-btn:hover {
  border-color: #3b82f6;
  transform: translateY(-2px);
}

.day-btn.selected {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

.modern-booking-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  color: #333;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}

.movie-header {
  margin-bottom: 2.5rem;
  text-align: center;
}

.movie-title {
  font-size: 1.8rem;
  color: #c5daff;
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
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
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

.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 0.8rem;
}

.room-btn {
  padding: 0.8rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
  text-align: center;
}

.room-btn:hover {
  border-color: #3b82f6;
  transform: translateY(-2px);
}

.room-btn.selected {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

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
  box-shadow: 0 4px 6px -1px rgba(59, 130, 246, 0.5),
    0 2px 4px -1px rgba(59, 130, 246, 0.06);
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

  .room-grid,
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
