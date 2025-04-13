<template>
    <div>
      <h1>Đặt Vé</h1>
      <p>Chỉ người dùng đăng nhập mới có thể đặt vé.</p>
      <button @click="bookTicket">Đặt ngay</button>
      <button @click="logout">Đăng xuất</button>
    </div>
  </template>
  
  <script>
  export default {
    methods: {
      async bookTicket() {
        try {
          const response = await this.$axios.post('/api/tickets/book', {});
          alert('Đặt vé thành công!');
        } catch (error) {
          alert('Lỗi khi đặt vé');
        }
      },
      logout() {
        this.$store.dispatch('logout');
        this.$router.push('/login');
      }
    }
  };
  </script>