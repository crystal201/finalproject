<template>
  <div class="movie-recommendation-container">
    <h1>Recommended Movies</h1>
    <div v-if="!isLoggedIn" class="login-message">
      Please <a href="/login">log in</a> to view personalized movie
      recommendations.
    </div>
    <div v-else>
      <div v-if="loading" class="loading-state">
        <div class="loading-icon">
          <svg
            class="loading-svg"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"
            ></path>
          </svg>
        </div>
        <h2>Analyzing Your Movie Preferences...</h2>
        <p>
          Our AI is processing your booking history to find the perfect movie
          matches. This may take a moment.
        </p>
        <div class="progress-bar-container">
          <div class="progress-bar"></div>
        </div>
      </div>
      <div v-else-if="error" class="error-state">
        <svg
          class="error-icon"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          ></path>
        </svg>
        <p class="error-message">{{ error }}</p>
        <button @click="fetchRecommendations" class="retry-button">
          Try Again
        </button>
      </div>
      <div v-else-if="recommendedMovies.length === 0" class="empty-state">
        <svg
          class="empty-icon"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
          ></path>
        </svg>
        <h2>No Recommendations Found</h2>
        <p>
          We couldn't find any movies based on your booking history. Try
          watching more movies to get better recommendations!
        </p>
      </div>
      <div v-else>
        <p class="recommendation-description">
          Based on your viewing history, we think you'll love these movies:
        </p>
        <div class="movie-grid">
  <div v-for="movie in recommendedMovies" :key="movie.movieId" class="movie-card">
    <nuxt-link :to="`/movie/${movie.movieId}`" class="movie-link">
      <img 
        :src="movie.posterPath ? `https://image.tmdb.org/t/p/w500${movie.posterPath}` : '/placeholder-movie.jpg'" 
        alt="Movie poster" 
        class="movie-poster"
        @error="handleImageError"
      >
      <div class="movie-info">
        <h2 class="movie-title">{{ movie.title }}</h2>
        <p class="movie-genres">{{ movie.genres }}</p>
      </div>
    </nuxt-link>
  </div>
</div>
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
      loading: false,
      error: null,
      userId: localStorage.getItem("userId")
        ? parseInt(localStorage.getItem("userId"), 10)
        : null,
      token: localStorage.getItem("authToken") || null,
      isLoggedIn: !!localStorage.getItem("authToken"),
    };
  },
  async created() {
    if (this.isLoggedIn) {
      await this.fetchRecommendations();
    }
  },
  methods: {
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

        const response = await axios.get("/api/recommendations", {
          params: { user_id: this.userId },
          headers: {
            Authorization: `Bearer ${this.token}`,
          },
        });
        this.recommendedMovies = response.data || [];
      } catch (error) {
        this.error =
          error.response?.data?.message ||
          "Failed to load recommendations. Please try again.";
        console.error("Error fetching recommendations:", error);
      } finally {
        this.loading = false;
      }
    },
    handleImageError(event) {
      event.target.src = "/placeholder-movie.jpg";
    },
  },
};
</script>

<style scoped>
/* Base container styles */
.movie-recommendation-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
}

/* Header styles */
.movie-recommendation-container h1 {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
  color: #4f46e5;
}

/* Login message styles */
.login-message {
  text-align: center;
  padding: 30px;
  background-color: #fef2f2;
  border-radius: 8px;
  max-width: 500px;
  margin: 0 auto;
  color: #dc2626;
}

.login-message a {
  text-decoration: underline;
  color: #4f46e5;
  font-weight: 500;
}

.login-message a:hover {
  color: #4338ca;
}

/* Loading state styles */
.loading-state {
  text-align: center;
  padding: 40px 20px;
}

.loading-icon {
  display: inline-block;
  margin-bottom: 20px;
  animation: pulse 1.5s infinite;
}

.loading-svg {
  width: 64px;
  height: 64px;
  color: #4f46e5;
}

.loading-state h2 {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #374151;
}

.loading-state p {
  color: #6b7280;
  max-width: 500px;
  margin: 0 auto 20px;
}

.progress-bar-container {
  width: 100%;
  max-width: 500px;
  margin: 20px auto 0;
  background-color: #e5e7eb;
  border-radius: 9999px;
  height: 6px;
  overflow: hidden;
}

.progress-bar {
  background-color: #4f46e5;
  height: 100%;
  width: 0%;
  animation: progress 2s ease-in-out infinite;
}

/* Error state styles */
.error-state {
  text-align: center;
  padding: 30px;
  background-color: #fef2f2;
  border-radius: 8px;
  max-width: 500px;
  margin: 0 auto;
}

.error-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 15px;
  color: #dc2626;
}

.error-message {
  color: #dc2626;
  font-weight: 500;
  margin-bottom: 20px;
}

.retry-button {
  padding: 8px 16px;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.retry-button:hover {
  background-color: #4338ca;
}

/* Empty state styles */
.empty-state {
  text-align: center;
  padding: 30px;
  background-color: #fffbeb;
  border-radius: 8px;
  max-width: 500px;
  margin: 0 auto;
}

.empty-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 15px;
  color: #d97706;
}

.empty-state h2 {
  font-size: 20px;
  font-weight: 500;
  margin-bottom: 10px;
  color: #374151;
}

.empty-state p {
  color: #6b7280;
}

/* Movie grid styles */
.recommendation-description {
  text-align: center;
  color: #6b7280;
  margin-bottom: 30px;
}

.movie-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}

@media (min-width: 640px) {
  .movie-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 768px) {
  .movie-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 1024px) {
  .movie-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (min-width: 1280px) {
  .movie-grid {
    grid-template-columns: repeat(5, 1fr);
  }
}

/* Movie card styles */
.movie-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -1px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s;
}

.movie-card:hover {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1),
    0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.movie-poster {
  width: 100%;
  height: 320px;
  object-fit: cover;
  transition: opacity 0.3s;
}

.movie-poster:hover {
  opacity: 0.9;
}

.movie-info {
  padding: 16px;
}

.movie-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #bed2ff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.movie-genres {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 12px;
}

.ai-badge {
  font-size: 12px;
  background-color: #e0e7ff;
  color: #4f46e5;
  padding: 4px 8px;
  border-radius: 9999px;
}

.details-button {
  font-size: 14px;
  font-weight: 500;
  color: #4f46e5;
  background: none;
  border: none;
  cursor: pointer;
  transition: color 0.3s;
}

.details-button:hover {
  color: #4338ca;
}

/* Animations */
@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

@keyframes progress {
  0% {
    width: 0%;
  }
  50% {
    width: 100%;
  }
  100% {
    width: 0%;
  }
}

/* Transition effects */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>
