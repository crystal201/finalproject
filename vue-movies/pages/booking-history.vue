<template>
  <div class="dark-booking-history">
    <div class="history-header">
      <h1>Lịch sử đặt vé</h1>
      <p>Thông tin vé phim bạn đã đặt sẽ hiển thị tại đây</p>
    </div>

    <div v-if="loading" class="loading-container">
      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="#9CA3AF">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
      </svg>
      <p>Đang tải thông tin vé...</p>
    </div>

    <div v-else-if="bookings.length === 0" class="empty-history">
      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="#9CA3AF">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <p>Bạn chưa có vé nào</p>
      <router-link to="/movies" class="discover-btn">Khám phá phim mới</router-link>
    </div>

    <div v-else class="booking-list">
      <div v-for="booking in bookings" :key="booking.id" class="booking-card" :class="{ 'expired': booking.status === 'EXPIRED' }">
        <div class="movie-section">
          <img
            :src="'https://image.tmdb.org/t/p/w300' + (booking.movie.poster_path || '/placeholder-dark.jpg')"
            :alt="booking.movie.title || 'Không có tiêu đề'"
            class="movie-poster"
            @error="handleImageError"
          />
          <div class="movie-info">
            <div class="title-rating">
              <h3>{{ booking.movie.title || 'Không có tiêu đề' }}</h3>
            </div>
            <div class="genres">
              <span v-for="genre in booking.movie.genres" :key="genre.id" class="genre">
                {{ genre.name }}
              </span>
            </div>
            <p class="overview">{{ truncateOverview(booking.movie.overview) }}</p>
            <div class="meta">
              <div class="meta-item">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="#9CA3AF">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                <span>{{ booking.movie.releaseDate || 'Chưa rõ' }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="ticket-section">
          <div class="ticket-info">
            <h4>Thông tin vé</h4>
            <div class="info-row">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="#6366F1">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <div>
                <p>{{ booking.showtime || 'Chưa rõ' }}</p>
                <p class="date">{{ formatDate(booking.date) }}</p>
              </div>
            </div>
            <div class="info-row">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="#6366F1">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
              </svg>
              <div>
                <p>Ghế: {{ booking.seats?.join(', ') || 'Chưa chọn ghế' }}</p>
              </div>
            </div>
            <div class="info-row">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="#6366F1">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a2 2 0 012-2h2a2 2 0 012 2v5m-4 0h4" />
              </svg>
              <div>
                <p>Phòng: {{ getRoomName(booking.roomId) || 'Chưa rõ' }}</p>
              </div>
            </div>
          </div>
          <div class="payment-info">
            <h4>Thanh toán</h4>
            <div class="info-row">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="#6366F1">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
              <div>
                <p class="total">{{ formatCurrency(booking.total) }}</p>
                <p class="time">Đặt lúc: {{ formatDateTime(booking.createdAt) }}</p>
                <p class="status" :class="{
                    'status-active': booking.status === 'ACTIVE',
                    'status-cancelled': booking.status === 'CANCELLED',
                    'status-expired': booking.status === 'EXPIRED'
                  }">
                  Trạng thái: {{ getStatusLabel(booking.status) }}
                </p>
              </div>
            </div>
          </div>
        </div>
        <div class="action-buttons">
          <button v-if="canCancel(booking)" class="cancel-btn" @click="cancelBooking(booking.id)">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="#FFF">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
            Hủy vé
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  middleware: ['auth'],
  data() {
    return {
      bookings: [],
      latestBooking: null,
      loading: true,
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
        const response = await this.$axios.get('/api/bookings');
        const bookings = await Promise.all(
          response.data.map(async (booking) => {
            const movieResponse = await this.$axios.get(
              `https://api.themoviedb.org/3/movie/${booking.movieId}`,
              { params: { api_key: this.$config.tmdbApiKey } }
            );
            return { ...booking, movie: movieResponse.data };
          })
        );
        // Sort by createdAt descending
        this.bookings = bookings.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
        this.latestBooking = bookings.length > 0 ? bookings[0] : null;
        console.log('Selected latestBooking:', this.latestBooking);
      } catch (error) {
        console.error('Lỗi khi lấy lịch sử:', error);
        this.$toast.error('Không thể tải thông tin vé.');
      } finally {
        this.loading = false;
      }
    },
    async cancelBooking(bookingId) {
      if (!confirm('Bạn có chắc muốn hủy vé này?')) return;
      try {
        const response = await this.$axios.post(`/api/bookings/cancel/${bookingId}`);
        this.$toast.success(response.data.message || 'Hủy vé thành công!');
        await this.fetchBookings();
      } catch (err) {
        console.error('Lỗi khi hủy vé:', err);
        this.$toast.error(err.response?.data?.message || 'Không thể hủy vé.');
      }
    },
    canCancel(booking) {
      if (!booking || booking.status !== 'ACTIVE') return false;
      const showDateTime = new Date(`${booking.date} ${booking.showtime}`);
      return showDateTime > new Date();
    },
    getRoomName(roomId) {
      const room = this.rooms.find((r) => r.id === roomId);
      return room ? room.name : 'Chưa rõ';
    },
    formatDateTime(dateTime) {
      if (!dateTime) return 'Chưa rõ';
      const options = {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      };
      return new Date(dateTime).toLocaleString('vi-VN', options);
    },
    formatDate(date) {
      if (!date) return 'Chưa rõ';
      const options = { day: '2-digit', month: '2-digit', year: 'numeric' };
      return new Date(date).toLocaleDateString('vi-VN', options);
    },
    formatCurrency(amount) {
      if (!amount) return '0 ₫';
      return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
      }).format(amount * 1000);
    },
    truncateOverview(text, maxLength = 120) {
      if (!text) return 'Không có mô tả';
      return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
    },
    handleImageError(event) {
      event.target.src = '/placeholder-dark.jpg';
    },
    getStatusLabel(status) {
      switch (status) {
        case 'ACTIVE': return 'Đang hoạt động';
        case 'CANCELLED': return 'Đã hủy';
        case 'EXPIRED': return 'Hết hiệu lực';
        default: return 'Không xác định';
      }
    },
  },
};
</script>

<style scoped>
.dark-booking-history {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
  font-family: 'Inter', sans-serif;
  background-color: #111827;
  min-height: 100vh;
  color: #E5E7EB;
}

.history-header {
  text-align: center;
  margin-bottom: 3rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #374151;
}

.history-header h1 {
  font-size: 2rem;
  font-weight: 700;
  color: #F3F4F6;
  margin-bottom: 0.5rem;
}

.history-header p {
  font-size: 1.1rem;
  color: #9CA3AF;
}

.empty-history {
  text-align: center;
  padding: 3rem 1rem;
  background-color: #1F2937;
  border-radius: 0.5rem;
  margin-top: 2rem;
  border: 1px dashed #374151;
}

.empty-history svg {
  width: 4rem;
  height: 4rem;
  margin-bottom: 1rem;
}

.empty-history p {
  font-size: 1.2rem;
  color: #9CA3AF;
  margin-bottom: 1.5rem;
}

.discover-btn {
  display: inline-block;
  padding: 0.75rem 1.5rem;
  background-color: #4F46E5;
  color: white;
  border-radius: 0.375rem;
  text-decoration: none;
  font-weight: 600;
  transition: background-color 0.2s;
}

.discover-btn:hover {
  background-color: #4338CA;
}

.booking-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.booking-card {
  background-color: #1F2937;
  border-radius: 0.5rem;
  border: 1px solid #374151;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}

.booking-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.3);
  border-color: #4F46E5;
}

.movie-section {
  display: flex;
  padding: 1.5rem;
  gap: 1.5rem;
  border-bottom: 1px solid #374151;
}

.movie-poster {
  width: 150px;
  height: 225px;
  object-fit: cover;
  border-radius: 0.375rem;
  border: 1px solid #374151;
}

.movie-info {
  flex: 1;
}

.title-rating {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.75rem;
}

.title-rating h3 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #F9FAFB;
  margin: 0;
}

.rating {
  display: flex;
  align-items: center;
  background-color: #1E40AF;
  padding: 0.25rem 0.75rem;
  border-radius: 0.25rem;
  color: #EFF6FF;
}

.star {
  color: #FBBF24;
  margin-right: 0.25rem;
}

.genres {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.genre {
  background-color: #374151;
  color: #E5E7EB;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 500;
}

.overview {
  color: #9CA3AF;
  line-height: 1.5;
  margin-bottom: 1rem;
}

.meta {
  display: flex;
  gap: 1.5rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  font-size: 0.875rem;
  color: #9CA3AF;
}

.meta-item svg {
  width: 1rem;
  height: 1rem;
}

.ticket-section {
  display: flex;
  padding: 1.5rem;
  gap: 2rem;
  border-bottom: 1px solid #374151;
}

.ticket-info,
.payment-info {
  flex: 1;
}

.ticket-info h4,
.payment-info h4 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #F3F4F6;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #374151;
}

.info-row {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.info-row svg {
  width: 1.25rem;
  height: 1.25rem;
  color: #6366F1;
  flex-shrink: 0;
  margin-top: 0.125rem;
}

.date {
  font-size: 0.875rem;
  color: #9CA3AF;
}

.total {
  font-size: 1.25rem;
  font-weight: 700;
  color: #818CF8;
}

.time {
  font-size: 0.875rem;
  color: #9CA3AF;
}

.status {
  font-size: 0.875rem;
}

.status-active {
  color: #10B981;
  font-weight: 600;
}

.status-cancelled {
  color: #EF4444;
  font-weight: 600;
}

.action-buttons {
  display: flex;
  padding: 1rem 1.5rem;
  gap: 1rem;
  justify-content: flex-end;
}

.print-btn,
.rate-btn,
.cancel-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1.25rem;
  border-radius: 0.375rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.print-btn,
.rate-btn {
  background-color: rgba(99, 102, 241, 0.1);
  color: #818CF8;
  border: 1px solid #4F46E5;
}

.cancel-btn {
  background-color: #EF4444;
  color: white;
  border: none;
}

.print-btn:hover,
.rate-btn:hover {
  background-color: rgba(99, 102, 241, 0.2);
}

.cancel-btn:hover {
  background-color: #DC2626;
}

.print-btn svg,
.rate-btn svg,
.cancel-btn svg {
  width: 1rem;
  height: 1rem;
}

@media (max-width: 768px) {
  .movie-section {
    flex-direction: column;
  }
  
  .movie-poster {
    width: 100%;
    height: auto;
    max-height: 400px;
  }
  
  .ticket-section {
    flex-direction: column;
    gap: 1.5rem;
  }
  
  .action-buttons {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .title-rating {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .meta {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .print-btn,
  .rate-btn,
  .cancel-btn {
    justify-content: center;
  }
}
</style>