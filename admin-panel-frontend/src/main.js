import Vue from 'vue'
import App from './App.vue'
import axios from 'axios'
import VueAxios from 'vue-axios'

const axiosInstance = axios.create({
  baseURL: process.env.NODE_ENV === 'development' 
    ? 'http://localhost:8081/api' 
    : '/api'
})

Vue.use(VueAxios, axiosInstance)

Vue.prototype.$axios = axiosInstance

new Vue({
  render: h => h(App),
}).$mount('#app')