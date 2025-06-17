<template>
  <div>
    <h1>Room Management</h1>
    <ul>
      <li v-for="room in rooms" :key="room.id">{{ room.roomName }} (Capacity: {{ room.capacity }})</li>
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
    fetchRooms() {
      this.axios.get('http://localhost:8081/admin/rooms')
        .then(response => {
          this.rooms = response.data;
        })
        .catch(error => console.log(error));
    }
  },
  mounted() {
    this.fetchRooms();
  }
}
</script>