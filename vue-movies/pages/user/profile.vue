<template>
  <div class="profile-container">
    <!-- Header Section -->
    <div class="profile-header">
      <h1 class="profile-title">Hồ sơ cá nhân</h1>
      <div class="profile-breadcrumb">
        <nuxt-link to="/" class="breadcrumb-link">Trang chủ</nuxt-link>
        <span class="breadcrumb-separator">/</span>
        <span class="breadcrumb-current">Hồ sơ</span>
      </div>
    </div>

    <!-- Main Content -->
    <div v-if="loading" class="loading-container">
      <i class="fas fa-spinner fa-spin"></i>
      <p>Đang tải thông tin...</p>
    </div>

    <div v-else-if="error" class="error-container">
      <i class="fas fa-exclamation-triangle"></i>
      <h3>Có lỗi xảy ra</h3>
      <p>{{ error }}</p>
      <button @click="retryLoading" class="retry-btn">
        <i class="fas fa-sync-alt"></i> Thử lại
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
            <h2 class="username">{{ user.username || 'Khách' }}</h2>
            <span v-if="user.role" class="user-badge" :class="getRoleClass(user.role)">
              {{ user.role }}
            </span>
          </div>
          
          <div class="user-details">
            <div class="detail-item">
              <i class="fas fa-envelope"></i>
              <div>
                <label>Email</label>
                <p>{{ user.email || 'Chưa cập nhật' }}</p>
              </div>
            </div>
            
            <div class="detail-item">
              <i class="fas fa-phone"></i>
              <div>
                <label>Số điện thoại</label>
                <p>{{ user.phone || 'Chưa cập nhật' }}</p>
              </div>
            </div>
            
            <div class="detail-item">
              <i class="fas fa-calendar-alt"></i>
              <div>
                <label>Thành viên từ</label>
                <p>{{ formatJoinDate(user.createdAt) }}</p>
              </div>
            </div>
          </div>
        </div>
        
        <button class="edit-profile-btn" @click="editProfile">
          <i class="fas fa-edit"></i> Chỉnh sửa hồ sơ
        </button>
      </div>

      <!-- Booking History Section -->
      <div class="profile-card booking-history">
        <div class="section-header">
          <h3><i class="fas fa-ticket-alt"></i> Lịch sử đặt vé</h3>
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
            @click="viewBookingDetail(booking.id)"
          >
            <div class="booking-poster">
              <img 
                :src="getMoviePoster(booking.moviePoster)" 
                :alt="booking.movieTitle"
                @error="handlePosterError"
              />
            </div>
            
            <div class="booking-info">
              <h4 class="movie-title">{{ booking.movieTitle || 'Phim không xác định' }}</h4>
              <div class="booking-meta">
                <div class="meta-item">
                  <i class="fas fa-calendar-day"></i>
                  <span>{{ booking.date || '--' }}</span>
                </div>
                <div class="meta-item">
                  <i class="fas fa-clock"></i>
                  <span>{{ booking.showtime || '--' }}</span>
                </div>
                <div class="meta-item">
                  <i class="fas fa-chair"></i>
                  <span>{{ formatSeats(booking.seats) }}</span>
                </div>
              </div>
              <div class="booking-footer">
                <div class="booking-total">
                  <span>Tổng cộng:</span>
                  <span class="price">{{ formatCurrency(booking.total) }}</span>
                </div>
                <div class="booking-date">
                  <i class="fas fa-clock"></i>
                  <span>{{ formatDateTime(booking.createdAt) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-history">
          <i class="fas fa-ticket-alt"></i>
          <p>{{ activeFilter === 'all' ? 'Bạn chưa có vé nào' : 'Không tìm thấy vé phù hợp' }}</p>
          <nuxt-link to="/movies" class="explore-btn">Khám phá phim mới</nuxt-link>
        </div>
      </div>
    </div>

    <div v-else class="not-found">
      <i class="fas fa-user-slash"></i>
      <h3>Không tìm thấy người dùng</h3>
      <p>Tài khoản không tồn tại hoặc đã bị xóa</p>
      <nuxt-link to="/" class="home-link">
        <i class="fas fa-home"></i> Về trang chủ
      </nuxt-link>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
  middleware: 'auth',
  data() {
    return {
      loading: true,
      error: null,
      bookings: [],
      filters: [
        { value: 'all', label: 'Tất cả' },
        { value: 'upcoming', label: 'Sắp chiếu' },
        { value: 'watched', label: 'Đã xem' }
      ],
      activeFilter: 'all',
      defaultAvatar: 'https://ui-avatars.com/api/?name=User&background=0D8ABC&color=fff&bold=true&size=128',
      defaultPoster: '/images/default-poster.jpg'
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
      
      const now = new Date();
      return this.bookings.filter(booking => {
        if (this.activeFilter === 'upcoming') {
          const showDate = new Date(booking.date);
          return showDate > now;
        }
        if (this.activeFilter === 'watched') {
          const showDate = new Date(booking.date);
          return showDate <= now;
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
        
        // Load bookings if user exists
        if (this.user) {
          const response = await this.$axios.get('/api/bookings');
          this.bookings = response.data.map(booking => ({
            ...booking,
            moviePoster: booking.moviePoster || null
          }));
        }
      } catch (err) {
        console.error('Lỗi khi tải dữ liệu:', err);
        this.error = err.response?.data?.message || 'Không thể tải thông tin. Vui lòng thử lại sau.';
      } finally {
        this.loading = false;
      }
    },
    
    retryLoading() {
      this.loadData();
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
          dateStyle: 'medium',
          timeStyle: 'short',
        });
      } catch {
        return 'N/A';
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
      if (!amount) return '--';
      try {
        return new Intl.NumberFormat('vi-VN', {
          style: 'currency',
          currency: 'VND'
        }).format(amount * 1000);
      } catch {
        return amount + ' VND';
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
    },
    
    editProfile() {
      this.$router.push('/profile/edit');
    },
    
    openAvatarEditor() {
      // TODO: Implement avatar editor
      this.$toast.info('Tính năng đang phát triển');
    },
    
    viewBookingDetail(bookingId) {
      this.$router.push(`/bookings/${bookingId}`);
    }
  }
};
</script>

<style scoped>
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
  color: #2d3748;
  margin-bottom: 10px;
}

.profile-breadcrumb {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #718096;
}

.breadcrumb-link {
  color: #4299e1;
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
  color: #4a5568;
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
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.booking-item {
  display: flex;
  gap: 20px;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  transition: all 0.2s;
  cursor: pointer;
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

.poster-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e2e8f0;
  color: #a0aec0;
  font-size: 24px;
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

.booking-date {
  font-size: 12px;
  color: #718096;
  display: flex;
  align-items: center;
  gap: 5px;
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