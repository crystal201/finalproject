<template>
  <div class="booking-history">
    <h2>Lịch sử đặt vé</h2>
    <div v-for="booking in bookings" :key="booking.id" class="booking-item">
      <div v-if="booking.movie" class="movie-card">
        <img
          :src="'https://image.tmdb.org/t/p/w200' + (booking.movie.posterPath || '/placeholder.jpg')"
          :alt="booking.movie.title || 'Không có tiêu đề'"
        />
        <div class="movie-info">
          <h4>{{ booking.movie.title || 'Không có tiêu đề' }}</h4>
          <p>
            <strong>Thể loại:</strong>
            {{ booking.movie.genreIds && Array.isArray(booking.movie.genreIds) ? booking.movie.genreIds.join(', ') : 'Không có' }}
          </p>
          <p><strong>Mô tả:</strong> {{ booking.movie.overview || 'Không có mô tả' }}</p>
          <p><strong>Ngày phát hành:</strong> {{ booking.movie.releaseDate || 'Không có' }}</p>
          <p>
            <strong>Điểm đánh giá:</strong>
            {{ booking.movie.voteAverage || 0 }} ({{ booking.movie.voteCount || 0 }} lượt)
          </p>
        </div>
      </div>
      <p><strong>Suất chiếu:</strong> {{ booking.showtime || 'Không có' }} ngày {{ booking.date || 'Không có' }}</p>
      <p>
        <strong>Ghế:</strong>
        {{ booking.seats && Array.isArray(booking.seats) ? booking.seats.join(', ') : 'Không có ghế' }}
      </p>
      <p><strong>Tổng cộng:</strong> ${{ booking.total || 0 }}</p>
      <p><strong>Đặt lúc:</strong> {{ formatDateTime(booking.createdAt) }}</p>
    </div>
  </div>
</template>

<script>
export default {
  middleware: ['auth'],
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
        const response = await this.$axios.get('/api/bookings');
        console.log('Bookings response:', response.data);
        this.bookings = await Promise.all(
          response.data.map(async (booking) => {
            console.log('Processing booking:', booking);
            const movieResponse = await this.$axios.get(
              `https://api.themoviedb.org/3/movie/${booking.movieId}`,
              { params: { api_key: this.$config.tmdbApiKey } }
            );
            return { ...booking, movie: movieResponse.data };
          })
        );
      } catch (error) {
        console.error('Lỗi khi lấy lịch sử:', error);
        this.$toast.error('Không thể tải lịch sử đặt vé.');
      }
    },
    formatDateTime(dateTime) {
      return new Date(dateTime).toLocaleString('vi-VN');
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
  padding: 10px;
  margin-bottom: 20px;
}
.movie-card {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
}
.movie-card img {
  width: 100px;
  height: auto;
}
.movie-info {
  flex: 1;
}
</style>