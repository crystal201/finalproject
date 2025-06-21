<template>
  <div class="dark-booking-history">
    <div class="history-header">
      <h1>Booked Movie Ticket</h1>
      <p>Your latest booked movie ticket displayed here</p>
    </div>

    <div v-if="loading" class="loading-container">
      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="#9CA3AF">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
      </svg>
      <p>Loading ticket information...</p>
    </div>

    <div v-else-if="!bookings.length" class="empty-history">
      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="#9CA3AF">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <p>you don't have any booked ticket</p>
      <router-link to="/" class="discover-btn">Discover movies</router-link>
    </div>

    <div v-else class="booking-list">
      <div class="booking-card" :class="{ 'expired': bookings[bookings.length - 1].status === 'EXPIRED' }">
        <div class="movie-section">
          <img
            :src="'https://image.tmdb.org/t/p/w300' + (bookings[bookings.length - 1].movie.poster_path || '/placeholder-dark.jpg')"
            :alt="bookings[bookings.length - 1].movie.title || 'Non title'"
            class="movie-poster"
            @error="handleImageError"
          />
          <div class="movie-info">
            <div class="title-rating">
              <h3>{{ bookings[bookings.length - 1].movie.title || 'Non title' }}</h3>
            </div>
            <div class="genres">
              <span v-for="genre in bookings[bookings.length - 1].movie.genres" :key="genre.id" class="genre">
                {{ genre.name }}
              </span>
            </div>
            <p class="overview">{{ truncateOverview(bookings[bookings.length - 1].movie.overview) }}</p>
          </div>
        </div>
        <div class="ticket-section">
          <div class="ticket-info">
            <h4>Movie information</h4>
            <div class="info-row">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="#6366F1">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <div>
                <p>{{ bookings[bookings.length - 1].showtime || 'No information' }}</p>
                <p class="date">{{ formatDate(bookings[bookings.length - 1].date) }}</p>
              </div>
            </div>
            <div class="info-row">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="#6366F1">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
              </svg>
              <div>
                <p>Seats: {{ bookings[bookings.length - 1].seats?.join(', ') || 'No seat booked' }}</p>
              </div>
            </div>
            <div class="info-row">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="#6366F1">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a2 2 0 012-2h2a2 2 0 012 2v5m-4 0h4" />
              </svg>
              <div>
                <p>Room: {{ getRoomName(bookings[bookings.length - 1].roomId) || 'No information' }}</p>
              </div>
            </div>
          </div>
          <div class="payment-info">
            <h4>Total Payment</h4>
            <div class="info-row" style="display: flex; flex-direction: column;">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="#6366F1">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
              <div>
                <p class="total">{{ formatCurrency(bookings[bookings.length - 1].total) }}</p>
              </div>
              <div>
                <p class="status" :class="{
                    'status-active': bookings[bookings.length - 1].status === 'ACTIVE',
                    'status-cancelled': bookings[bookings.length - 1].status === 'CANCELLED',
                    'status-expired': bookings[bookings.length - 1].status === 'EXPIRED'
                  }">
                  Status: {{ bookings[bookings.length - 1].status }}
                </p>
              </div>
            </div>
          </div>
        </div>
        <div class="action-buttons">
          <button v-if="canCancel(bookings[bookings.length - 1])" class="cancel-btn" @click="cancelBooking(bookings[bookings.length - 1].id)">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="#FFF">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
            Cancel Booking
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
export default {
  data() {
    return {
      bookings: [],
      loading: false,
      rooms: [
        { id: 1, name: 'Room 1' },
        { id: 2, name: 'Room 2' },
        { id: 3, name: 'Room 3' },
        { id: 4, name: 'Room 4' },
      ],
    };
  },
  created() {
    this.fetchBookings();
  },
  methods: {
    async fetchBookings() {
      try {
        this.loading = true;
        const movieCache = JSON.parse(localStorage.getItem('movieCache') || '{}');
        const response = await this.$axios.get("/api/bookings", {
          params: { userId: this.$route.query.userId || '' }
        });
        if (!response.data) {
          this.bookings = [];
          return;
        }
        const booking = response.data;
        const showDateTime = new Date(`${booking.date} ${booking.showtime}`);
        const now = new Date();
        if (booking.status === 'ACTIVE' && showDateTime < now) {
          console.warn(`Booking ${booking.id} is ACTIVE but past due: ${showDateTime}`);
        }
        // Kiểm tra movieId trước khi gọi TMDB
        if (booking.movieId) {
          if (movieCache[booking.movieId]) {
            this.bookings = [{ ...booking, movie: movieCache[booking.movieId] }];
            return;
          }
          try {
            const movieResponse = await this.$axios.get(
              `https://api.themoviedb.org/3/movie/${booking.movieId}`,
              { params: { api_key: this.$config.tmdbApiKey } }
            );
            movieCache[booking.movieId] = movieResponse.data;
            localStorage.setItem('movieCache', JSON.stringify(movieCache));
            this.bookings = [{ ...booking, movie: movieResponse.data }];
          } catch (err) {
            console.warn(`Failed to fetch movie data for movieId ${booking.movieId}:`, err);
            this.bookings = [{ ...booking, movie: { poster_path: '', title: booking.movieTitle, genres: [], overview: '', releaseDate: '' } }];
          }
        } else {
          console.warn('movieId is missing in booking data:', booking);
          this.bookings = [{ ...booking, movie: { poster_path: '', title: booking.movieTitle, genres: [], overview: '', releaseDate: '' } }];
        }
      } catch (error) {
        console.error('Error fetching booking:', error);
        this.$toast.error('Không thể tải thông tin vé.');
      } finally {
        this.loading = false;
      }
    },
    async requestCancelBooking(bookingId) {
      if (!confirm('Bạn có chắc muốn yêu cầu hủy vé này?')) return;
      try {
        const response = await this.$axios.post(`/api/bookings/cancel/${bookingId}`);
        this.$toast.success(response.data.message || 'Yêu cầu hủy vé đã được gửi!');
        await this.fetchBookings();
      } catch (err) {
        console.error('Error requesting cancellation:', err);
        this.$toast.error(err.response?.data?.message || 'Không thể gửi yêu cầu hủy vé.');
      }
    },
    canCancel(booking) {
      if (!booking || booking.status !== 'ACTIVE') return false;
      const showDateTime = new Date(`${booking.date} ${booking.showtime}`);
      return showDateTime > new Date();
    },
    getRoomName(roomId) {
      const room = this.rooms.find(r => r.id === roomId);
      return room ? room.name : 'Chưa rõ';
    },
    formatDateTime(dateTime) {
      if (!dateTime) return 'N/A';
      try {
        const date = new Date(dateTime);
        return date.toLocaleString('vi-VN', {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
          hour: '2-digit',
          minute: '2-digit',
        });
      } catch {
        return 'N/A';
      }
    },
    formatDate(date) {
      if (!date) return '--';
      try {
        const d = new Date(date);
        return d.toLocaleDateString('vi-VN', {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
        });
      } catch {
        return '--';
      }
    },
    formatCurrency(amount) {
      if (!amount) return '0 ₫';
      try {
        return new Intl.NumberFormat('vi-VN', {
          style: 'currency',
          currency: 'VND',
        }).format(amount * 1000);
      } catch {
        return amount + ' ₫';
      }
    },
    truncateOverview(text, maxLength = 120) {
      if (!text) return 'Không có mô tả';
      return text.length > maxLength ? text.slice(0, maxLength) + '...' : text;
    },
    handleImageError(event) {
      event.target.src = '/placeholder-dark.jpg';
    },
  },
};
</script>


<style scoped>
.history-header{
  padding: 10px 20px;
}
.history-header h1 {
  font-size: 1.8rem;
  font-weight: bold;
  color: #e5e7eb;
  margin-bottom: 0.5rem;
}
.history-header p {
  color: #9ca3af;
  margin-bottom: 1rem;
}
.loading-container, .empty-history {
  text-align: center;
  padding: 2rem;
  color: #9ca3af;
}
.loading-container svg, .empty-history svg {
  width: 40px;
  height: 40px;
  margin-bottom: 1rem;
}
.discover-btn {
  display: inline-block;
  padding: 0.5rem 1rem;
  background-color: #6366f1;
  color: #fff;
  border-radius: 0.25rem;
  text-decoration: none;
  margin-top: 1rem;
}
.discover-btn:hover {
  background-color: #4f46e5;
}
.booking-list {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}
.booking-card {
  background-color: #374151;
  border-radius: 0.5rem;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.booking-card.expired {
  opacity: 0.7;
  border: 1px solid #dc2626;
}
.movie-section {
  display: flex;
  gap: 1rem;
  padding: 1rem;
}
.movie-poster {
  width: 120px;
  height: auto;
  border-radius: 0.25rem;
}
.movie-info {
  flex: 1;
}
.movie-info h3 {
  color: #e5e7eb;
  font-size: 1.25rem;
  margin-bottom: 0.5rem;
}
.genres {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}
.genre {
  background-color: #4b5563;
  color: #e5e7eb;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  font-size: 0.875rem;
}
.overview {
  color: #9ca3af;
  margin-bottom: 0.5rem;
}
.meta {
  display: flex;
  gap: 1rem;
  color: #9ca3af;
}
.meta-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}
.meta-item svg {
  width: 16px;
  height: 16px;
}
.ticket-section {
  display: flex;
  gap: 2rem;
  padding: 1rem;
  border-top: 1px solid #4b5563;
}
.ticket-info, .payment-info {
  flex: 1;
}
.ticket-info h4, .payment-info h4 {
  color: #e5e7eb;
  font-size: 1.125rem;
  margin-bottom: 0.5rem;
}
.info-row {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  color: #9ca3af;
}
.info-row svg {
  width: 20px;
  height: 20px;
}
.info-row p {
  margin: 0;
}
.date, .time {
  color: #d1d5db;
}
.total {
  color: #e5e7eb;
  font-weight: bold;
}
.status.status-active {
  color: #22c55e;
}
.status.status-cancelled {
  color: #ef4444;
}
.status.status-expired {
  color: #f59e0b;
}
.action-buttons {
  padding: 1rem;
  border-top: 1px solid #4b5563;
  text-align: right;
}
.cancel-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: #ef4444;
  color: #fff;
  border: none;
  border-radius: 0.25rem;
  cursor: pointer;
}
.cancel-btn:hover {
  background-color: #dc2626;
}
.cancel-btn svg {
  width: 16px;
  height: 16px;
}
</style>