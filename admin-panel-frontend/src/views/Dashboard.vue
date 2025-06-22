<template>
  <div class="min-h-screen bg-gray-50">
    <header class="bg-indigo-700 text-white shadow-md">
      <div class="container mx-auto px-4 py-4 flex justify-between items-center">
        <h1 class="text-2xl font-bold">MovieHub Admin</h1>
        <button 
          @click="logout" 
          class="flex items-center gap-2 bg-red-500 hover:bg-red-600 px-4 py-2 rounded-md transition-colors"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M3 3a1 1 0 00-1 1v12a1 1 0 102 0V4a1 1 0 00-1-1zm10.293 9.293a1 1 0 001.414 1.414l3-3a1 1 0 000-1.414l-3-3a1 1 0 10-1.414 1.414L14.586 9H7a1 1 0 100 2h7.586l-1.293 1.293z" clip-rule="evenodd" />
          </svg>
          Logout
        </button>
      </div>
    </header>

    <div class="container mx-auto px-4 py-8">
      <div class="flex flex-col md:flex-row gap-6">
        <!-- Sidebar -->
        <aside class="w-full md:w-64 bg-white rounded-lg shadow-md p-4 h-fit">
          <nav class="space-y-2">
            <router-link 
              to="/rooms" 
              class="flex items-center gap-3 px-4 py-3 rounded-md hover:bg-indigo-50 text-gray-700 hover:text-indigo-700 transition-colors"
              active-class="bg-indigo-100 text-indigo-700 font-medium"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path d="M5.5 16a3.5 3.5 0 01-.369-6.98 4 4 0 117.753-1.977A4.5 4.5 0 1113.5 16h-8z" />
              </svg>
              Manage Rooms
            </router-link>
            <router-link 
              to="/users" 
              class="flex items-center gap-3 px-4 py-3 rounded-md hover:bg-indigo-50 text-gray-700 hover:text-indigo-700 transition-colors"
              active-class="bg-indigo-100 text-indigo-700 font-medium"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z" />
              </svg>
              Manage Users
            </router-link>
            <router-link 
              to="/bookings" 
              class="flex items-center gap-3 px-4 py-3 rounded-md hover:bg-indigo-50 text-gray-700 hover:text-indigo-700 transition-colors"
              active-class="bg-indigo-100 text-indigo-700 font-medium"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M6 6V5a3 3 0 013-3h2a3 3 0 013 3v1h2a2 2 0 012 2v3.57A22.952 22.952 0 0110 13a22.95 22.95 0 01-8-1.43V8a2 2 0 012-2h2zm2-1a1 1 0 011-1h2a1 1 0 011 1v1H8V5zm1 5a1 1 0 011-1h.01a1 1 0 110 2H10a1 1 0 01-1-1z" clip-rule="evenodd" />
                <path d="M2 13.692V16a2 2 0 002 2h12a2 2 0 002-2v-2.308A24.974 24.974 0 0110 15c-2.796 0-5.487-.46-8-1.308z" />
              </svg>
              Manage Bookings
            </router-link>
            <router-link 
              to="/occupied-seats" 
              class="flex items-center gap-3 px-4 py-3 rounded-md hover:bg-indigo-50 text-gray-700 hover:text-indigo-700 transition-colors"
              active-class="bg-indigo-100 text-indigo-700 font-medium"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
                <path fill-rule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clip-rule="evenodd" />
              </svg>
              Occupied Seats
            </router-link>
          </nav>
        </aside>

        <!-- Main content -->
        <main class="flex-1">
          <div class="card">
            <router-view></router-view>
          </div>
        </main>
      </div>
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