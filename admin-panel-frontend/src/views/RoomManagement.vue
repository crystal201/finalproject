<template>
  <div class="room-management">
    <h2 class="section-title">Room Management</h2>
    <div class="rooms-list">
      <div v-for="room in rooms" :key="room.id" class="room-card">
        <div class="room-info">
          <p><strong>Room Name:</strong> {{ room.roomName }}</p>
          <p><strong>Capacity:</strong> {{ room.capacity }}</p>
          <p><strong>Status:</strong> {{ room.status }}</p>
        </div>
        <div class="room-actions">
          <button @click="confirmDeleteRoom(room.id)" class="action-btn delete">Delete</button>
          <button @click="showRoomBookings(room.id)" class="action-btn view">View Bookings</button>
        </div>
      </div>
    </div>
    <button @click="openAddRoomModal" class="add-btn">Add New Room</button>
    <button @click="fetchRooms" class="refresh-btn">Refresh</button>

    <!-- Modal for adding room -->
    <div v-if="showAddRoomModal" class="modal">
      <div class="modal-content">
        <h3>Add New Room</h3>
        <form @submit.prevent="addRoom">
          <div class="form-group">
            <label>Room Name:</label>
            <input v-model="newRoom.roomName" type="text" required placeholder="Enter room name" />
          </div>
          <div class="form-group">
            <label>Capacity:</label>
            <input v-model.number="newRoom.capacity" type="number" required placeholder="Enter capacity" min="1" />
          </div>
          <div class="form-group">
            <label>Status:</label>
            <select v-model="newRoom.status" required>
              <option value="Available">Available</option>
              <option value="Maintenance">Maintenance</option>
              <option value="Closed">Closed</option>
            </select>
          </div>
          <div class="modal-actions">
            <button type="submit" :disabled="addingRoom" class="confirm-btn">
              <span v-if="addingRoom">Adding...</span>
              <span v-else>Confirm</span>
            </button>
            <button type="button" @click="closeAddRoomModal" class="cancel-btn">Cancel</button>
          </div>
          <p v-if="addRoomMessage" :class="{ 'success': addRoomSuccess, 'error': !addRoomSuccess }">
            {{ addRoomMessage }}
          </p>
        </form>
      </div>
    </div>

    <!-- Confirm Delete Modal -->
    <div v-if="showDeleteConfirmModal" class="modal">
      <div class="modal-content">
        <h3>Confirm Delete</h3>
        <p>Are you sure you want to delete {{ roomToDelete ? roomToDelete.roomName : 'Unknown Room' }}?</p>
        <div class="modal-actions">
          <button @click="deleteRoom" :disabled="deletingRoom" class="confirm-btn">
            <span v-if="deletingRoom">Deleting...</span>
            <span v-else>Yes, Delete</span>
          </button>
          <button @click="closeDeleteConfirmModal" class="cancel-btn">Cancel</button>
        </div>
        <p v-if="deleteMessage" :class="{ 'success': deleteSuccess, 'error': !deleteSuccess }">
          {{ deleteMessage }}
        </p>
      </div>
    </div>

    <!-- Modal for viewing bookings -->
    <div v-if="showBookingsModal" class="modal">
      <div class="modal-content">
        <h3>Bookings for {{ selectedRoomName }}</h3>
        <div v-if="roomBookings.length > 0" class="booking-list">
          <div v-for="booking in roomBookings" :key="booking.id" class="booking-item">
            <p><strong>Date:</strong> {{ booking.date }}</p>
            <p><strong>Showtime:</strong> {{ booking.showtime }}</p>
            <p><strong>User:</strong> {{ booking.username }}</p>
            <p><strong>Seat:</strong> {{ booking.seat }}</p>
          </div>
        </div>
        <p v-else>No active bookings found for this room.</p>
        <button @click="closeBookingsModal" class="close-btn">Close</button>
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
      addRoomMessage: '',
      addRoomSuccess: false,
      newRoom: {
        roomName: '',
        capacity: null,
        status: 'Available'
      },
      showDeleteConfirmModal: false,
      deletingRoom: false,
      deleteMessage: '',
      deleteSuccess: false,
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
        console.log("Fetching rooms with baseURL:", axios.defaults.baseURL);
        const response = await axios.get('/api/rooms');
        console.log("Rooms data:", response.data);
        this.rooms = response.data;
      } catch (error) {
        console.error("API Error:", error.response ? error.response.data : error.message);
        this.rooms = [];
      }
    },
    openAddRoomModal() {
      this.showAddRoomModal = true;
      this.newRoom = { roomName: '', capacity: null, status: 'Available' };
      this.addRoomMessage = '';
    },
    closeAddRoomModal() {
      this.showAddRoomModal = false;
      this.addingRoom = false;
      this.addRoomMessage = '';
    },
    async addRoom() {
      this.addingRoom = true;
      this.addRoomMessage = '';
      try {
        const response = await axios.post('/api/rooms', this.newRoom);
        this.addRoomSuccess = true;
        this.addRoomMessage = response.data.message || 'Room added successfully';
        await this.fetchRooms();
      } catch (error) {
        this.addRoomSuccess = false;
        this.addRoomMessage = error.response && error.response.data ? error.response.data.message : 'Failed to add room';
        console.error("Error adding room:", error);
      } finally {
        this.addingRoom = false;
      }
    },
    confirmDeleteRoom(roomId) {
      this.roomToDelete = this.rooms.find(room => room.id === roomId);
      this.showDeleteConfirmModal = true;
      this.deleteMessage = '';
    },
    closeDeleteConfirmModal() {
      this.showDeleteConfirmModal = false;
      this.deletingRoom = false;
      this.deleteMessage = '';
      this.roomToDelete = null;
    },
    async deleteRoom() {
      if (!this.roomToDelete) return;
      this.deletingRoom = true;
      this.deleteMessage = '';
      try {
        const response = await axios.delete(`/api/rooms/${this.roomToDelete.id}`);
        this.deleteSuccess = true;
        this.deleteMessage = response.data.message || 'Room deleted successfully';
        await this.fetchRooms();
      } catch (error) {
        this.deleteSuccess = false;
        this.deleteMessage = error.response && error.response.data ? error.response.data.message : 'Failed to delete room';
        console.error("Error deleting room:", error);
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
  max-width: 100%;
  padding: 20px;
}

.section-title {
  color: #ffcc00;
  font-size: 24px;
  margin-bottom: 20px;
}

.rooms-list {
  display: grid;
  gap: 15px;
  max-width: 100%;

}

.room-card {
  background-color: #2c2c2c;
  padding: 15px;
  border-radius: 8px;
  border-left: 4px solid #4CAF50;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-info p {
  margin: 5px 0;
  color: #e0e0e0;
}

.room-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 8px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.action-btn.delete {
  background-color: #ff4444;
  color: white;
}

.action-btn.delete:hover {
  background-color: #cc0000;
}

.action-btn.view {
  background-color: #4CAF50;
  color: white;
}

.action-btn.view:hover {
  background-color: #45a049;
}

.add-btn, .refresh-btn {
  margin-top: 20px;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.add-btn {
  background-color: #4CAF50;
  color: white;
}

.add-btn:hover {
  background-color: #45a049;
}

.refresh-btn {
  background-color: #555;
  color: white;
  margin-left: 10px;
}

.refresh-btn:hover {
  background-color: #666;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: #2c2c2c;
  padding: 20px;
  border-radius: 8px;
  text-align: left;
  max-width: 400px;
  max-height: 80vh;
  overflow-y: auto;
  color: #e0e0e0;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
}

.form-group input, .form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #444;
  border-radius: 4px;
  background-color: #333;
  color: #e0e0e0;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.confirm-btn, .cancel-btn, .close-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.confirm-btn {
  background-color: #4CAF50;
  color: white;
}

.confirm-btn:hover {
  background-color: #45a049;
}

.cancel-btn, .close-btn {
  background-color: #555;
  color: white;
}

.cancel-btn:hover, .close-btn:hover {
  background-color: #666;
}

.success {
  color: #4CAF50;
  margin-top: 10px;
}

.error {
  color: #ff4444;
  margin-top: 10px;
}

.booking-list {
  margin-top: 15px;
}

.booking-item {
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #444;
  border-radius: 5px;
}

.booking-item p {
  margin: 5px 0;
}
</style>