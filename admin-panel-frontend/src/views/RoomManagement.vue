<template>
  <div>
    <h1>Room Management</h1>
    <ul>
      <li v-for="room in rooms" :key="room.id">
        {{ room.roomName }} (Capacity: {{ room.capacity }}, Status: {{ room.status }})
      </li>
    </ul>
    <button @click="openAddRoomModal">Add New Room</button>
    <button @click="fetchRooms">Refresh</button>

    <div v-if="showAddRoomModal" class="modal">
      <div class="modal-content">
        <h2>Add New Room</h2>
        <p>Room will be created as "Room X" with 50 seats and Available status.</p>
        <button @click="addRoom" :disabled="addingRoom">
          <span v-if="addingRoom">Adding...</span>
          <span v-else>Confirm Add</span>
        </button>
        <button @click="closeAddRoomModal">Cancel</button>
        <p v-if="addRoomMessage" :class="{ 'success': addRoomSuccess, 'error': !addRoomSuccess }">
          {{ addRoomMessage }}
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
    };
  },
  methods: {
    async fetchRooms() {
      try {
        console.log("Fetching rooms with baseURL:", this.$axios.defaults.baseURL);
        const response = await this.$axios.get('/api/rooms');
        console.log("Rooms data:", response.data);
        this.rooms = response.data;
      } catch (error) {
        console.error("API Error:", error.response ? error.response.data : error.message);
        this.rooms = [];
      }
    },
    openAddRoomModal() {
      this.showAddRoomModal = true;
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
        const response = await this.$axios.post('/api/rooms/add');
        this.addRoomSuccess = true;
        this.addRoomMessage = response.data.message;
        await this.fetchRooms();
      } catch (error) {
        this.addRoomSuccess = false;
        this.addRoomMessage = error.response && error.response.data ? error.response.data.message : 'Failed to add room';
        console.error("Error adding room:", error);
      } finally {
        this.addingRoom = false;
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
}
button {
  margin: 5px;
  padding: 10px 20px;
  cursor: pointer;
}
.success {
  color: green;
}
.error {
  color: red;
}
</style>