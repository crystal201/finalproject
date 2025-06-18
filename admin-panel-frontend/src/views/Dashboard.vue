<template>
  <div>
    <h1>Admin Dashboard</h1>
    <nav>
      <router-link to="/rooms">Manage Rooms</router-link> |
      <router-link to="/users">Manage Users</router-link> |
      <router-link to="/bookings">Manage Bookings</router-link> |
      <router-link to="/occupied-seats">Manage Occupied Seats</router-link>
    </nav>
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'Dashboard'
}
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
</style>