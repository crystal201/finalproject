<template>
  <div class="room-management">
    <div class="page-header">
      <h2><i class="fas fa-door-open"></i> Room Management</h2>
      <div class="header-actions">
        <button @click="openAddRoomModal" class="add-btn">
          <i class="fas fa-plus"></i> Add Room
        </button>
        <button @click="fetchRooms" class="refresh-btn">
          <i class="fas fa-sync-alt"></i> Refresh
        </button>
      </div>
    </div>
    
    <div class="card">
      <div class="table-responsive">
        <table class="rooms-table">
          <thead>
            <tr>
              <th>Room</th>
              <th>Capacity</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="room in rooms" :key="room.id">
              <td>
                <div class="room-info">
                  <div class="room-icon">
                    <i class="fas fa-door-closed"></i>
                  </div>
                  <div>
                    <strong>{{ room.roomName }}</strong>
                    <div class="room-id">ID: {{ room.id }}</div>
                  </div>
                </div>
              </td>
              <td>{{ room.capacity }} seats</td>
              <td>
                <span class="status-badge" :class="room.status.toLowerCase()">
                  {{ room.status }}
                </span>
              </td>
              <td>
                <div class="action-buttons">
                  <button @click="showRoomBookings(room.id)" class="action-btn view">
                    <i class="fas fa-calendar-alt"></i> Bookings
                  </button>
                  <button @click="confirmDeleteRoom(room.id)" class="action-btn delete">
                    <i class="fas fa-trash-alt"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="rooms.length === 0" class="empty-state">
        <i class="fas fa-door-open"></i>
        <p>No rooms found</p>
        <button @click="openAddRoomModal" class="add-btn">
          <i class="fas fa-plus"></i> Add Your First Room
        </button>
      </div>
      
      <div class="table-footer">
        <div class="table-info">
          Showing {{ rooms.length }} of {{ rooms.length }} rooms
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
    
    <!-- Add Room Modal -->
    <div v-if="showAddRoomModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h3>Add New Room</h3>
          <button class="close-modal" @click="closeAddRoomModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="addRoom">
            <div class="form-group">
              <label>Room Name</label>
              <input v-model="newRoom.roomName" type="text" required placeholder="Enter room name">
            </div>
            <div class="form-group">
              <label>Capacity</label>
              <input v-model.number="newRoom.capacity" type="number" required placeholder="Enter capacity" min="1">
            </div>
            <div class="form-group">
              <label>Status</label>
              <select v-model="newRoom.status" required>
                <option value="Available">Available</option>
                <option value="Maintenance">Maintenance</option>
                <option value="Closed">Closed</option>
              </select>
            </div>
            <div class="modal-footer">
              <button type="button" @click="closeAddRoomModal" class="cancel-btn">
                Cancel
              </button>
              <button type="submit" :disabled="addingRoom" class="confirm-btn">
                <span v-if="addingRoom" class="loading-spinner"></span>
                <span v-else>Add Room</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    
    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteConfirmModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h3>Confirm Deletion</h3>
          <button class="close-modal" @click="closeDeleteConfirmModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <p>Are you sure you want to delete <strong>{{ roomToDelete?.roomName }}</strong>?</p>
          <p>This action cannot be undone.</p>
        </div>
        <div class="modal-footer">
          <button @click="closeDeleteConfirmModal" class="cancel-btn">
            Cancel
          </button>
          <button @click="deleteRoom" :disabled="deletingRoom" class="confirm-btn danger">
            <span v-if="deletingRoom" class="loading-spinner"></span>
            <span v-else>Delete</span>
          </button>
        </div>
      </div>
    </div>
    
    <!-- Bookings Modal -->
    <div v-if="showBookingsModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h3>Bookings for {{ selectedRoomName }}</h3>
          <button class="close-modal" @click="closeBookingsModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="roomBookings.length > 0" class="bookings-list">
            <div v-for="booking in roomBookings" :key="booking.id" class="booking-item">
              <div class="booking-header">
                <div class="booking-date">
                  <i class="fas fa-calendar-day"></i>
                  {{ booking.date }}
                </div>
                <div class="booking-time">
                  <i class="fas fa-clock"></i>
                  {{ booking.showtime }}
                </div>
              </div>
              <div class="booking-details">
                <div class="booking-user">
                  <i class="fas fa-user"></i>
                  {{ booking.username || 'Guest' }}
                </div>
                <div class="booking-seat">
                  <i class="fas fa-chair"></i>
                  {{ booking.seat }}
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <i class="fas fa-calendar-times"></i>
            <p>No bookings found for this room</p>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeBookingsModal" class="cancel-btn">
            Close
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
      rooms: [],
      showAddRoomModal: false,
      addingRoom: false,
      newRoom: {
        roomName: '',
        capacity: null,
        status: 'Available'
      },
      showDeleteConfirmModal: false,
      deletingRoom: false,
      roomToDelete: null,
      showBookingsModal: false,
      roomBookings: [],
      selectedRoomName: '',
      selectedRoomId: null,
    };
  },
  methods: {
    async fetchRooms() {
      try {
        const response = await axios.get('/api/rooms');
        this.rooms = response.data;
      } catch (error) {
        console.error("API Error:", error);
        this.$toast.error('Failed to fetch rooms');
        this.rooms = [];
      }
    },
    openAddRoomModal() {
      this.showAddRoomModal = true;
      this.newRoom = { roomName: '', capacity: null, status: 'Available' };
    },
    closeAddRoomModal() {
      this.showAddRoomModal = false;
      this.addingRoom = false;
    },
    async addRoom() {
      this.addingRoom = true;
      try {
        await axios.post('/api/rooms', this.newRoom);
        this.$toast.success('Room added successfully');
        await this.fetchRooms();
        this.closeAddRoomModal();
      } catch (error) {
  let errorMessage = 'Failed to add room';
  if (error.response && error.response.data && error.response.data.message) {
    errorMessage += ': ' + error.response.data.message;
  } else {
    errorMessage += ': ' + error.message;
  }
  this.$toast.error(errorMessage);
}finally {
        this.addingRoom = false;
      }
    },
    confirmDeleteRoom(roomId) {
      this.roomToDelete = this.rooms.find(room => room.id === roomId);
      this.showDeleteConfirmModal = true;
    },
    closeDeleteConfirmModal() {
      this.showDeleteConfirmModal = false;
      this.deletingRoom = false;
      this.roomToDelete = null;
    },
    async deleteRoom() {
      if (!this.roomToDelete) return;
      this.deletingRoom = true;
      try {
        await axios.delete(`/api/rooms/${this.roomToDelete.id}`);
        this.$toast.success('Room deleted successfully');
        await this.fetchRooms();
        this.closeDeleteConfirmModal();
      } catch (error) {
        this.$toast.error('Failed to delete room: ' + (error.response?.data?.message || error.message));
      } finally {
        this.deletingRoom = false;
      }
    },
    async showRoomBookings(roomId) {
      this.selectedRoomId = roomId;
      const room = this.rooms.find(r => r.id === roomId);
      this.selectedRoomName = room ? room.roomName : 'Unknown Room';
      try {
        const response = await axios.get('/api/bookings/occupied-seats', {
          params: { roomId: roomId }
        });
        this.roomBookings = response.data.map(booking => ({
          id: booking.roomId + '-' + booking.date + '-' + booking.showtime,
          date: booking.date,
          showtime: booking.showtime,
          username: booking.username,
          seat: booking.seat
        }));
        this.showBookingsModal = true;
      } catch (error) {
        console.error("Error fetching bookings:", error);
        this.roomBookings = [];
        this.showBookingsModal = true;
      }
    },
    closeBookingsModal() {
      this.showBookingsModal = false;
      this.roomBookings = [];
      this.selectedRoomId = null;
      this.selectedRoomName = '';
    },
  },
  mounted() {
    this.fetchRooms();
  },
};
</script>

<style scoped>
.room-management {
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

.add-btn {
  background: var(--primary);
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.add-btn:hover {
  background: var(--primary-hover);
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

.rooms-table {
  width: 100%;
  border-collapse: collapse;
}

.rooms-table th,
.rooms-table td {
  padding: 15px;
  text-align: left;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.rooms-table th {
  background: rgba(255, 255, 255, 0.03);
  color: var(--gray);
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.8rem;
  letter-spacing: 0.5px;
}

.room-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.room-icon {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  background: rgba(99, 102, 241, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary);
}

.room-id {
  font-size: 0.75rem;
  color: var(--gray);
}

.status-badge {
  display: inline-block;
  padding: 6px 10px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.status-badge.available {
  background: rgba(16, 185, 129, 0.1);
  color: var(--success);
}

.status-badge.maintenance {
  background: rgba(234, 179, 8, 0.1);
  color: #eab308;
}

.status-badge.closed {
  background: rgba(239, 68, 68, 0.1);
  color: var(--danger);
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 8px 12px;
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

.action-btn.delete {
  background: rgba(239, 68, 68, 0.1);
  color: var(--danger);
}

.action-btn.delete:hover {
  background: rgba(239, 68, 68, 0.2);
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

.empty-state .add-btn {
  margin-top: 15px;
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

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal {
  background: var(--card-bg);
  border-radius: 10px;
  width: 100%;
  max-width: 500px;
  overflow: hidden;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.modal-header {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-header h3 {
  font-size: 1.2rem;
  color: var(--light);
}

.close-modal {
  background: none;
  border: none;
  color: var(--gray);
  font-size: 1.2rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.close-modal:hover {
  color: var(--light);
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: var(--light);
  font-size: 0.9rem;
  font-weight: 500;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  color: var(--light);
  transition: all 0.2s ease;
}

.form-group input:focus,
.form-group select:focus {
  border-color: var(--primary);
  outline: none;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2);
}

.modal-footer {
  padding: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.cancel-btn {
  background: rgba(255, 255, 255, 0.05);
  color: var(--light);
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.confirm-btn {
  background: var(--primary);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.confirm-btn:hover {
  background: var(--primary-hover);
}

.confirm-btn.danger {
  background: var(--danger);
}

.confirm-btn.danger:hover {
  background: #dc2626;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.bookings-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 400px;
  overflow-y: auto;
}

.booking-item {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  padding: 12px;
}

.booking-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.booking-date,
.booking-time {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--light);
}

.booking-details {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  color: var(--gray);
}

.booking-user,
.booking-seat {
  display: flex;
  align-items: center;
  gap: 6px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .action-btn.view span {
    display: none;
  }
  
  .modal {
    max-width: 100%;
  }
}
</style>