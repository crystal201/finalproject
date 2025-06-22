<template>
  <div class="dashboard-container">
    <header class="dashboard-header">
      <h1 class="dashboard-title">Admin Dashboard</h1>
      <button @click="logout" class="logout-btn">Logout</button>
    </header>
    <div class="dashboard-content">
      <aside class="sidebar">
        <nav class="sidebar-nav">
          <router-link to="/rooms" class="nav-item">Manage Rooms</router-link>
          <router-link to="/users" class="nav-item">Manage Users</router-link>
          <router-link to="/bookings" class="nav-item">Manage Bookings</router-link>
          <router-link to="/occupied-seats" class="nav-item">Manage Seats</router-link>
        </nav>
      </aside>
      <main class="main-content">
        <router-view />
      </main>
    </div>
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
.dashboard-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.dashboard-header {
  background-color: #2c2c2c;
  padding: 15px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #444;
}

.dashboard-title {
  color: #ffcc00;
  font-size: 24px;
  margin: 0;
  font-weight: 600;
}

.logout-btn {
  padding: 10px 20px;
  background-color: #ff4444;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.logout-btn:hover {
  background-color: #cc0000;
}

.dashboard-content {
  display: flex;
  flex: 1;
}

.sidebar {
  width: 250px;
  background-color: #333;
  padding-top: 20px;
}

.sidebar-nav {
  list-style: none;
  padding: 0;
}

.nav-item {
  display: block;
  padding: 15px 20px;
  color: #e0e0e0;
  text-decoration: none;
  font-size: 16px;
  transition: background-color 0.3s;
}

.nav-item:hover,
.nav-item.router-link-active {
  background-color: #444;
  color: #ffcc00;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #1a1a1a;
  overflow-y: auto;
}
</style>