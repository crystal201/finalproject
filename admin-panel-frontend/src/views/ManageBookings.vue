<template>
  <div class="manage-bookings">
    <div class="page-header">
      <h2><i class="fas fa-ticket-alt"></i> Booking Management</h2>
      <div class="header-actions">
        <button @click="fetchBookings" class="refresh-btn">
          <i class="fas fa-sync-alt"></i> Refresh
        </button>
      </div>
    </div>
    
    <div class="card">
      <div class="table-responsive">
        <table class="bookings-table">
          <thead>
            <tr>
              <th>Movie</th>
              <th>Date & Time</th>
              <th>User</th>
              <th>Seats</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="booking in bookings" :key="booking.id">
              <td>
                <div class="movie-info">
                  <div class="movie-poster">
                    <i class="fas fa-film"></i>
                  </div>
                  <div class="movie-details">
                    <strong>{{ booking.movieTitle }}</strong>
                    <span>{{ booking.roomName }}</span>
                  </div>
                </div>
              </td>
              <td>
                <div class="datetime">
                  <div>{{ formatDate(booking.date) }}</div>
                  <div class="time">{{ booking.showtime }}</div>
                </div>
              </td>
              <td>
                <div class="user-info">
                  <div class="avatar">
                    <i class="fas fa-user"></i>
                  </div>
                  <div>
                    <div>{{ booking.username || 'Guest' }}</div>
                    <div class="user-id">ID: {{ booking.userId }}</div>
                  </div>
                </div>
              </td>
              <td>
                <div class="seats">
                  <span v-for="seat in booking.seats" :key="seat" class="seat-badge">{{ seat }}</span>
                </div>
              </td>
              <td>
                <span class="status-badge" :class="booking.status.toLowerCase()">
                  {{ formatStatus(booking.status) }}
                </span>
              </td>
              <td>
                <div class="action-buttons">
                  <template v-if="booking.status === 'WAITING_BOOKING'">
                    <button @click="acceptBooking(booking.id)" class="action-btn accept">
                      <i class="fas fa-check"></i>
                    </button>
                    <button @click="rejectBooking(booking.id)" class="action-btn reject">
                      <i class="fas fa-times"></i>
                    </button>
                  </template>
                  <template v-else-if="booking.status === 'WAITING_CANCEL'">
                    <button @click="acceptCancelBooking(booking.id)" class="action-btn accept">
                      <i class="fas fa-check"></i>
                    </button>
                    <button @click="rejectCancelBooking(booking.id)" class="action-btn reject">
                      <i class="fas fa-times"></i>
                    </button>
                  </template>
                  <button @click="viewBookingDetails(booking)" class="action-btn view">
                    <i class="fas fa-eye"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="bookings.length === 0" class="empty-state">
        <i class="fas fa-ticket-alt"></i>
        <p>No bookings found</p>
      </div>
      
      <div class="table-footer">
        <div class="table-info">
          Showing {{ bookings.length }} of {{ bookings.length }} bookings
        </div>
        <div class="pagination">
          <button class="pagination-btn" disabled>
            <i class="fas fa-chevron-left"></i>
          </button>
          <span class="current-page">1</span>
          <button class="pagination-btn" disabled>
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import dayjs from 'dayjs';

export default {
  data() {
    return {
      bookings: [],
      loading: false
    }
  },
  methods: {
    async fetchBookings() {
      this.loading = true;
      try {
        const response = await axios.get("/api/bookings");
        this.bookings = response.data;
      } catch (error) {
        console.error("API Error:", error);
        this.$toast.error('Failed to fetch bookings');
        this.bookings = [];
      } finally {
        this.loading = false;
      }
    },
    formatDate(date) {
      return dayjs(date).format('DD/MM/YYYY');
    },
    formatStatus(status) {
      return status.split('_').join(' ');
    },
    async acceptBooking(bookingId) {
      try {
        await axios.post(`/api/bookings/accept/${bookingId}`);
        this.$toast.success('Booking accepted successfully');
        await this.fetchBookings();
      } catch (error) {
        this.$toast.error('Error accepting booking: ' + error.message);
      } 
    },
    async rejectBooking(bookingId) {
      try {
        await axios.post(`/api/bookings/reject/${bookingId}`);
        this.$toast.success('Booking rejected successfully');
        await this.fetchBookings();
      } catch (error) {
        this.$toast.error('Error rejecting booking: ' + error.message);
      }
    },
    async acceptCancelBooking(bookingId) {
      try {
        await axios.post(`/api/bookings/accept-cancel/${bookingId}`);
        this.$toast.success('Cancellation accepted successfully');
        await this.fetchBookings();
      } catch (error) {
        this.$toast.error('Error accepting cancellation: ' + error.message);
      }
    },
    async rejectCancelBooking(bookingId) {
      try {
        await axios.post(`/api/bookings/reject-cancel/${bookingId}`);
        this.$toast.success('Cancellation rejected successfully');
        await this.fetchBookings();
      } catch (error) {
        this.$toast.error('Error rejecting cancellation: ' + error.message);
      }
    },
    viewBookingDetails(booking) {
      console.log("View booking details:", booking);
    }
  },
  mounted() {
    this.fetchBookings();
  }
}
</script>

<style scoped>
.manage-bookings {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 1.5rem;
  color: var(--light);
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-header h2 i {
  color: var(--primary);
}

.header-actions {
  display: flex;
  gap: 10px;
}

.refresh-btn {
  background: var(--card-bg);
  color: var(--light);
  border: none;
  padding: 8px 15px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.refresh-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.card {
  background: var(--card-bg);
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.table-responsive {
  overflow-x: auto;
}

.bookings-table {
  width: 100%;
  border-collapse: collapse;
}

.bookings-table th,
.bookings-table td {
  padding: 15px;
  text-align: left;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.bookings-table th {
  background: rgba(255, 255, 255, 0.03);
  color: var(--gray);
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.8rem;
  letter-spacing: 0.5px;
}

.movie-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.movie-poster {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.movie-details {
  display: flex;
  flex-direction: column;
}

.movie-details span {
  font-size: 0.8rem;
  color: var(--gray);
}

.datetime {
  display: flex;
  flex-direction: column;
}

.datetime .time {
  color: var(--gray);
  font-size: 0.85rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(99, 102, 241, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary);
}

.user-id {
  font-size: 0.75rem;
  color: var(--gray);
}

.seats {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.seat-badge {
  background: rgba(255, 255, 255, 0.05);
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
}

.status-badge {
  display: inline-block;
  padding: 6px 10px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.status-badge.confirmed {
  background: rgba(16, 185, 129, 0.1);
  color: var(--success);
}

.status-badge.waiting_booking,
.status-badge.waiting_cancel {
  background: rgba(234, 179, 8, 0.1);
  color: #eab308;
}

.status-badge.cancelled,
.status-badge.rejected {
  background: rgba(239, 68, 68, 0.1);
  color: var(--danger);
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn.accept {
  background: rgba(16, 185, 129, 0.1);
  color: var(--success);
}

.action-btn.accept:hover {
  background: rgba(16, 185, 129, 0.2);
}

.action-btn.reject {
  background: rgba(239, 68, 68, 0.1);
  color: var(--danger);
}

.action-btn.reject:hover {
  background: rgba(239, 68, 68, 0.2);
}

.action-btn.view {
  background: rgba(99, 102, 241, 0.1);
  color: var(--primary);
}

.action-btn.view:hover {
  background: rgba(99, 102, 241, 0.2);
}

.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: var(--gray);
}

.empty-state i {
  font-size: 2rem;
  margin-bottom: 10px;
  opacity: 0.5;
}

.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: rgba(255, 255, 255, 0.03);
}

.table-info {
  color: var(--gray);
  font-size: 0.85rem;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pagination-btn {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.05);
  border: none;
  color: var(--gray);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pagination-btn:not(:disabled):hover {
  background: rgba(255, 255, 255, 0.1);
  color: var(--light);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.current-page {
  min-width: 32px;
  text-align: center;
  font-weight: 600;
}

@media (max-width: 992px) {
  .bookings-table {
    min-width: 800px;
  }
}
</style>