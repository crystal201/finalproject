import Vue from 'vue'
import VueRouter from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import RoomManagement from '../views/RoomManagement.vue'
import ManageBookings from '../views/ManageBookings.vue'
import ManageSeats from '../views/ManageSeats.vue'
import ManageUsers from '../views/ManageUsers.vue'
import Login from '../views/Login.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'rooms',
        name: 'RoomManagement',
        component: RoomManagement
      },
      {
        path: 'users',
        name: 'ManageUsers',
        component: ManageUsers
      },
      {
        path: 'bookings',
        name: 'ManageBookings',
        component: ManageBookings
      },
      {
        path: 'occupied-seats',
        name: 'ManageSeats',
        component: ManageSeats
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  routes
})
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !localStorage.getItem('authenticated')) {
    next('/login');
  } else {
    next();
  }
});
export default router