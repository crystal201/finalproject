<template>
  <div class="dashboard">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <h1 class="logo">
          <i class="fas fa-film"></i>
          <span>CineAdmin</span>
        </h1>
      </div>
      
      <nav class="sidebar-nav">
        <router-link to="/dashboard/rooms" class="nav-item">
          <i class="fas fa-door-open"></i>
          <span>Rooms</span>
        </router-link>
        <router-link to="/dashboard/users" class="nav-item">
          <i class="fas fa-users"></i>
          <span>Users</span>
        </router-link>
        <router-link to="/dashboard/bookings" class="nav-item">
          <i class="fas fa-ticket-alt"></i>
          <span>Bookings</span>
        </router-link>
        <router-link to="/dashboard/occupied-seats" class="nav-item">
          <i class="fas fa-chair"></i>
          <span>Seats</span>
        </router-link>
      </nav>
      
      <div class="sidebar-footer">
        <button @click="logout" class="logout-btn">
          <i class="fas fa-sign-out-alt"></i>
          <span>Logout</span>
        </button>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="main-content">
      <!-- Top Header -->
      <header class="header">
        <div class="header-left">
          <button class="menu-toggle" @click="toggleSidebar">
            <i class="fas fa-bars"></i>
          </button>
          <h2 class="page-title">{{ currentRouteName }}</h2>
        </div>
        <div class="header-right">
          <div class="user-profile">
            <i class="fas fa-user-circle"></i>
            <span>Admin</span>
          </div>
        </div>
      </header>

      <!-- Content Area -->
      <div class="content-wrapper">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Dashboard',
  computed: {
    currentRouteName() {
      return this.$route.name || 'Dashboard';
    }
  },
  methods: {
    logout() {
      localStorage.removeItem('authenticated');
      this.$router.push('/login');
    },
    toggleSidebar() {
      document.querySelector('.sidebar').classList.toggle('collapsed');
      document.querySelector('.main-content').classList.toggle('expanded');
    }
  }
};
</script>

<style scoped>
.dashboard {
  display: flex;
  min-height: 100vh;
}

/* Sidebar Styles */
.sidebar {
  width: 250px;
  background-color: var(--sidebar-bg);
  color: white;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  position: relative;
  z-index: 10;
}

.sidebar.collapsed {
  width: 80px;
}

.sidebar.collapsed .sidebar-header span,
.sidebar.collapsed .nav-item span,
.sidebar.collapsed .logout-btn span {
  display: none;
}

.sidebar.collapsed .sidebar-header,
.sidebar.collapsed .nav-item,
.sidebar.collapsed .logout-btn {
  justify-content: center;
}

.sidebar-header {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  font-size: 1.5rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--light);
}

.logo i {
  font-size: 1.8rem;
  color: var(--primary);
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.nav-item {
  padding: 12px 20px;
  color: var(--gray);
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.2s ease;
  margin: 0 10px;
  border-radius: 8px;
}

.nav-item i {
  font-size: 1.1rem;
  width: 24px;
  text-align: center;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.05);
  color: var(--light);
}

.nav-item.router-link-active {
  background: var(--primary);
  color: white;
  font-weight: 500;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.logout-btn {
  background: rgba(239, 68, 68, 0.1);
  color: var(--danger);
  border: none;
  padding: 10px 15px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  transition: all 0.2s ease;
}

.logout-btn:hover {
  background: rgba(239, 68, 68, 0.2);
}

/* Main Content Styles */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
}

.main-content.expanded {
  margin-left: -170px;
}

.header {
  background-color: var(--header-bg);
  padding: 15px 25px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.menu-toggle {
  background: none;
  border: none;
  color: var(--gray);
  font-size: 1.2rem;
  cursor: pointer;
  display: none;
}

.menu-toggle:hover {
  color: var(--light);
}

.page-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: var(--light);
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--light);
}

.user-profile i {
  font-size: 1.5rem;
}

.content-wrapper {
  flex: 1;
  padding: 25px;
  overflow-y: auto;
}

/* Responsive Design */
@media (max-width: 992px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 100;
    transform: translateX(-100%);
  }
  
  .sidebar.collapsed {
    transform: translateX(0);
    width: 70px;
  }
  
  .sidebar:not(.collapsed) {
    transform: translateX(0);
  }
  
  .main-content {
    margin-left: 0;
  }
  
  .menu-toggle {
    display: block;
  }
}
</style>