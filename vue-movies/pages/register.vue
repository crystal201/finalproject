<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <i class="fas fa-film logo-icon"></i>
        <h1>CineCustomer</h1>
        <p>Create your account</p>
      </div>
      
      <form v-if="!showVerification" @submit.prevent="handleRegister" class="login-form">
        <div class="form-group">
          <label>Username</label>
          <div class="input-with-icon">
            <i class="fas fa-user"></i>
            <input v-model="form.username" type="text" placeholder="Enter your username" required>
          </div>
        </div>
        
        <div class="form-group">
          <label>Email</label>
          <div class="input-with-icon">
            <i class="fas fa-envelope"></i>
            <input v-model="form.email" type="email" placeholder="Enter your email" required>
          </div>
        </div>
        
        <div class="form-group">
          <label>Phone Number</label>
          <div class="input-with-icon">
            <i class="fas fa-phone"></i>
            <input v-model="form.phone" type="tel" placeholder="Enter your phone number" required>
          </div>
        </div>
        
        <div class="form-group">
          <label>Password</label>
          <div class="input-with-icon">
            <i class="fas fa-lock"></i>
            <input v-model="form.password" type="password" placeholder="Enter your password" required>
          </div>
        </div>
        
        <div class="form-group">
          <label>Confirm Password</label>
          <div class="input-with-icon">
            <i class="fas fa-lock"></i>
            <input v-model="confirmPassword" type="password" placeholder="Confirm your password" required>
          </div>
        </div>
        
        <button type="submit" class="login-btn" :disabled="loading">
          <span v-if="!loading">Register</span>
          <span v-else class="loading-spinner"></span>
        </button>
        
        <p v-if="error" class="error-message">
          <i class="fas fa-exclamation-circle"></i> {{ error }}
        </p>
        
        <p class="register-text">
          Already have an account? <nuxt-link to="/login" class="register-link">Login</nuxt-link>
        </p>
      </form>
      
      <form v-if="showVerification" @submit.prevent="handleVerify" class="login-form">
        <div class="login-header">
          <i class="fas fa-film logo-icon"></i>
          <h1>CineCustomer</h1>
          <p>Verify your email</p>
        </div>
        
        <div class="form-group">
          <label>Verification Code</label>
          <div class="input-with-icon">
            <i class="fas fa-shield-alt"></i>
            <input v-model="verificationCode" type="text" placeholder="Enter your code" required>
          </div>
        </div>
        
        <button type="submit" class="login-btn" :disabled="loading">
          <span v-if="!loading">Verify</span>
          <span v-else class="loading-spinner"></span>
        </button>
        
        <p v-if="error" class="error-message">
          <i class="fas fa-exclamation-circle"></i> {{ error }}
        </p>
      </form>
      
      <div class="login-footer">
        <p>© 2023 CineCustomer. All rights reserved.</p>
      </div>
    </div>
    
    <div class="login-background">
      <div class="background-overlay"></div>
    </div>
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
        avatar: '',
      },
      confirmPassword: '',
      verificationCode: '',
      showVerification: false,
      error: '',
      loading: false
    };
  },
  methods: {
    async handleRegister() {
      this.error = '';
      this.loading = true;

      if (this.form.password !== this.confirmPassword) {
        this.error = 'Password and Confirm Password do not match!';
        this.$toast.error(this.error);
        this.loading = false;
        return;
      }

      try {
        const response = await this.$axios.post('/api/users/register', this.form);
        this.showVerification = true;
        this.$toast.success(response.data.message);
      } catch (error) {
        const message = error.response?.data?.error || 'Registration failed, please try again!';
        this.error = message;
        this.$toast.error(this.error);
      } finally {
        this.loading = false;
      }
    },
    async handleVerify() {
      this.error = '';
      this.loading = true;

      try {
        const response = await this.$axios.post('/api/users/verify', {
          email: this.form.email,
          code: this.verificationCode
        });
        this.$toast.success(response.data.message);
        this.$router.push('/login');
      } catch (error) {
        const message = error.response?.data?.error || 'Verification failed, please try again!';
        this.error = message;
        this.$toast.error(this.error);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  min-height: 100vh;
}

.login-card {
  width: 100%;
  max-width: 450px;
  background: var(--darker);
  padding: 40px;
  display: flex;
  flex-direction: column;
  z-index: 2;
  box-shadow: 0 0 40px rgba(0, 0, 0, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-icon {
  font-size: 3rem;
  color: var(--primary);
  margin-bottom: 15px;
}

.login-header h1 {
  font-size: 2rem;
  color: var(--light);
  margin-bottom: 5px;
}

.login-header p {
  color: var(--gray);
  font-size: 0.9rem;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: var(--light);
  font-size: 0.9rem;
  font-weight: 500;
}

.input-with-icon {
  position: relative;
}

.input-with-icon i {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--gray);
}

.input-with-icon input {
  width: 100%;
  padding: 12px 15px 12px 45px;
  background: var(--card-bg);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: var(--light);
  font-size: 1rem;
  transition: all 0.2s ease;
}

.input-with-icon input:focus {
  border-color: var(--primary);
  outline: none;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2);
}

.login-btn {
  background: var(--primary);
  color: white;
  border: none;
  padding: 14px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 48px;
}

.login-btn:hover {
  background: var(--primary-hover);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-message {
  color: var(--danger);
  background: rgba(239, 68, 68, 0.1);
  padding: 12px;
  border-radius: 8px;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: -10px;
}

.register-text {
  text-align: center;
  color: var(--gray);
  font-size: 0.9rem;
  margin-top: 15px;
}

.register-link {
  color: var(--primary);
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s ease;
}

.register-link:hover {
  color: var(--primary-hover);
  text-decoration: underline;
}

.login-footer {
  margin-top: auto;
  padding-top: 30px;
  text-align: center;
  color: var(--gray);
  font-size: 0.8rem;
}

.login-background {
  flex: 1;
  background: url('https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80') center/cover no-repeat;
  position: relative;
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(30, 41, 59, 0.9) 0%, rgba(15, 23, 42, 0.9) 100%);
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }
  
  .login-card {
    max-width: 100%;
    padding: 30px 20px;
  }
  
  .login-background {
    display: none;
  }
}
</style>