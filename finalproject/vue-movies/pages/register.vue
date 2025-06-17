<template>
     <div class="auth-page">
       <form v-if="!showVerification" @submit.prevent="handleRegister" class="login-form">
         <div class="logo">
           <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
             <path d="M12 2L3 7L12 12L21 7L12 2Z" fill="#3B82F6" />
             <path d="M3 17L12 22L21 17" stroke="#3B82F6" stroke-width="2" />
             <path d="M3 12L12 17L21 12" stroke="#3B82F6" stroke-width="2" />
           </svg>
         </div>
         <h2 class="title">Register</h2>

         <!-- Username -->
         <div class="form-group">
           <label class="input-label">Username</label>
           <input v-model="form.username" type="text" required class="input-field" placeholder="Enter your username" />
         </div>

         <!-- Email -->
         <div class="form-group">
           <label class="input-label">Email</label>
           <input v-model="form.email" type="email" required class="input-field" placeholder="Enter your email" />
         </div>

         <!-- Password -->
         <div class="form-group">
           <label class="input-label">Password</label>
           <input v-model="form.password" type="password" required class="input-field" placeholder="Enter your password" />
         </div>

         <!-- Confirm Password -->
         <div class="form-group">
           <label class="input-label">Confirm Password</label>
           <input v-model="confirmPassword" type="password" required class="input-field" placeholder="Confirm your password" />
         </div>

         <!-- Phone -->
         <div class="form-group">
           <label class="input-label">Phone Number</label>
           <input v-model="form.phone" type="tel" required class="input-field" placeholder="Enter your phone number" />
         </div>

         <!-- Register Button -->
         <button type="submit" class="login-button" :disabled="loading">
           <span v-if="loading">
             <i class="fas fa-spinner fa-spin"></i> Registering...
           </span>
           <span v-else>Register</span>
         </button>

         <!-- Already have an account -->
         <p class="register-text">
           Already have an account?
           <nuxt-link to="/login" class="register-link">Login</nuxt-link>
         </p>

         <!-- Error -->
         <p v-if="error" class="error-text">{{ error }}</p>
       </form>

       <form v-if="showVerification" @submit.prevent="handleVerify" class="login-form">
         <div class="logo">
           <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
             <path d="M12 2L3 7L12 12L21 7L12 2Z" fill="#3B82F6" />
             <path d="M3 17L12 22L21 17" stroke="#3B82F6" stroke-width="2" />
             <path d="M3 12L12 17L21 12" stroke="#3B82F6" stroke-width="2" />
           </svg>
         </div>
         <h2 class="title">Verify Email</h2>

         <div class="form-group">
           <label class="input-label">Verification Code</label>
           <input v-model="verificationCode" type="text" required class="input-field" placeholder="Enter your code" />
         </div>

         <button type="submit" class="login-button" :disabled="loading">
           <span v-if="loading">
             <i class="fas fa-spinner fa-spin"></i> Verifying...
           </span>
           <span v-else>Verify</span>
         </button>

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
           email: '',
           password: '',
           phone: '',
           role: 'CUSTOMER',
           avatar: ''
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
     background-color: #1e1e1e;
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
     color: #e0e0e0;
     margin-bottom: 8px;
     font-size: 14px;
   }

   .input-field {
     width: 100%;
     padding: 12px 15px;
     background-color: #2d2d2d;
     border: 1px solid #3b82f6;
     border-radius: 6px;
     color: white;
     font-size: 15px;
     transition: all 0.3s;
   }

   .input-field:focus {
     outline: none;
     border-color: #60a5fa;
     box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.3);
   }

   .input-field::placeholder {
     color: #9ca3af;
   }

   .login-button {
     width: 100%;
     padding: 14px;
     background-color: #3b82f6;
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
     background-color: #2563eb;
   }

   .register-text {
     text-align: center;
     color: #9ca3af;
     margin-top: 20px;
     font-size: 14px;
   }

   .register-link {
     color: #3b82f6;
     text-decoration: none;
     font-weight: 500;
     transition: color 0.3s;
   }

   .register-link:hover {
     color: #60a5fa;
     text-decoration: underline;
   }

   .error-text {
     color: #ef4444;
     text-align: center;
     margin-top: 15px;
     font-size: 14px;
   }
   </style>