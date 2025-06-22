<template>
  <div>
    <h1>Room Management</h1>
    <ul>
      <li v-for="room in rooms" :key="room.id" class="room-item">
        {{ room.roomName }} (Capacity: {{ room.capacity }}, Status: {{ room.status }})
        <button @click="confirmDeleteRoom(room.id)" class="delete-btn">Delete</button>
        <button @click="showRoomBookings(room.id)" class="view-btn">View Bookings</button>
      </li>
    </ul>
    <button @click="openAddRoomModal">Add New Room</button>
    <button @click="fetchRooms">Refresh</button>

    <!-- Modal for adding room -->
    <div v-if="showAddRoomModal" class="modal">
      <div class="modal-content">
        <h2>Add New Room</h2>
        <form @submit.prevent="addRoom">
          <div>
            <label>Room Name:</label>
            <input v-model="newRoom.roomName" type="text" required placeholder="Enter room name" />
          </div>
          <div>
            <label>Capacity:</label>
            <input v-model.number="newRoom.capacity" type="number" required placeholder="Enter capacity" min="1" />
          </div>
          <div>
            <label>Status:</label>
            <select v-model="newRoom.status" required>
              <option value="Available">Available</option>
              <option value="Maintenance">Maintenance</option>
              <option value="Closed">Closed</option>
            </select>
          </div>
          <button type="submit" :disabled="addingRoom">
            <span v-if="addingRoom">Adding...</span>
            <span v-else>Confirm Add</span>
          </button>
          <button type="button" @click="closeAddRoomModal">Cancel</button>
        </form>
        <p v-if="addRoomMessage" :class="{ 'success': addRoomSuccess, 'error': !addRoomSuccess }">
          {{ addRoomMessage }}
        </p>
      </div>
    </div>

    <!-- Confirm Delete Modal -->
    <div v-if="showDeleteConfirmModal" class="modal">
      <div class="modal-content">
        <h2>Confirm Delete</h2>
        <p>Are you sure you want to delete {{ roomToDelete ? roomToDelete.roomName : 'Unknown Room' }}?</p>
        <button @click="deleteRoom" :disabled="deletingRoom">
          <span v-if="deletingRoom">Deleting...</span>
          <span v-else>Yes, Delete</span>
        </button>
        <button @click="closeDeleteConfirmModal">Cancel</button>
        <p v-if="deleteMessage" :class="{ 'success': deleteSuccess, 'error': !deleteSuccess }">
          {{ deleteMessage }}
        </p>
      </div>
    </div>

    <!-- Modal for viewing bookings -->
    <div v-if="showBookingsModal" class="modal">
      <div class="modal-content">
        <h2>Bookings for {{ selectedRoomName }}</h2>
        <ul v-if="roomBookings.length > 0" class="booking-list">
          <li v-for="booking in roomBookings" :key="booking.id" class="booking-item">
            <strong>Date:</strong> {{ booking.date }}<br>
            <strong>Showtime:</strong> {{ booking.showtime }}<br>
            <strong>User:</strong> {{ booking.username }}<br>
            <strong>Seats:</strong> {{ booking.seat }}
          </li>
        </ul>
        <p v-else>No active bookings found for this room.</p>
        <button @click="closeBookingsModal">Close</button>
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
          id: booking.roomId + '-' + booking.date + '-' + booking.showtime, // Unique key
          date: booking.date,
          showtime: booking.showtime,
          username: booking.username,
          seat: booking.seat
        }));
        this.showBookingsModal = true;
      } catch (error) {
        console.error("Error fetching bookings:", error);
        this.roomBookings = [];
        this.showBookingsModal = true; // Hiển thị modal với thông báo lỗi
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
  background: white;
  padding: 20px;
  border-radius: 5px;
  text-align: center;
  min-width: 300px;
  max-height: 70vh;
  overflow-y: auto;
}
form {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
label {
  margin-right: 10px;
}
input, select {
  padding: 5px;
  margin-bottom: 10px;
}
button {
  margin: 5px;
  padding: 10px 20px;
  cursor: pointer;
}
.delete-btn {
  background-color: #ff4444;
  color: white;
  border: none;
  padding: 5px 10px;
  cursor: pointer;
}
.delete-btn:hover {
  background-color: #cc0000;
}
.view-btn {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 5px 10px;
  cursor: pointer;
  margin-left: 10px;
}
.view-btn:hover {
  background-color: #45a049;
}
.success {
  color: green;
}
.error {
  color: red;
}
.room-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 0;
}
.booking-list {
  list-style: none;
  padding: 0;
  text-align: left;
}
.booking-item {
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}
</style>