<template>
    <div v-if="isOpen" class="modal-overlay">
      <div class="modal-content">
        <h2 class="modal-title">{{ title }}</h2>
        <p class="modal-message">{{ message }}</p>
        <button @click="redirectToLogin" class="modal-button">Đăng nhập ngay</button>
        <button @click="closeModal" class="modal-button secondary">Hủy</button>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      isOpen: {
        type: Boolean,
        default: false,
      },
      title: {
        type: String,
        default: 'Thông báo',
      },
      message: {
        type: String,
        default: 'Vui lòng đăng nhập để tiếp tục.',
      },
    },
    methods: {
      closeModal() {
        this.$emit('close');
      },
      redirectToLogin() {
        this.$router.push('/login');
      },
    },
  };
  </script>
  
  <style scoped>
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }
  
  .modal-content {
    background-color: white;
    padding: 20px;
    border-radius: 8px;
    width: 90%;
    max-width: 400px;
    text-align: center;
  }
  
  .modal-title {
    font-size: 1.5rem;
    font-weight: bold;
    margin-bottom: 10px;
    color: #333;
  }
  
  .modal-message {
    font-size: 1rem;
    margin-bottom: 20px;
    color: #666;
  }
  
  .modal-button {
    padding: 10px 20px;
    margin: 0 10px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 1rem;
  }
  
  .modal-button.primary {
    background-color: #3B82F6;
    color: white;
  }
  
  .modal-button.secondary {
    background-color: #E5E7EB;
    color: #333;
  }
  
  .modal-button:hover {
    opacity: 0.9;
  }
  </style>