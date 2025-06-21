<template>
  <div>
    <h1>Room Management</h1>
    <ul>
      <li v-for="room in rooms" :key="room.id" class="room-item">
        {{ room.roomName }} (Capacity: {{ room.capacity }}, Status: {{ room.status }})
        <button @click="confirmDeleteRoom(room.id)" class="delete-btn">Delete</button>
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
  </div>
</template>

<script>
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
</style>