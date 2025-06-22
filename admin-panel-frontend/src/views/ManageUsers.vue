<template>
  <div class="manage-users">
    <h2 class="section-title">Manage Users</h2>
    <div class="users-list">
      <div v-for="user in users" :key="user.id" class="user-card">
        <p><strong>Username:</strong> {{ user.username }}</p>
        <p><strong>Email:</strong> {{ user.email }}</p>
        <p><strong>Role:</strong> {{ user.role }}</p>
      </div>
    </div>
    <button @click="fetchUsers" class="refresh-btn">Refresh Users</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      users: []
    }
  },
  methods: {
    async fetchUsers() {
      try {
        console.log("Fetching users with baseURL:", this.axios.defaults.baseURL);
        const response = await this.axios.get('/api/users');
        console.log("Users data:", response.data);
        this.users = response.data;
      } catch (error) {
        console.error("API Error:", error.response ? error.response.data : error.message);
        this.users = [];
      }
    }
  },
  mounted() {
    this.fetchUsers();
  }
}
</script>

<style scoped>
.manage-users {
  padding: 20px;
    max-width: 100%;

}

.section-title {
  color: #ffcc00;
  font-size: 24px;
  margin-bottom: 20px;
}

.users-list {
  display: grid;
  gap: 15px;
  max-width: 100%;

}

.user-card {
  background-color: #2c2c2c;
  padding: 15px;
  border-radius: 8px;
  border-left: 4px solid #4CAF50;
}

.user-card p {
  margin: 5px 0;
  color: #e0e0e0;
}

.refresh-btn {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #555;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.refresh-btn:hover {
  background-color: #666;
}
</style>