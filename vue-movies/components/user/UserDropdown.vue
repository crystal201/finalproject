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
        :class="{'right-0': !isMobile, 'bottom-full mb-2': isMobile}"
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
      isMobile: false
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
        console.error('Logout failed:', error);
      }
    },
    checkScreenSize() {
      this.isMobile = window.innerWidth < 768; // Adjust breakpoint as needed
    }
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
  mounted() {
    this.checkScreenSize();
    window.addEventListener('resize', this.checkScreenSize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkScreenSize);
  }
};
</script>

<style scoped lang="scss">
.relative {
  position: relative;
  padding: 20px 0;
}

.user-avatar {
  border-radius: 100%;
  width: 50px;
  height: 50px;
  object-fit: cover;
}

.dropdown-selection {
  position: absolute;
  min-width: 150px;
  background: white;
  border-radius: 6px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 50;
  display: flex;
  flex-direction: column;
  padding: 8px 0;
  
  @media (min-width: 768px) {
    right: -140;
    top: 100%;
    margin-top: -70px;
  }
  
  @media (max-width: 767px) {
    bottom: 100%;
    left: 0;
    margin-bottom: 8px;
  }

  .profile-slc,
  .logout-slc {
    padding: 8px 16px;
    text-align: left;
    font-size: 14px;
    color: #333;
    transition: background-color 0.2s;
    
    &:hover {
      background-color: #f5f5f5;
    }
  }

  .logout-slc {
    color: #e53e3e;
  }
}
</style>