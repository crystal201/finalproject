<template>
  <div class="manage-seats">
    <div class="page-header">
      <h2><i class="fas fa-chair"></i> Occupied Seats Management</h2>
      <div class="header-actions">
        <button @click="fetchOccupiedSeats" class="refresh-btn">
          <i class="fas fa-sync-alt"></i> Refresh
        </button>
      </div>
    </div>
    
    <div class="card">
      <div class="search-filter">
        <div class="search-input">
          <i class="fas fa-search"></i>
          <input v-model="searchQuery" placeholder="Search seats...">
        </div>
        <div class="filter-group">
          <label>Filter by:</label>
          <select v-model="filterBy">
            <option value="all">All Seats</option>
            <option value="today">Today</option>
            <option value="upcoming">Upcoming</option>
          </select>
        </div>
      </div>
      
      <div class="seats-grid">
        <div v-for="seat in filteredSeats" :key="seat.id" class="seat-card">
          <div class="seat-header">
            <h3>Seat {{ seat.seat }}</h3>
            <span class="status-badge" :class="getStatusClass(seat)">{{ getStatusText(seat) }}</span>
          </div>
          <div class="seat-details">
            <div class="detail-item">
              <i class="fas fa-door-open"></i>
              <span>Room {{ seat.roomId }}</span>
            </div>
            <div class="detail-item">
              <i class="fas fa-calendar-day"></i>
              <span>{{ formatDate(seat.date) }}</span>
            </div>
            <div class="detail-item">
              <i class="fas fa-clock"></i>
              <span>{{ seat.showtime }}</span>
            </div>
          </div>
          <div class="seat-actions">
            <button class="action-btn view" @click="viewBookingDetails(seat)">
              <i class="fas fa-eye"></i> Details
            </button>
          </div>
        </div>
      </div>
      
      <div v-if="occupiedSeats.length === 0" class="empty-state">
        <i class="fas fa-chair"></i>
        <p>No occupied seats found</p>
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
      occupiedSeats: [],
      searchQuery: '',
      filterBy: 'all',
      loading: false
    }
  },
  computed: {
    filteredSeats() {
      let filtered = this.occupiedSeats;
      
      // Apply search filter
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase();
        filtered = filtered.filter(seat => 
          seat.seat.toLowerCase().includes(query) ||
          seat.roomId.toString().includes(query) ||
          seat.date.toLowerCase().includes(query) ||
          seat.showtime.toLowerCase().includes(query)
        );
      }
      
      // Apply time filter
      if (this.filterBy === 'today') {
        const today = dayjs().format('YYYY-MM-DD');
        filtered = filtered.filter(seat => seat.date === today);
      } else if (this.filterBy === 'upcoming') {
        const today = dayjs().format('YYYY-MM-DD');
        filtered = filtered.filter(seat => seat.date >= today);
      }
      
      return filtered;
    }
  },
  methods: {
    async fetchOccupiedSeats() {
      this.loading = true;
      try {
        const response = await this.axios.get('/api/bookings/occupied-seats');
        this.occupiedSeats = response.data;
      } catch (error) {
        console.error("API Error:", error);
        this.$toast.error('Failed to fetch occupied seats');
        this.occupiedSeats = [];
      } finally {
        this.loading = false;
      }
    },
    formatDate(date) {
      return dayjs(date).format('DD/MM/YYYY');
    },
    getStatusClass(seat) {
      const today = dayjs().format('YYYY-MM-DD');
      if (seat.date < today) return 'past';
      if (seat.date === today) return 'current';
      return 'upcoming';
    },
    getStatusText(seat) {
      const today = dayjs().format('YYYY-MM-DD');
      if (seat.date < today) return 'Past';
      if (seat.date === today) return 'Today';
      return 'Upcoming';
    },
    viewBookingDetails(seat) {
      // Implement view details functionality
      console.log("View seat details:", seat);
    }
  },
  mounted() {
    this.fetchOccupiedSeats();
  }
}
</script>

<style scoped>
.manage-seats {
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
  padding: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.search-filter {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  gap: 15px;
}

.search-input {
  flex: 1;
  position: relative;
}

.search-input i {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--gray);
}

.search-input input {
  width: 100%;
  padding: 10px 15px 10px 40px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  color: var(--light);
  transition: all 0.2s ease;
}

.search-input input:focus {
  border-color: var(--primary);
  outline: none;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-group label {
  color: var(--gray);
  font-size: 0.9rem;
}

.filter-group select {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  padding: 8px 12px;
  color: var(--light);
  min-width: 150px;
}

.seats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 15px;
}

.seat-card {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  padding: 15px;
  border-left: 4px solid var(--primary);
  transition: all 0.2s ease;
}

.seat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.seat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.seat-header h3 {
  font-size: 1.1rem;
  color: var(--light);
}

.status-badge {
  font-size: 0.75rem;
  padding: 4px 8px;
  border-radius: 12px;
  font-weight: 600;
}

.status-badge.past {
  background: rgba(239, 68, 68, 0.1);
  color: var(--danger);
}

.status-badge.current {
  background: rgba(234, 179, 8, 0.1);
  color: #eab308;
}

.status-badge.upcoming {
  background: rgba(16, 185, 129, 0.1);
  color: var(--success);
}

.seat-details {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 15px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--gray);
  font-size: 0.9rem;
}

.detail-item i {
  width: 16px;
  text-align: center;
}

.seat-actions {
  display: flex;
  justify-content: flex-end;
}

.action-btn {
  padding: 6px 12px;
  border-radius: 6px;
  border: none;
  font-size: 0.85rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s ease;
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

@media (max-width: 768px) {
  .search-filter {
    flex-direction: column;
  }
  
  .seats-grid {
    grid-template-columns: 1fr;
  }
}
</style>