<template>
  <div class="relative" v-click-outside="closeDropdown">
    <!-- Avatar -->
    <button
      @click="toggleDropdown"
      class="flex items-center focus:outline-none"
      aria-label="User menu"
    >
      <img
        :src="avatarUrl"
        alt="User avatar"
        class="w-10 h-10 rounded-full object-cover"
        :class="{ 'border-2 border-blue-500': isDropdownOpen }"
      />
    </button>

    <!-- Dropdown -->
    <transition
      enter-active-class="transition ease-out duration-100"
      enter-from-class="transform opacity-0 scale-95"
      enter-to-class="transform opacity-100 scale-100"
      leave-active-class="transition ease-in duration-75"
      leave-from-class="transform opacity-100 scale-100"
      leave-to-class="transform opacity-0 scale-95"
    >
      <div
        v-if="isDropdownOpen"
        class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-10"
      >
        <nuxt-link
          to="/user/profile"
          class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
          @click="closeDropdown"
        >
          Thông tin cá nhân
        </nuxt-link>
        <button
          @click="logout"
          class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
        >
          Đăng xuất
        </button>
      </div>
    </transition>
  </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
  data() {
    return {
      isDropdownOpen: false,
    };
  },
  computed: {
    ...mapState('auth', ['user']),
    avatarUrl() {
      // Nếu có avatar trong user, dùng nó; nếu không, dùng ảnh mặc định từ ui-avatars
      return this.user?.avatar || `https://ui-avatars.com/api/?name=${this.user?.username || 'User'}&background=0D8ABC&color=fff`;
    },
  },
  methods: {
    toggleDropdown() {
      this.isDropdownOpen = !this.isDropdownOpen;
    },
    closeDropdown() {
      this.isDropdownOpen = false;
    },
    async logout() {
      try {
        await this.$store.dispatch('auth/logout');
        this.$router.push('/');
      } catch (error) {
        console.error('Đăng xuất thất bại:', error);
      }
    },
  },
  directives: {
    clickOutside: {
      bind(el, binding) {
        el.clickOutsideEvent = function (event) {
          if (!(el === event.target || el.contains(event.target))) {
            binding.value();
          }
        };
        document.addEventListener('click', el.clickOutsideEvent);
      },
      unbind(el) {
        document.removeEventListener('click', el.clickOutsideEvent);
      },
    },
  },
};
</script>

<style scoped>
/* Tailwind xử lý hầu hết style, thêm scoped style nếu cần */
</style>