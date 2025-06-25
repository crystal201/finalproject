<template>
  <div class="profile-container">
    <!-- Header Section -->
    <div class="profile-header">
      <h1 class="profile-title">Personal Profile</h1>
      <div class="profile-breadcrumb">
        <nuxt-link to="/" class="breadcrumb-link">Home</nuxt-link>
        <span class="breadcrumb-separator">/</span>
        <span class="breadcrumb-current">Profile</span>
      </div>
    </div>

    <!-- Main Content -->
    <div v-if="loading" class="loading-container">
      <i class="fas fa-spinner fa-spin"></i>
      <p>Loading information...</p>
    </div>

    <div v-else-if="error" class="error-container">
      <i class="fas fa-exclamation-triangle"></i>
      <h3>An error occurred</h3>
      <p>{{ error }}</p>
      <button @click="retryLoading" class="retry-btn">
        <i class="fas fa-sync-alt"></i> Try again
      </button>
    </div>

    <div v-else-if="user" class="profile-content">
      <!-- User Info Card -->
      <div class="profile-card user-card">
        <div class="user-avatar-section">
          <img 
            :src="avatarUrl" 
            alt="User avatar" 
            class="user-avatar"
            @error="handleImageError"
          />
          <button class="avatar-edit-btn" @click="openAvatarEditor">
            <i class="fas fa-camera"></i>
          </button>
        </div>
        
        <div class="user-info">
          <div class="user-meta">
            <h2 class="username">{{ user.username || 'Guest' }}</h2>
            <span v-if="user.role" class="user-badge" :class="getRoleClass(user.role)">
              {{ user.role }}
            </span>
          </div>
          
          <div class="user-details">
            <div class="detail-item">
              <i class="fas fa-envelope"></i>
              <div>
                <label>Email</label>
                <p>{{ user.email || 'Not updated' }}</p>
              </div>
            </div>
            
            <div class="detail-item">
              <i class="fas fa-phone"></i>
              <div>
                <label>Phone number</label>
                <p>{{ user.phone || 'Not updated' }}</p>
              </div>
            </div>
            
          </div>
        </div>
        
        <!-- <button class="edit-profile-btn" @click="editProfile">
          <i class="fas fa-edit"></i> Edit profile
        </button> -->
      </div>

      <!-- Booking History Section -->
      <div class="profile-card booking-history">
        <div class="section-header">
          <h3><i class="fas fa-ticket-alt"></i> Booking History</h3>
          <div class="history-filter">
            <button 
              v-for="filter in filters" 
              :key="filter.value"
              class="filter-btn"
              :class="{ active: activeFilter === filter.value }"
              @click="changeFilter(filter.value)"
            >
              {{ filter.label }}
            </button>
          </div>
        </div>
        
        <div v-if="filteredBookings.length" class="booking-list">
          <div 
            v-for="booking in filteredBookings" 
            :key="booking.id" 
            class="booking-item"
            :class="{ 'expired': booking.status === 'EXPIRED' }"
          >
            <div class="booking-poster">
              <img 
                :src="getMoviePoster(booking.movie?.poster_path)"
                :alt="booking.movieTitle || 'Unknown movie'"
                @error="handlePosterError"
              />
            </div>
            
            <div class="booking-info">
              <h4 class="movie-title">{{ booking.movieTitle || 'Unknown movie' }}</h4>
              <div class="booking-meta">
                <div class="meta-item">
                  <i class="fas fa-calendar-day"></i>
                  <span>{{ formatDate(booking.date) }}</span>
                </div>
                <div class="meta-item">
                  <i class="fas fa-clock"></i>
                  <span>{{ booking.showtime || '--' }}</span>
                </div>
                <div class="meta-item">
                  <i class="fas fa-chair"></i>
                  <span>{{ formatSeats(booking.seats) }}</span>
                </div>
                <div class="meta-item">
                  <i class="fas fa-building"></i>
                  <span>{{ getRoomName(booking.roomId) || '--' }}</span>
                </div>
              </div>
              <div class="booking-footer">
                <div class="booking-total">
                  <span>Total:</span>
                  <span class="price">{{ formatCurrency(booking.total) }}</span>
                </div>
                <div class="booking-status">
                  <span :class="{
                    'status-active': booking.status === 'ACTIVE',
                    'status-cancelled': booking.status === 'CANCELLED',
                    'status-expired': booking.status === 'EXPIRED'
                  }">
                    {{ getStatusLabel(booking.status) }}
                  </span>
                </div>
              </div>
              <div class="booking-actions" v-if="canCancel(booking)">
                <button class="cancel-btn" @click="requestCancelBooking(booking.id)">
                  <i class="fas fa-times"></i> Cancel ticket
                </button>
              </div>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-history">
          <i class="fas fa-ticket-alt"></i>
          <p>{{ activeFilter === 'all' ? 'You have no tickets yet' : 'No matching tickets found' }}</p>
          <nuxt-link to="/" class="explore-btn">Explore new movies</nuxt-link>
        </div>
      </div>
    </div>
        <div v-if="showCancelConfirm" class="confirmation-dialog-overlay">
      <div class="confirmation-dialog">
        <h3>Confirm Cancellation</h3>
        <p>Are you sure you want to cancel this ticket?</p>
        <div class="dialog-buttons">
          <button @click="confirmCancel" class="confirm-btn">
            <i class="fas fa-check"></i> Confirm
          </button>
          <button @click="showCancelConfirm = false" class="cancel-btn-modal">
            <i class="fas fa-times"></i> Quit
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import axios from 'axios';
export default {
  data() {
    return {
      showCancelConfirm: false,
      bookingToCancel: null,
      loading: true,
      error: null,
      bookings: [],
      filters: [
        { value: 'all', label: 'All' },
        { value: 'waiting_booking', label: 'Waiting Booking' },
        { value: 'active', label: 'Active' },
        { value: 'waiting_cancel', label: 'Waiting Cancel' },
        { value: 'cancelled', label: 'Cancelled' },
        { value: 'expired', label: 'Expired' },
        { value: 'rejected', label: 'Rejected' }
      ],
      activeFilter: 'all',
      defaultAvatar: 'https://ui-avatars.com/api/?name=User&background=0D8ABC&color=fff&bold=true&size=128',
      defaultPoster: '/images/default-poster.jpg',
      rooms: [
        { id: 1, name: 'Room 1' },
        { id: 2, name: 'Room 2' },
        { id: 3, name: 'Room 3' },
        { id: 4, name: 'Room 4' },
      ],
    };
  },
  computed: {
    ...mapState('auth', ['user']),
    avatarUrl() {
      if (!this.user?.avatar) {
        const name = this.user?.username || 'User';
        return `https://ui-avatars.com/api/?name=${encodeURIComponent(name)}&background=0D8ABC&color=fff&bold=true&size=128`;
      }
      return this.user.avatar;
    },
    filteredBookings() {
      if (!this.bookings.length) return [];
      
      return this.bookings.filter(booking => {
        if (this.activeFilter === 'waiting_booking') {
          return booking.status === 'WAITING_BOOKING';
        }
        if (this.activeFilter === 'active') {
          return booking.status === 'ACTIVE';
        }
        if (this.activeFilter === 'waiting_cancel') {
          return booking.status === 'WAITING_CANCEL';
        }
        if (this.activeFilter === 'cancelled') {
          return booking.status === 'CANCELLED';
        }
        if (this.activeFilter === 'expired') {
          return booking.status === 'EXPIRED';
        }
        if (this.activeFilter === 'rejected') {
          return booking.status === 'REJECTED';
        }
        return true;
      });
    }
  },
  async created() {
    await this.loadData();
  },
  methods: {
    async loadData() {
    try {
      this.loading = true;
      this.error = null;
      
      if (this.user && this.user.id) { // Đảm bảo user.id có giá trị
        const response = await this.$axios.get("/api/bookings", {
          params: { filter: this.activeFilter, userId: this.user.id }
        });
        if (Array.isArray(response.data)) {
          this.bookings = await Promise.all(
            response.data.map(async (booking) => {
              try {
                const movieResponse = await this.$axios.get(
                  `https://api.themoviedb.org/3/movie/${booking.movieId}`,
                  { params: { api_key: this.$config.tmdbApiKey } }
                );
                return { ...booking, movie: movieResponse.data };
              } catch (err) {
                console.warn(`Failed to fetch movie data for movieId ${booking.movieId}:`, err);
                return { ...booking, movie: { poster_path: null, title: booking.movieTitle } };
              }
            })
          );
        } else {
          console.warn("Unexpected data format from /api/bookings:", response.data);
          this.bookings = [];
          this.$toast.error("Invalid data format from server.");
        }
      }
    } catch (err) {
      console.error('Error loading data:', err);
      this.error = err.response?.data?.message || 'Failed to load information. Please try again later.';
    } finally {
      this.loading = false;
    }
  },
    requestCancelBooking(bookingId) {
      this.bookingToCancel = bookingId;
      this.showCancelConfirm = true;
    },
    async confirmCancel() {
      try {
        const response = await this.$axios.post(`/api/bookings/cancel/${this.bookingToCancel}`);
        this.$toast.success(response.data.message || 'Yêu cầu hủy vé đã được gửi!');
        await this.loadData();
      } catch (err) {
        console.error('Error cancelling ticket:', err);
        this.$toast.error(err.response?.data?.message || 'Failed to request cancellation.');
      } finally {
        this.showCancelConfirm = false;
        this.bookingToCancel = null;
      }
    },
    canCancel(booking) {
      if (!booking || booking.status !== 'ACTIVE') return false;
      const showDateTime = new Date(`${booking.date} ${booking.showtime}`);
      return showDateTime > new Date();
    },
    retryLoading() {
      this.loadData();
    },
    getRoomName(roomId) {
      const room = this.rooms.find(r => r.id === roomId);
      return room ? room.name : 'Unknown';
    },
    getRoleClass(role) {
      if (!role) return '';
      return role.toLowerCase().replace(/\s+/g, '-');
    },
    formatSeats(seats) {
      return seats?.join(', ') || '--';
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
    formatJoinDate(dateTime) {
      if (!dateTime) return 'N/A';
      try {
        const date = new Date(dateTime);
        return date.toLocaleDateString('vi-VN', {
          year: 'numeric',
          month: 'long',
        });
      } catch {
        return 'N/A';
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
    getMoviePoster(posterPath) {
      return posterPath 
        ? `https://image.tmdb.org/t/p/w200${posterPath}`
        : this.defaultPoster;
    },
    handleImageError(e) {
      e.target.src = this.defaultAvatar;
    },
    handlePosterError(e) {
      e.target.src = this.defaultPoster;
    },
    changeFilter(filter) {
      this.activeFilter = filter;
      this.loadData();
    },
    editProfile() {
      this.$router.push('/profile/edit');
    },
    openAvatarEditor() {
      this.$toast.info('Feature under development');
    },
    getStatusLabel(status) {
      switch (status) {
        case 'WAITING_BOOKING': return 'Waiting for accept booking';
        case 'ACTIVE': return 'Active';
        case 'WAITING_CANCEL': return 'Waiting for cancelation';
        case 'CANCELLED': return 'Canceled';
        case 'EXPIRED': return 'Expired';
        case 'REJECTED': return 'Reject';
        default: return 'UnIdentify';
      }
    },
  }
};
</script>

<style scoped>
.confirmation-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.confirmation-dialog {
  background-color: white;
  padding: 1.5rem;
  border-radius: 0.5rem;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.confirmation-dialog h3 {
  color: #2d3748;
  margin-bottom: 1rem;
  font-size: 1.25rem;
}

.confirmation-dialog p {
  color: #4a5568;
  margin-bottom: 1.5rem;
}

.dialog-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

.dialog-buttons button {
  padding: 0.5rem 1rem;
  border-radius: 0.25rem;
  cursor: pointer;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.confirm-btn {
  background-color: #e53e3e;
  color: white;
  border: none;
}

.confirm-btn:hover {
  background-color: #c53030;
}

.cancel-btn-modal {
  background-color: transparent;
  color: #3d4451;
  border: 1px solid #e2e8f0;
}

.cancel-btn-modal:hover {
  background-color: #f7fafc;
}
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
}

.profile-header {
  margin-bottom: 30px;
}

.profile-title {
  font-size: 28px;
  font-weight: 700;
  color: #c3d9fe;
  margin-bottom: 10px;
}

.profile-breadcrumb {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #718096;
}

.breadcrumb-link {
  color: #4a5568;
  text-decoration: none;
  transition: color 0.2s;
}

.breadcrumb-link:hover {
  color: #2b6cb0;
  text-decoration: underline;
}

.breadcrumb-separator {
  margin: 0 8px;
  color: #cbd5e0;
}

.breadcrumb-current {
  color: #4299e1;
}

.loading-container,
.error-container,
.not-found {
  text-align: center;
  padding: 50px 20px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.loading-container i,
.error-container i,
.not-found i {
  font-size: 50px;
  color: #4299e1;
  margin-bottom: 20px;
}

.error-container i {
  color: #e53e3e;
}

.not-found i {
  color: #718096;
}

.loading-container p,
.error-container p,
.not-found p {
  font-size: 16px;
  color: #4a5568;
  margin-bottom: 20px;
}

.error-container h3,
.not-found h3 {
  font-size: 22px;
  color: #2d3748;
  margin-bottom: 10px;
}

.retry-btn,
.home-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #4299e1;
  color: white;
  border-radius: 6px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.2s;
  border: none;
  cursor: pointer;
}

.retry-btn:hover,
.home-link:hover {
  background: #2b6cb0;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(66, 153, 225, 0.3);
}

.profile-content {
  display: grid;
  grid-template-columns: 350px 1fr;
  gap: 25px;
}

@media (max-width: 992px) {
  .profile-content {
    grid-template-columns: 1fr;
  }
}

.profile-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.user-card {
  padding: 25px;
  position: relative;
  height: 450px;
}

.user-avatar-section {
  position: relative;
  width: 150px;
  height: 150px;
  margin: 0 auto 20px;
}

.user-avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 5px solid #f7fafc;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.avatar-edit-btn {
  position: absolute;
  bottom: 10px;
  right: 10px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #4299e1;
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.avatar-edit-btn:hover {
  background: #2b6cb0;
  transform: scale(1.1);
}

.user-info {
  text-align: center;
}

.user-meta {
  margin-bottom: 25px;
}

.username {
  font-size: 22px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 5px;
}

.user-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  background: #718096;
  color: white;
}

.user-badge.admin {
  background: #f56565;
}

.user-badge.customer {
  background: #48bb78;
}

.user-badge.staff {
  background: #ed8936;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  margin-bottom: 18px;
  text-align: left;
}

.detail-item i {
  font-size: 18px;
  color: #718096;
  margin-top: 2px;
}

.detail-item label {
  display: block;
  font-size: 12px;
  color: #718096;
  margin-bottom: 2px;
}

.detail-item p {
  font-size: 14px;
  font-weight: 500;
  color: #2d3748;
  margin: 0;
}

.edit-profile-btn {
  width: 100%;
  padding: 12px;
  background: #4299e1;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.edit-profile-btn:hover {
  background: #2b6cb0;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(66, 153, 225, 0.3);
}

.booking-history {
  padding: 25px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.section-header h3 {
  font-size: 20px;
  font-weight: 700;
  color: #2d3748;
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
}

.section-header h3 i {
  color: #4299e1;
}

.history-filter {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 6px 12px;
  background: #edf2f7;
  color: #4a5568;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-btn.active {
  background: #4299e1;
  color: white;
}

.filter-btn:hover:not(.active) {
  background: #e2e8f0;
}

.booking-list {
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 15px;
  height: 350px;
}

.booking-item {
  display: flex;
  gap: 20px;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  transition: all 0.2s;
}

.booking-item:hover {
  border-color: #cbd5e0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.booking-poster {
  width: 80px;
  height: 120px;
  flex-shrink: 0;
  border-radius: 6px;
  overflow: hidden;
  background: #f7fafc;
}

.booking-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.booking-info {
  flex-grow: 1;
  min-width: 0;
}

.movie-title {
  font-size: 16px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.booking-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 10px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #4a5568;
  white-space: nowrap;
}

.meta-item i {
  color: #718096;
}

.booking-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed #e2e8f0;
}

.booking-total {
  font-size: 14px;
  color: #4a5568;
  display: flex;
  align-items: center;
  gap: 5px;
}

.price {
  font-weight: 700;
  color: #2f855a;
}

.booking-status {
  font-size: 12px;
}

.status-active {
  color: #2f855a;
  font-weight: 600;
}

.status-cancelled {
  color: #e53e3e;
  font-weight: 600;
}

.booking-actions {
  margin-top: 10px;
  text-align: right;
}

.cancel-btn {
  padding: 6px 12px;
  background: #e53e3e;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.cancel-btn:hover {
  background: #c53030;
}

.empty-history {
  text-align: center;
  padding: 40px 20px;
  color: #718096;
}

.empty-history i {
  font-size: 40px;
  color: #cbd5e0;
  margin-bottom: 15px;
}

.empty-history p {
  font-size: 16px;
  margin-bottom: 20px;
}

.explore-btn {
  display: inline-block;
  padding: 10px 20px;
  background: #4299e1;
  color: white;
  border-radius: 6px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.2s;
}

.explore-btn:hover {
  background: #2b6cb0;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(66, 153, 225, 0.3);
}

@media (max-width: 576px) {
  .booking-item {
    flex-direction: column;
  }
  
  .booking-poster {
    width: 100%;
    height: 180px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .history-filter {
    width: 100%;
  }
  
  .filter-btn {
    flex-grow: 1;
    text-align: center;
  }
}
</style>