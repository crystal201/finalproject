<template>
    <div class="auth-page">
      <form @submit.prevent="handleRegister">
        <h2>Đăng ký</h2>
        <div class="form-group">
          <label>Tài khoản</label>
          <input v-model="form.username" required>
        </div>
        <div class="form-group">
          <label>Email</label>
          <input v-model="form.email" type="email" required>
        </div>
        <div class="form-group">
          <label>Mật khẩu</label>
          <input v-model="form.password" type="password" required>
        </div>
        <div class="form-group">
          <label>Số điện thoại</label>
          <input v-model="form.phone" type="phone-number" required>
        </div>
        <button type="submit">Đăng ký</button>
        <p>Đã có tài khoản? <nuxt-link to="/login">Đăng nhập</nuxt-link></p>
      </form>
    </div>
  </template>
  
  <script>
  export default {
    data() {
        
      return {
        form: {
          username: '',
          email: '',
          password: '',
          phone: '',
          role: 'CUSTOMER',
        }
      }
    },
    methods: {
      async handleRegister() {
        try {
          await this.$axios.$post('/api/auth/register', this.form)
          this.$router.push('/login')
        } catch (error) {
          console.error('Registration failed:', error)
        }
      }
    }
  }
  </script>