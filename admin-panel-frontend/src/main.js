import Vue from 'vue';
import App from './App.vue';
import VueRouter from 'vue-router';
import axios from 'axios';
import VueAxios from 'vue-axios';

Vue.use(VueRouter);
Vue.use(VueAxios, axios.create({
  baseURL: process.env.VUE_APP_API_URL || (process.env.NODE_ENV === 'development' ? '/api' : 'http://157.66.219.181:8081')
}));

import router from './router';

Vue.config.productionTip = false;

new Vue({
  router,
  render: h => h(App),
}).$mount('#app');