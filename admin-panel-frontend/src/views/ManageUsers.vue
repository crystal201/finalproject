<template>
  <div>
    <h2>Manage Users</h2>
    <ul>
      <li v-for="user in users" :key="user.id">
        {{ user.username }} (Email: {{ user.email }}, Role: {{ user.role }})
      </li>
    </ul>
    <button @click="fetchUsers">Refresh Users</button>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      users: []
    }
  },
  methods: {
    async fetchUsers() {
      try {
        const response = await axios.get('/admin/users');
        this.users = response.data;
      } catch (error) {
        console.error("API Error:", error);
        this.users = [];
      }
    }
  },
  mounted() {
    this.fetchUsers();
  }
}
</script>