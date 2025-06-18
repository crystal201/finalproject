<template>
  <div>
    <h1>Room Management</h1>
    <ul>
      <li v-for="room in rooms" :key="room.id">
        {{ room.roomName }}
      </li>
    </ul>
    <button @click="fetchRooms">Refresh</button>
  </div>
</template>

<script>
import axios from 'axios';
export default {
  data() {
    return {
      rooms: []
    }
  },
  methods: {
    async fetchRooms() {
      try {
        const response = await axios.get('/admin/rooms')
        this.rooms = response.data
      } catch (error) {
        console.error("API Error:", error)
        this.rooms = []
      }
    }
  },
  mounted() {
    this.fetchRooms()
  }
}
</script>