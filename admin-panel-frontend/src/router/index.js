import Vue from 'vue'
import VueRouter from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import RoomManagement from '../views/RoomManagement.vue'

Vue.use(VueRouter)

const routes = [
  { path: '/', component: Dashboard },
  { path: '/rooms', component: RoomManagement }
]

const router = new VueRouter({
  routes
})

export default router