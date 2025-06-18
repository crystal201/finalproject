import Vue from 'vue';
import App from './App.vue';
import VueRouter from 'vue-router';
import axios from 'axios';
import VueAxios from 'vue-axios';

Vue.use(VueRouter);
const baseURL = process.env.VUE_APP_API_URL || 'http://157.66.219.181:8081';
console.log('Configured baseURL:', baseURL); // Debug
Vue.use(VueAxios, axios.create({
  baseURL: baseURL
}));

import router from './router';

Vue.config.productionTip = false;

new Vue({
  router,
  render: h => h(App),
}).$mount('#app');