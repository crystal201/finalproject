<template>
  <nav :class="$style.nav">
    <ul class="nolist">
      <li>
        <nuxt-link
          v-if="!$store.state.auth.isAuthenticated"
          :to="{ name: 'login' }"
          aria-label="Login"
        >
          Đăng nhập
        </nuxt-link>
        <div
          v-else
          class="logout-button"
          aria-label="Logout"
        >
          <UserDropdown/>
        </div>
      </li>
      <li>
        <nuxt-link exact :to="{ name: 'index' }" aria-label="Home">
          <!-- eslint-disable-next-line -->
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
          >
            <g
              fill="none"
              stroke="#fff"
              stroke-width="1.5"
              stroke-linecap="round"
              stroke-miterlimit="10"
              stroke-linejoin="round"
            >
              <path
                d="M8.5 23.2H1.3V9L12 .8 22.7 9v14.2h-7.2v-5c0-1.9-1.6-3.4-3.5-3.4s-3.5 1.5-3.5 3.4v5z"
              />
            </g>
          </svg>
        </nuxt-link>
      </li>
      <li>
        <nuxt-link :to="{ name: 'movie' }" aria-label="Movies">
          <!-- eslint-disable-next-line -->
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
          >
            <g
              fill="none"
              stroke="#fff"
              stroke-width="1.5"
              stroke-miterlimit="10"
              stroke-linejoin="round"
              stroke-linecap="round"
            >
              <path
                d="M3.2 12.8h19.6v9.5c0 .5-.4.9-1 .9H4.1c-.5 0-1-.4-1-.9v-9.5"
              />
              <path
                d="M3.3 13.1l-2-4.4c-.2-.5 0-1 .5-1.2L18 .8c.5-.2 1.1 0 1.3.5l1.8 4-17.5 7.3"
              />
              <path d="M15 2.1l-.9 6M8 4.7l-1.2 6.6" />
            </g>
          </svg>
        </nuxt-link>
      </li>
      <li>
        <nuxt-link :to="{ name: 'tv' }" aria-label="TV Shows">
          <!-- eslint-disable-next-line -->
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
          >
            <g
              fill="none"
              stroke="#fff"
              stroke-width="1.5"
              stroke-linecap="round"
              stroke-miterlimit="10"
            >
              <path
                d="M21.4 23H2.6c-.9 0-1.6-.7-1.6-1.6V8.9c0-.9.7-1.6 1.6-1.6h18.9c.8 0 1.5.7 1.5 1.6v12.6c0 .8-.7 1.5-1.6 1.5zM6.4 1L12 7M17.6 1L12 7"
              />
            </g>
          </svg>
        </nuxt-link>
      </li>
      <li>
        <nuxt-link :to="{ name: 'recommendation' }" aria-label="Recommendation">
          <!-- eslint-disable-next-line -->
<svg fill="#ffffff" height="24" width="24" version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 512 512" xml:space="preserve" stroke="#ffffff"><g id="SVGRepo_bgCarrier" stroke-width="0"></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"> <g> <g> <path d="M472.018,45.359H39.982C17.935,45.359,0,63.294,0,85.34v186.277c0,5.272,4.274,9.547,9.547,9.547 c5.272,0,9.547-4.274,9.547-9.547V85.34c0-11.517,9.37-20.888,20.888-20.888h432.036c11.517,0,20.888,9.371,20.888,20.888v252.124 h-44.692c-5.272,0-9.547,4.274-9.547,9.547s4.274,9.547,9.547,9.547h44.692v6.021c0,11.517-9.37,20.888-20.888,20.888H39.982 c-11.518,0-20.888-9.371-20.888-20.888v-6.021h397.299c5.272,0,9.547-4.274,9.547-9.547s-4.274-9.547-9.547-9.547H19.094V303.44 c0-5.272-4.274-9.547-9.547-9.547c-5.272,0-9.547,4.274-9.547,9.547v59.139c0,22.047,17.935,39.982,39.982,39.982h129.753 l-7.375,44.987h-18.402c-5.272,0-9.547,4.274-9.547,9.547c0,5.272,4.274,9.547,9.547,9.547h224.09 c5.272,0,9.547-4.274,9.547-9.547c0-5.272-4.274-9.547-9.547-9.547h-18.402l-7.375-44.987h129.748 c22.045,0,39.982-17.935,39.982-39.982V85.341C512,63.294,494.065,45.359,472.018,45.359z M181.71,447.548l7.375-44.987h133.837 l7.374,44.987H181.71z"></path> </g> </g> <g> <g> <path d="M404.035,147.1c-12.07-30.08-33.388-49.698-58.176-49.698s-46.106,19.618-58.176,49.698 c-30.08,12.07-49.698,33.387-49.698,58.176s19.618,46.106,49.698,58.176c12.07,30.081,33.387,49.698,58.176,49.698 c24.789,0,46.106-19.618,58.176-49.698c30.08-12.07,49.698-33.387,49.698-58.176S434.115,159.17,404.035,147.1z M280.305,238.351 c-14.354-8.856-23.225-20.509-23.225-33.075s8.871-24.22,23.225-33.075c-2.062,10.386-3.18,21.487-3.18,33.075 S278.241,227.966,280.305,238.351z M345.859,116.496c12.566,0,24.221,8.871,33.075,23.225c-10.386-2.062-21.487-3.18-33.075-3.18 s-22.69,1.116-33.075,3.18C321.639,125.367,333.293,116.496,345.859,116.496z M345.859,294.056 c-12.566,0-24.221-8.871-33.075-23.225c10.386,2.062,21.487,3.18,33.075,3.18s22.69-1.116,33.075-3.18 C370.08,285.185,358.425,294.056,345.859,294.056z M389.003,248.422c-12.863,4.114-27.609,6.494-43.145,6.494 s-30.282-2.38-43.145-6.494c-4.114-12.864-6.494-27.609-6.494-43.146s2.38-30.282,6.494-43.146 c12.863-4.114,27.609-6.494,43.145-6.494s30.282,2.38,43.145,6.494c4.114,12.863,6.494,27.609,6.494,43.146 S393.117,235.558,389.003,248.422z M411.414,238.351c2.062-10.386,3.18-21.487,3.18-33.075s-1.116-22.69-3.18-33.075 c14.355,8.856,23.225,20.509,23.225,33.075S425.768,229.496,411.414,238.351z"></path> </g> </g> <g> <g> <path d="M345.859,175.444c-16.45,0-29.832,13.382-29.832,29.832c0,16.45,13.382,29.832,29.832,29.832 c16.45,0,29.832-13.382,29.832-29.832S362.309,175.444,345.859,175.444z M345.859,216.014c-5.922,0-10.738-4.817-10.738-10.738 c0-5.922,4.817-10.738,10.738-10.738s10.738,4.817,10.738,10.738S351.781,216.014,345.859,216.014z"></path> </g> </g> <g> <g> <path d="M188.699,110.13h-63.802c-5.272,0-9.547,4.274-9.547,9.547c0,5.272,4.274,9.547,9.547,9.547h63.802 c5.272,0,9.547-4.274,9.547-9.547C198.246,114.405,193.971,110.13,188.699,110.13z"></path> </g> </g> <g> <g> <path d="M99.439,110.13H72.235c-5.272,0-9.547,4.274-9.547,9.547c0,5.272,4.273,9.547,9.547,9.547h27.204 c5.272,0,9.547-4.274,9.547-9.547C108.986,114.405,104.712,110.13,99.439,110.13z"></path> </g> </g> <g> <g> <path d="M188.699,188.41h-24.342c-5.272,0-9.547,4.274-9.547,9.547c0,5.272,4.274,9.547,9.547,9.547h24.342 c5.272,0,9.547-4.274,9.547-9.547C198.246,192.684,193.971,188.41,188.699,188.41z"></path> </g> </g> <g> <g> <path d="M138.901,188.41H72.235c-5.272,0-9.547,4.274-9.547,9.547c0,5.272,4.273,9.547,9.547,9.547h66.666 c5.272,0,9.547-4.274,9.547-9.547C148.447,192.684,144.173,188.41,138.901,188.41z"></path> </g> </g> <g> <g> <path d="M188.7,149.271H72.235c-5.272,0-9.547,4.274-9.547,9.547c0,5.272,4.273,9.547,9.547,9.547H188.7 c5.272,0,9.547-4.274,9.547-9.547C198.247,153.545,193.973,149.271,188.7,149.271z"></path> </g> </g> </g></svg>
        </nuxt-link>
      </li>
      <li>
        <button
          class="search-toggle"
          type="button"
          aria-label="Search"
          aria-haspopup="true"
          :aria-expanded="`${searchOpen}`"
          @click="toggleSearch"
        >
          <!-- eslint-disable-next-line -->
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
          >
            <g
              fill="none"
              stroke="#fff"
              stroke-width="1.5"
              stroke-linecap="round"
              stroke-miterlimit="10"
            >
              <path d="M16.4 16.7l6.3 6.5" />
              <ellipse cx="10.5" cy="9.8" rx="9.2" ry="9.1" />
            </g>
          </svg>
        </button>
      </li>
      <span v-if="$store.state.auth.isAuthenticated" class="nav-welcome">
      Welcome, {{ $store.state.auth.user?.username }}
    </span>
    </ul>

  </nav>
</template>

<script>
import { mapState } from "vuex";
import UserDropdown from "../../components/user/UserDropdown.vue";

export default {
  computed: {
    ...mapState("search", ["searchOpen"]),
    ...mapState("auth", ["isAuthenticated"]),
  },

  methods: {
    toggleSearch() {
      if (this.$route.name !== "search") {
        this.$store.commit("search/toggleSearch");
      }
    },
    async logout() {
      try {
        await this.$store.dispatch("auth/logout");
        this.$router.push("/login");
      } catch (error) {
        console.error("Đăng xuất thất bại:", error);
      }
    },
  },
};
</script>

<style lang="scss" module>
@import "~/assets/css/utilities/variables.scss";

.nav {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 5;
  height: 4.5rem;
  background-color: #000;

  @media (min-width: $breakpoint-large) {
    top: 0;
    right: auto;
    width: 10rem;
    height: 100%;
    border-right: 1px solid $secondary-color;
  }

  ul {
    display: flex;
    height: 100%;

    @media (min-width: $breakpoint-large) {
      flex-direction: column;
    }

    li {
      flex: 1 1 auto;
      height: 100%;

      @media (min-width: $breakpoint-large) {
        flex: 0 1 auto;
        height: 10rem;
      }
    }
  }

  button {
    padding: 0;
    margin: 0;
    background: none;
  }

  a,
  button {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    outline: 0;
    transition: all 0.2s;
    color: white;
    font-size: 16px;

    &:hover,
    &:focus {
      opacity: 0.8;
    }
  }
}
</style>

<style lang="scss" scoped>
@import "~/assets/css/utilities/variables.scss";

a.nuxt-link-active {
  &:hover,
  &:focus {
    opacity: 1;
  }

  svg g {
    stroke: $primary-color;
  }
}

.logout-button {
  color: white;
  background: none;
  border: none;
  cursor: pointer;
}
.nav-welcome {
  padding: 8px 12px;
}
</style>
