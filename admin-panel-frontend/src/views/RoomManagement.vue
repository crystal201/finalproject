<template>
  <div>
    <h1>Room Management</h1>
    <ul>
      <li v-for="room in rooms" :key="room.id">
        {{ room.roomName }} (Status: {{ room.status }})
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
        const response = await axios.get('/rooms')
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