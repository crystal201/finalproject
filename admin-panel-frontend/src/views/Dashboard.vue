<template>
  <div>
    <h1>Admin Dashboard</h1>
    <nav>
      <router-link to="/rooms">Manage Rooms</router-link> |
      <router-link to="/users">Manage Users</router-link> |
      <router-link to="/bookings">Manage Bookings</router-link> |
      <router-link to="/occupied-seats">Manage Occupied Seats</router-link>
    </nav>
    <button @click="logout" class="logout-btn">Logout</button>
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'Dashboard',
  methods: {
    logout() {
      localStorage.removeItem('authenticated');
      this.$router.push('/login');
    }
  }
};
</script>

<style scoped>
nav {
  margin: 20px 0;
}
router-link {
  margin: 0 10px;
  text-decoration: none;
  color: #42b983;
}
router-link:hover {
  text-decoration: underline;
}
.logout-btn {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.logout-btn:hover {
  background-color: #da190b;
}
</style>