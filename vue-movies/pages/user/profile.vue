<template>
    <div class="container mx-auto p-4">
      <h1 class="text-2xl font-bold mb-4">Thông tin cá nhân</h1>
      <div v-if="user" class="bg-white p-6 rounded-md shadow-md">
        <div class="flex items-center mb-4">
          <img
            :src="avatarUrl"
            alt="User avatar"
            class="w-16 h-16 rounded-full object-cover mr-4"
          />
          <div>
            <h2 class="text-xl font-semibold">{{ user.username }}</h2>
            <p class="text-gray-600">{{ user.role }}</p>
          </div>
        </div>
        <div class="grid grid-cols-1 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700">Email</label>
            <p class="mt-1 text-sm text-gray-900">{{ user.email }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">Số điện thoại</label>
            <p class="mt-1 text-sm text-gray-900">{{ user.phone || 'Chưa cập nhật' }}</p>
          </div>
        </div>
  
        <!-- Lịch sử đặt vé -->
        <div class="mt-6">
          <h3 class="text-lg font-semibold mb-2">Lịch sử đặt vé</h3>
          <div v-if="bookings.length">
            <div v-for="booking in bookings" :key="booking.id" class="border p-4 mb-2 rounded-md">
              <p><strong>Phim:</strong> {{ booking.movieTitle }}</p>
              <p><strong>Ngày chiếu:</strong> {{ booking.date }}</p>
              <p><strong>Suất chiếu:</strong> {{ booking.showtime }}</p>
              <p><strong>Ghế:</strong> {{ booking.seats.join(', ') }}</p>
              <p><strong>Tổng cộng:</strong> ${{ booking.total }}</p>
              <p><strong>Đặt lúc:</strong> {{ formatDateTime(booking.createdAt) }}</p>
            </div>
          </div>
          <p v-else class="text-gray-600">Bạn chưa có vé nào.</p>
        </div>
  
        <nuxt-link
          to="/"
          class="mt-4 inline-block px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
        >
          Quay lại
        </nuxt-link>
      </div>
      <div v-else class="text-center">
        <p class="text-red-500">Không tìm thấy thông tin người dùng</p>
        <nuxt-link to="/" class="text-blue-500 hover:underline">Quay lại trang chủ</nuxt-link>
      </div>
    </div>
  </template>
  
  <script>
  import { mapState } from 'vuex';
  
  export default {
    middleware: 'auth',
    data() {
      return {
        bookings: [],
      };
    },
    computed: {
      ...mapState('auth', ['user']),
      avatarUrl() {
        return this.user?.avatar || `https://ui-avatars.com/api/?name=${this.user?.username || 'User'}&background=0D8ABC&color=fff`;
      },
    },
    created() {
      this.fetchBookings();
    },
    methods: {
      async fetchBookings() {
        try {
          const response = await this.$axios.get('/api/bookings');
          this.bookings = response.data;
        } catch (error) {
          console.error('Lỗi khi lấy lịch sử đặt vé:', error);
          this.bookings = [];
        }
      },
      formatDateTime(dateTime) {
        if (!dateTime) return 'N/A';
        const date = new Date(dateTime);
        return date.toLocaleString('vi-VN', {
          dateStyle: 'medium',
          timeStyle: 'short',
        });
      },
    },
  };
  </script>
  
  <style scoped>
  /* Tailwind xử lý style */
  </style>