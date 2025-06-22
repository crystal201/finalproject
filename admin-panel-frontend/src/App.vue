<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

#app {
  font-family: 'Inter', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* Global styles */
.button {
  @apply px-4 py-2 rounded-md font-medium transition-all duration-200;
}
.button-primary {
  @apply bg-blue-600 text-white hover:bg-blue-700;
}
.button-danger {
  @apply bg-red-600 text-white hover:bg-red-700;
}
.button-success {
  @apply bg-green-600 text-white hover:bg-green-700;
}
.button-secondary {
  @apply bg-gray-200 text-gray-800 hover:bg-gray-300;
}

.card {
  @apply bg-white rounded-lg shadow-md p-6;
}

.modal-overlay {
  @apply fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50;
}

.modal-content {
  @apply bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto;
}

.input-field {
  @apply w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500;
}

.label {
  @apply block text-sm font-medium text-gray-700 mb-1;
}
</style>