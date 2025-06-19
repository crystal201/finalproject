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
        const response = await this.axios.get('/rooms')
        console.log("Rooms data:", response.data)
        this.rooms = response.data
      } catch (error) {
        console.error("API Error:", {
          message: error.message,
          response: error.response ? error.response.data : 'No response',
          status: error.response ? error.response.status : 'No status',
          config: error.config ? error.config.url : 'No config'
        })
        this.rooms = []
      }
    }
  },
  mounted() {
    this.fetchRooms()
  }
}
</script>