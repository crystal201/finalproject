import Vue from 'vue'
import App from './App.vue'
import axios from 'axios'
import VueAxios from 'vue-axios'
import router from './router/index'

const axiosInstance = axios.create({
  baseURL: process.env.NODE_ENV === 'development' 
    ? 'http://localhost:8081/api' 
    : ''
})

Vue.use(VueAxios, axiosInstance)

Vue.prototype.$axios = axiosInstance

new Vue({
  router,
  render: h => h(App),
}).$mount('#app')