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
export default {
  data() {
    return {
      rooms: []
    }
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
    }
  },
  mounted() {
    this.fetchRooms()
  }
}
</script>