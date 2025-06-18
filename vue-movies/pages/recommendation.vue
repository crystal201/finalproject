<template>
  <div class="movie-recommendation-container">
    <!-- Header Section -->
    <div class="header-section">
      <h1 class="section-title">Your Personalized Recommendations</h1>
      <p class="section-subtitle">Curated just for you based on your viewing history</p>
    </div>

    <!-- Main Content -->
    <div v-if="!isLoggedIn" class="auth-prompt">
      <div class="auth-card">
        <i class="fas fa-film auth-icon"></i>
        <h3>Personalized Movie Experience</h3>
        <p>Sign in to unlock recommendations tailored to your taste</p>
        <nuxt-link to="/login" class="auth-button">
          Sign In
          <i class="fas fa-arrow-right"></i>
        </nuxt-link>
      </div>
    </div>

    <!-- Loading State -->
    <div v-else-if="loading" class="loading-container">
      <div class="modern-loader">
        <div class="film-reel-container">
          <div class="film-reel">
            <div class="film-cell" v-for="n in 8" :key="n" :style="`--i:${n}`"></div>
          </div>
        </div>
        <div class="loading-text">
          <div class="loading-title">Crafting Your Perfect Movie List</div>
          <div class="loading-subtitle">Analyzing your preferences...</div>
          <div class="loading-progress">
            <div class="progress-bar" :style="{ width: progress + '%' }"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Movie Grid -->
    <div v-else-if="!initialLoad && recommendedMovies.length > 0" class="movie-section">
      <div class="movie-grid">
        <div v-for="movie in recommendedMovies" :key="movie.movieId" class="movie-card">
          <div class="movie-poster-container">
            <nuxt-link :to="`/movie/${movie.movieId}`">
              <img
                :src="movie.posterPath ? `https://image.tmdb.org/t/p/w500${movie.posterPath}` : '/placeholder-movie.jpg'"
                :alt="movie.title"
                class="movie-poster"
                @error="handleImageError"
              />
              <div class="movie-rating" v-if="movie.voteAverage">
                <i class="fas fa-star"></i>
                {{ movie.voteAverage.toFixed(1) }}
              </div>
            </nuxt-link>
          </div>

          <div class="movie-content">
            <div class="movie-header">
              <h3 class="movie-title">{{ movie.title }}</h3>
              <p class="movie-genres">{{ displayGenres(movie.genres) }}</p>
            </div>
          </div>

          <!-- Popup Details (Always Visible) -->
          <div class="movie-details-popup">
            <div class="popup-content">
              <div class="detail-section">
                <h4><i class="fas fa-lightbulb"></i> Why we recommend this</h4>
                <p>{{ movie.reason }}</p>
              </div>

              <div class="detail-section" v-if="movie.overview_summary">
                <h4><i class="fas fa-align-left"></i> Summary</h4>
                <p>{{ movie.overview_summary }}</p>
              </div>

              <div class="popup-actions">
                <nuxt-link :to="`/movie/${movie.movieId}`" class="more-info-btn">
                  More Info
                </nuxt-link>
                <nuxt-link :to="{ path: '/booking', query: { movieId: movie.movieId } }" class="book-now-btn">
                  Book Now
                </nuxt-link>
              </div>
            </div>
            <div class="popup-arrow"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else-if="!initialLoad && recommendedMovies.length === 0" class="empty-state">
      <div class="empty-content">
        <div class="empty-animation">
          <div class="film-icon">
            <i class="fas fa-film"></i>
          </div>
          <div class="search-icon">
            <i class="fas fa-search"></i>
          </div>
        </div>
        <h3>No Recommendations Yet</h3>
        <p>Watch more movies to get personalized recommendations</p>
        <nuxt-link to="/" class="explore-button">
          Explore Movies
        </nuxt-link>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      recommendedMovies: [],
      loading: true,
      initialLoad: true,
      error: null,
      userId: localStorage.getItem("userId")
        ? parseInt(localStorage.getItem("userId"), 10)
        : null,
      token: localStorage.getItem("authToken") || null,
      isLoggedIn: !!localStorage.getItem("authToken"),
      logs: [],
      bookingCount: 0,
      progress: 0,
      progressInterval: null
    };
  },
  computed: {
    username() {
      return this.$store.state.auth.user?.username || 'user';
    }
  },
  async created() {
    if (this.isLoggedIn) {
      this.startProgressSimulation();
      try {
        await this.fetchBookingCount();
        await this.fetchRecommendations();
      } finally {
        this.initialLoad = false;
        this.clearProgressInterval();
      }
    } else {
      this.loading = false;
      this.initialLoad = false;
    }
  },
  methods: {
    startProgressSimulation() {
      this.progressInterval = setInterval(() => {
        if (this.progress < 90) {
          this.progress += Math.floor(Math.random() * 10) + 1;
          if (this.progress > 90) this.progress = 90;
        }
      }, 500);
    },
    clearProgressInterval() {
      if (this.progressInterval) {
        clearInterval(this.progressInterval);
        this.progress = 100;
        setTimeout(() => {
          this.loading = false;
        }, 500);
      }
    },
    displayGenres(genres) {
      if (!genres) return 'Genre not specified';
      if (typeof genres === 'string') return genres;
      if (Array.isArray(genres)) {
        const validGenres = genres.filter(g => g);
        return validGenres.join(', ') || 'Genre not specified';
      }
      return 'Genre not specified';
    },
    async fetchBookingCount() {
      try {
        const response = await axios.get("/api/bookings/count", {
          params: { user_id: this.username },
          headers: {
            Authorization: `Bearer ${this.token}`,
          },
        });
        this.bookingCount = response.data || 0;
        this.logs.push(`Retrieved ${this.bookingCount} bookings for user ${this.username}`);
      } catch (error) {
        console.error("Error fetching booking count:", error);
        this.logs.push(`Error fetching booking count: ${error.message}`);
      }
    },
    async fetchRecommendations() {
      this.loading = true;
      this.error = null;
      try {
        if (!this.token) {
          throw new Error("No token found. Please log in.");
        }
        if (!this.userId) {
          throw new Error("User ID not found. Please log in again.");
        }
        const response = await this.$axios.get("/api/recommendations", {
          params: { user_id: this.userId },
          headers: {
            Authorization: `Bearer ${this.token}`,
          },
        });
        this.recommendedMovies = response.data || [];
        this.logs.push(`Gives ${this.recommendedMovies.length} movies recommended for ${this.username}.`);
      } catch (error) {
        this.error =
          error.response?.data?.error ||
          "Failed to load recommendations. Please try again.";
        console.error("Error fetching recommendations:", error);
        this.logs.push(`Lỗi: ${this.error}`);
        this.clearProgressInterval();
      }
    },
    handleImageError(event) {
      event.target.src = "/placeholder-movie.jpg";
    }
  },
  beforeDestroy() {
    this.clearProgressInterval();
  }
};
</script>

<style scoped>
/* Base Styles */
.movie-recommendation-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* Header Section */
.header-section {
  text-align: center;
  margin-bottom: 3rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  background: linear-gradient(90deg, #4f46e5, #c471ed);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 0.5rem;
}

.section-subtitle {
  color: #6b7280;
  font-size: 1.1rem;
}

/* Auth Prompt */
.auth-prompt {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}

.auth-card {
  background: white;
  border-radius: 16px;
  padding: 3rem;
  text-align: center;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
  max-width: 500px;
  width: 100%;
}

.auth-icon {
  font-size: 3rem;
  color: #4f46e5;
  margin-bottom: 1.5rem;
}

.auth-card h3 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
  color: #82aaff;
}

.auth-card p {
  color: #6b7280;
  margin-bottom: 2rem;
}

.auth-button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(90deg, #4f46e5, #7c3aed);
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s ease;
}

.auth-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(79, 70, 229, 0.3);
}

/* Modern Loading Animation */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.modern-loader {
  display: flex;
  align-items: center;
  gap: 2rem;
  max-width: 600px;
  width: 100%;
}

.film-reel-container {
  position: relative;
  width: 100px;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.film-reel {
  position: relative;
  width: 80px;
  height: 80px;
  animation: rotateReel 2s linear infinite;
}

.film-cell {
  position: absolute;
  width: 16px;
  height: 16px;
  background: #4f46e5;
  border-radius: 4px;
  top: 50%;
  left: 50%;
  margin-left: -8px;
  margin-top: -8px;
  transform: rotate(calc(var(--i) * 45deg)) translate(0, -32px);
  opacity: calc(var(--i) * 0.1);
}

.loading-text {
  flex: 1;
}

.loading-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #82aaff;
  margin-bottom: 0.5rem;
}

.loading-subtitle {
  color: #6b7280;
  margin-bottom: 1.5rem;
}

.loading-progress {
  height: 6px;
  background: #f3f4f6;
  border-radius: 3px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #4f46e5, #7c3aed);
  border-radius: 3px;
  transition: width 0.3s ease;
}

@keyframes rotateReel {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Movie Grid */
.movie-section {
  margin-top: 2rem;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 2rem;
}

.movie-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  height: 100%;
  position: relative;
}

.movie-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.12);
}

.movie-poster-container {
  position: relative;
  padding-top: 150%;
  overflow: hidden;
}

.movie-poster {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.movie-poster:hover {
  transform: scale(1.03);
}

.movie-rating {
  position: absolute;
  bottom: 1rem;
  left: 1rem;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.movie-content {
  padding: 1.5rem;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.movie-header {
  margin-bottom: 1rem;
}

.movie-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #82aaff;
  margin-bottom: 0.25rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.movie-genres {
  color: #6b7280;
  font-size: 0.9rem;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Popup Details (Always Visible) */
.movie-details-popup {
  position: relative; /* Changed to relative to keep within card */
  width: 100%;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  padding: 10px;
}

.popup-content {
  padding: 1rem;
  max-height: 300px;
  overflow-y: auto;
}

.popup-arrow {
  display: none; /* Hidden since popup is now part of card flow */
}

.detail-section {
  margin-bottom: 1.25rem;
}

.detail-section h4 {
  font-size: 0.9rem;
  font-weight: 600;
  color: #4f46e5;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.detail-section p {
  font-size: 0.9rem;
  color: #4b5563;
  line-height: 1.5;
}

.popup-actions {
  display: flex;
  gap: 0.75rem;
  margin-top: 1rem;
}

.more-info-btn {
  flex: 1;
  background: #f3f4f6;
  color: #4b5563;
  text-align: center;
  padding: 0.75rem;
  border-radius: 8px;
  font-size: 0.9rem;
  text-decoration: none;
  transition: all 0.2s ease;
}

.more-info-btn:hover {
  background: #e5e7eb;
}

.book-now-btn {
  flex: 1;
  background: linear-gradient(90deg, #4f46e5, #7c3aed);
  color: white;
  text-align: center;
  padding: 0.75rem;
  border-radius: 8px;
  font-size: 0.9rem;
  text-decoration: none;
  transition: all 0.2s ease;
}

.book-now-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(79, 70, 229, 0.3);
}

.movie-actions {
  display: flex;
  gap: 0.75rem;
  margin-top: auto;
}

.book-button {
  flex: 1;
  background: linear-gradient(90deg, #4f46e5, #7c3aed);
  color: white;
  text-align: center;
  padding: 0.75rem;
  border-radius: 8px;
  font-size: 0.9rem;
  text-decoration: none;
  transition: all 0.2s ease;
}

.book-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(79, 70, 229, 0.3);
}

/* Empty State */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.empty-content {
  text-align: center;
  max-width: 400px;
}

.empty-animation {
  position: relative;
  width: 100px;
  height: 100px;
  margin: 0 auto 2rem;
}

.film-icon {
  font-size: 4rem;
  color: #a5b4fc;
  position: absolute;
  top: 0;
  left: 0;
  animation: filmFloat 3s ease-in-out infinite;
}

.search-icon {
  font-size: 2rem;
  color: #4f46e5;
  position: absolute;
  bottom: 0;
  right: 0;
  animation: searchPulse 2s ease infinite;
}

.empty-content h3 {
  font-size: 1.5rem;
  color: #b7ceff;
  margin-bottom: 0.5rem;
}

.empty-content p {
  color: #6b7280;
  margin-bottom: 1.5rem;
}

.explore-button {
  display: inline-block;
  background: linear-gradient(90deg, #4f46e5, #7c3aed);
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s ease;
}

.explore-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(79, 70, 229, 0.3);
}

@keyframes filmFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes searchPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .section-title {
    font-size: 2rem;
  }

  .movie-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }

  .modern-loader {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .movie-actions,
  .popup-actions {
    flex-direction: column;
  }

  .movie-card {
    height: auto;
  }

  .movie-details-popup {
    width: 100%;
    left: 0;
  }
}
</style>
