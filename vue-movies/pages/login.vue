<template>
  <div class="auth-page">
    <form @submit.prevent="handleLogin" class="login-form">
      <div class="logo">
        <!-- Có thể thay bằng logo của bạn -->
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 2L3 7L12 12L21 7L12 2Z" fill="#3B82F6"/>
          <path d="M3 17L12 22L21 17" stroke="#3B82F6" stroke-width="2"/>
          <path d="M3 12L12 17L21 12" stroke="#3B82F6" stroke-width="2"/>
        </svg>
      </div>
      <h2 class="title">Đăng nhập</h2>
      <div class="form-group">
        <label class="input-label">Tài khoản</label>
        <input 
          v-model="form.username" 
          type="text" 
          required
          class="input-field"
          placeholder="Nhập tài khoản">
      </div>
      <div class="form-group">
        <label class="input-label">Mật khẩu</label>
        <input 
          v-model="form.password" 
          type="password" 
          required
          class="input-field"
          placeholder="Nhập mật khẩu">
      </div>
      <button type="submit" class="login-button">Đăng nhập</button>
      <p class="register-text">
        Chưa có tài khoản? 
        <nuxt-link to="/register" class="register-link">Đăng ký ngay</nuxt-link>
      </p>
      <p v-if="error" class="error-text">{{ error }}</p>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      error: ''
    };
  },
  methods: {
  async handleLogin() {
    this.error = ''
    try {
      // Cách 1: Nếu dùng namespaced module
      const success = await this.$store.dispatch('auth/login', this.form)      
      // Cách 2: Nếu không dùng namespace
      // const success = await this.$store.dispatch('login', this.form)
      
      if (success) {
        this.$router.push('/')
      } else {
        this.error = 'Sai tài khoản hoặc mật khẩu'
      }
    } catch (error) {
      console.error('Login error:', error)
      this.error = 'Lỗi kết nối máy chủ'
    }
  }
}
};
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #121212;
  padding: 20px;
}

.login-form {
  width: 100%;
  max-width: 400px;
  background-color: #1E1E1E;
  padding: 40px 30px;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.logo {
  text-align: center;
  margin-bottom: 25px;
}

.title {
  color: white;
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
}

.input-label {
  display: block;
  color: #E0E0E0;
  margin-bottom: 8px;
  font-size: 14px;
}

.input-field {
  width: 100%;
  padding: 12px 15px;
  background-color: #2D2D2D;
  border: 1px solid #3B82F6;
  border-radius: 6px;
  color: white;
  font-size: 15px;
  transition: all 0.3s;
}

.input-field:focus {
  outline: none;
  border-color: #60A5FA;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.3);
}

.input-field::placeholder {
  color: #9CA3AF;
}

.login-button {
  width: 100%;
  padding: 14px;
  background-color: #3B82F6;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-top: 10px;
}

.login-button:hover {
  background-color: #2563EB;
}

.register-text {
  text-align: center;
  color: #9CA3AF;
  margin-top: 20px;
  font-size: 14px;
}

.register-link {
  color: #3B82F6;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.register-link:hover {
  color: #60A5FA;
  text-decoration: underline;
}

.error-text {
  color: #EF4444;
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
}
</style>