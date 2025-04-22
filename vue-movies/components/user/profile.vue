<template>
  <div class="profile-page">
    <h2>Thông tin cá nhân</h2>
    <p>Tên người dùng: {{ user.username }}</p>
    <p>Email: {{ user.email }}</p>
    <img :src="avatarUrl" alt="Avatar" />

    <h3>Phim đã đặt</h3>
    <div class="movie-list">
      <div v-for="movie in bookedMovies" :key="movie.id" class="movie-card">
        <img :src="'https://image.tmdb.org/t/p/w200' + movie.posterPath" :alt="movie.title" />
        <div class="movie-info">
          <h4>{{ movie.title }}</h4>
          <p><strong>Thể loại:</strong> {{ movie.genreIds.join(', ') }}</p>
          <p><strong>Mô tả:</strong> {{ movie.overview }}</p>
          <p><strong>Ngày phát hành:</strong> {{ movie.releaseDate }}</p>
          <p><strong>Điểm đánh giá:</strong> {{ movie.voteAverage }} ({{ movie.voteCount }} lượt)</p>
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
      user: this.$store.state.auth.user || {},
      bookedMovies: [],
    };
  },
  computed: {
    avatarUrl() {
      return `https://ui-avatars.com/api/?name=${this.user.username}`;
    },
  },
  created() {
    this.fetchBookedMovies();
  },
  methods: {
    async fetchBookedMovies() {
      try {
        const response = await this.$axios.get('/api/bookings/movies');
        this.bookedMovies = response.data;
      } catch (error) {
        console.error('Lỗi khi lấy danh sách phim:', error);
        this.$toast.error('Không thể tải danh sách phim.');
      }
    },
  },
};
</script>

<style scoped>
.profile-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.movie-list {
  display: grid;
  gap: 20px;
}
.movie-card {
  display: flex;
  gap: 20px;
  border: 1px solid #ccc;
  padding: 10px;
}
.movie-card img {
  width: 100px;
  height: auto;
}
.movie-info {
  flex: 1;
}
</style>