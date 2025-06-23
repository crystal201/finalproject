import Vue from 'vue'
import App from './App.vue'
import axios from 'axios'
import VueAxios from 'vue-axios'
import 'vue-toastification/dist/index.css';
import router from './router/index'

const axiosInstance = axios.create({
  baseURL: process.env.NODE_ENV === 'development' 
    ? 'http://localhost:8081/api' 
    : ''
})

Vue.use(VueAxios, axiosInstance)
Vue.use(Toast, {
  position: 'top-right',
  timeout: 5000,
  closeOnClick: true,
  pauseOnFocusLoss: true,
  pauseOnHover: true,
});
Vue.prototype.$axios = axiosInstance

new Vue({
  router,
  render: h => h(App),
}).$mount('#app')