import Vue from 'vue';
import App from './App.vue';
import VueRouter from 'vue-router';
import axios from 'axios';
import VueAxios from 'vue-axios';

Vue.use(VueRouter);

// Cấu hình axios
const apiClient = axios.create({
  baseURL: process.env.NODE_ENV === 'development' 
    ? 'http://localhost:8081/api'
    : '/api'
});

Vue.use(VueAxios, apiClient);
console.log('Configured axios with baseURL:', apiClient.defaults.baseURL);

import router from './router';

Vue.config.productionTip = false;

new Vue({
  router,
  render: h => h(App),
}).$mount('#app');