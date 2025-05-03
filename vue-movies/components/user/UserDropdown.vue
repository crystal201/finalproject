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
        class="user-avatar"
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
        class="dropdown-selection"
      >
        <nuxt-link
          to="/user/profile"
          class="profile-slc"
          @click="closeDropdown"
        >
          Profile
        </nuxt-link>
        <button
          @click="logout"
          class="logout-slc"
        >
          Logout
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

<style scoped lang="scss">
.relative {
  padding: 20px 0;
}
.user-avatar {
  border-radius: 100%;
  width: 50px;
}
.dropdown-selection{
  
  .profile-slc{
   font-size: 12px;
   padding: 4px 6px;
  }
  .logout-slc{
   font-size: 12px;
   padding: 4px 6px;
  }
}
</style>