import Vue from 'vue';
import App from './views/App.vue';
import VueRouter from 'vue-router'; // Import vue-router
import axios from 'axios';
import VueAxios from 'vue-axios';

// Sử dụng vue-router
Vue.use(VueRouter);

// Định nghĩa routes
import Dashboard from './views/Dashboard.vue';
import RoomManagement from './views/RoomManagement.vue';

const routes = [
  { path: '/', component: Dashboard },
  { path: '/rooms', component: RoomManagement }
];

const router = new VueRouter({
  routes
});

// Sử dụng axios
Vue.use(VueAxios, axios);
Vue.config.productionTip = false;

new Vue({
  router, // Thêm router vào instance Vue
  render: h => h(App),
}).$mount('#app');