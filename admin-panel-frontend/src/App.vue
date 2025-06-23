<template>
  <div id="app">
    <router-view />
  </div>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');

:root {
  --primary: #6366f1;
  --primary-hover: #4f46e5;
  --secondary: #f43f5e;
  --dark: #1e293b;
  --darker: #0f172a;
  --light: #f8fafc;
  --gray: #94a3b8;
  --success: #10b981;
  --warning: #f59e0b;
  --danger: #ef4444;
  --card-bg: #1e293b;
  --sidebar-bg: #0f172a;
  --header-bg: #0f172a;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: 'Inter', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--light);
  background-color: var(--darker);
  min-height: 100vh;
}

/* Scrollbar styling */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: var(--darker);
}

::-webkit-scrollbar-thumb {
  background: var(--primary);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--primary-hover);
}

/* Animation classes */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>