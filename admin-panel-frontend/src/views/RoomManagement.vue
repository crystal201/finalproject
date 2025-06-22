<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-gray-800">Room Management</h1>
      <div class="flex gap-3">
        <button 
          @click="openAddRoomModal" 
          class="button button-primary flex items-center gap-2"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
          </svg>
          Add Room
        </button>
        <button 
          @click="fetchRooms" 
          class="button button-secondary flex items-center gap-2"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M4 2a1 1 0 011 1v2.101a7.002 7.002 0 0111.601 2.566 1 1 0 11-1.885.666A5.002 5.002 0 005.999 7H9a1 1 0 010 2H4a1 1 0 01-1-1V3a1 1 0 011-1zm.008 9.057a1 1 0 011.276.61A5.002 5.002 0 0014.001 13H11a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0v-2.101a7.002 7.002 0 01-11.601-2.566 1 1 0 01.61-1.276z" clip-rule="evenodd" />
          </svg>
          Refresh
        </button>
      </div>
    </div>

    <!-- Rooms List -->
    <div v-if="rooms.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div 
        v-for="room in rooms" 
        :key="room.id" 
        class="bg-white rounded-lg shadow-md p-4 border-l-4"
        :class="{
          'border-green-500': room.status === 'Available',
          'border-yellow-500': room.status === 'Maintenance',
          'border-red-500': room.status === 'Closed'
        }"
      >
        <div class="flex justify-between items-start">
          <div>
            <h3 class="text-lg font-semibold text-gray-800">{{ room.roomName }}</h3>
            <p class="text-sm text-gray-600">Capacity: {{ room.capacity }} seats</p>
            <span 
              class="inline-block px-2 py-1 text-xs rounded-full mt-2"
              :class="{
                'bg-green-100 text-green-800': room.status === 'Available',
                'bg-yellow-100 text-yellow-800': room.status === 'Maintenance',
                'bg-red-100 text-red-800': room.status === 'Closed'
              }"
            >
              {{ room.status }}
            </span>
          </div>
          <div class="flex gap-2">
            <button 
              @click="showRoomBookings(room.id)" 
              class="p-2 text-blue-600 hover:bg-blue-50 rounded-full"
              title="View Bookings"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
                <path fill-rule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clip-rule="evenodd" />
              </svg>
            </button>
            <button 
              @click="confirmDeleteRoom(room.id)" 
              class="p-2 text-red-600 hover:bg-red-50 rounded-full"
              title="Delete Room"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="text-center py-12">
      <p class="text-gray-500">No rooms found. Add your first room to get started.</p>
    </div>

    <!-- Add Room Modal -->
    <div v-if="showAddRoomModal" class="modal-overlay">
      <div class="modal-content">
        <div class="p-6">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-800">Add New Room</h2>
            <button @click="closeAddRoomModal" class="text-gray-500 hover:text-gray-700">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          
          <form @submit.prevent="addRoom" class="space-y-4">
            <div>
              <label class="label">Room Name</label>
              <input 
                v-model="newRoom.roomName" 
                type="text" 
                required 
                placeholder="e.g. Theater 1" 
                class="input-field"
              />
            </div>
            
            <div>
              <label class="label">Capacity</label>
              <input 
                v-model.number="newRoom.capacity" 
                type="number" 
                required 
                placeholder="e.g. 120" 
                min="1" 
                class="input-field"
              />
            </div>
            
            <div>
              <label class="label">Status</label>
              <select 
                v-model="newRoom.status" 
                required 
                class="input-field"
              >
                <option value="Available">Available</option>
                <option value="Maintenance">Maintenance</option>
                <option value="Closed">Closed</option>
              </select>
            </div>
            
            <div class="flex justify-end gap-3 pt-4">
              <button 
                type="button" 
                @click="closeAddRoomModal" 
                class="button button-secondary"
              >
                Cancel
              </button>
              <button 
                type="submit" 
                :disabled="addingRoom" 
                class="button button-primary flex items-center gap-2"
              >
                <span v-if="addingRoom">
                  <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Adding...
                </span>
                <span v-else>Add Room</span>
              </button>
            </div>
            
            <div v-if="addRoomMessage" class="mt-4 p-3 rounded-md" :class="addRoomSuccess ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
              {{ addRoomMessage }}
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteConfirmModal" class="modal-overlay">
      <div class="modal-content">
        <div class="p-6">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-800">Confirm Deletion</h2>
            <button @click="closeDeleteConfirmModal" class="text-gray-500 hover:text-gray-700">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          
          <p class="text-gray-700 mb-6">Are you sure you want to delete <span class="font-semibold">{{ roomToDelete ? roomToDelete.roomName : 'this room' }}</span>? This action cannot be undone.</p>
          
          <div class="flex justify-end gap-3">
            <button 
              @click="closeDeleteConfirmModal" 
              class="button button-secondary"
            >
              Cancel
            </button>
            <button 
              @click="deleteRoom" 
              :disabled="deletingRoom" 
              class="button button-danger flex items-center gap-2"
            >
              <span v-if="deletingRoom">
                <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Deleting...
              </span>
              <span v-else>Delete Room</span>
            </button>
          </div>
          
          <div v-if="deleteMessage" class="mt-4 p-3 rounded-md" :class="deleteSuccess ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
            {{ deleteMessage }}
          </div>
        </div>
      </div>
    </div>

    <!-- Bookings Modal -->
    <div v-if="showBookingsModal" class="modal-overlay">
      <div class="modal-content">
        <div class="p-6">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-800">Bookings for {{ selectedRoomName }}</h2>
            <button @click="closeBookingsModal" class="text-gray-500 hover:text-gray-700">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          
          <div v-if="roomBookings.length > 0" class="space-y-4">
            <div 
              v-for="booking in roomBookings" 
              :key="booking.id" 
              class="border border-gray-200 rounded-lg p-4 hover:bg-gray-50"
            >
              <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <p class="text-sm text-gray-500">Date</p>
                  <p class="font-medium">{{ booking.date }}</p>
                </div>
                <div>
                  <p class="text-sm text-gray-500">Showtime</p>
                  <p class="font-medium">{{ booking.showtime }}</p>
                </div>
                <div>
                  <p class="text-sm text-gray-500">User</p>
                  <p class="font-medium">{{ booking.username || 'Guest' }}</p>
                </div>
              </div>
              <div class="mt-3">
                <p class="text-sm text-gray-500">Seats</p>
                <div class="flex flex-wrap gap-2 mt-1">
                  <span 
                    v-for="(seat, index) in booking.seat.split(',')" 
                    :key="index" 
                    class="px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded-full"
                  >
                    {{ seat.trim() }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="text-center py-8 text-gray-500">
            No active bookings found for this room.
          </div>
          
          <div class="flex justify-end mt-6">
            <button 
              @click="closeBookingsModal" 
              class="button button-secondary"
            >
              Close
            </button>
          </div>
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
        const response = await axios.get('/api/rooms');
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
        this.closeAddRoomModal();
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
        this.closeDeleteConfirmModal();
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