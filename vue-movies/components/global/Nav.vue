<template>
  <nav :class="$style.nav">
    <ul class="nolist">
      <li>
        <nuxt-link
          v-if="!$store.state.auth.isAuthenticated"
          :to="{ name: 'login' }"
          aria-label="Login"
        >
          Login
        </nuxt-link>
        <div v-else class="logout-button" aria-label="Logout">
          <UserDropdown />
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
      <!-- <li>
        <nuxt-link :to="{ name: 'tv' }" aria-label="TV Shows">
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
      </li> -->
      <li>
        <nuxt-link :to="{ name: 'recommendation' }" aria-label="Recommendation">
          <!-- eslint-disable-next-line -->
<svg width="24px" height="24px" viewBox="-5.28 -5.28 58.56 58.56" xmlns="http://www.w3.org/2000/svg" fill="#ffffff" stroke="#ffffff"><g id="SVGRepo_bgCarrier" stroke-width="0"><rect x="-5.28" y="-5.28" width="58.56" height="58.56" rx="0" fill="#7ed0ec" strokewidth="0"></rect></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"> <title>Recommendation Movies</title> <g id="Layer_2" data-name="Layer 2"> <g id="invisible_box" data-name="invisible box"> <rect width="48" height="48" fill="none"></rect> </g> <g id="Q3_icons" data-name="Q3 icons"> <g> <path d="M45.6,18.7,41,14.9V7.5a1,1,0,0,0-.6-.9L30.5,2.1h-.4l-.6.2L24,5.9,18.5,2.2,17.9,2h-.4L7.6,6.6a1,1,0,0,0-.6.9v7.4L2.4,18.7a.8.8,0,0,0-.4.8v9H2a.8.8,0,0,0,.4.8L7,33.1v7.4a1,1,0,0,0,.6.9l9.9,4.5h.4l.6-.2L24,42.1l5.5,3.7.6.2h.4l9.9-4.5a1,1,0,0,0,.6-.9V33.1l4.6-3.8a.8.8,0,0,0,.4-.7V19.4h0A.8.8,0,0,0,45.6,18.7Zm-5.1,6.8H42v1.6l-3.5,2.8-.4.3-.4-.2a1.4,1.4,0,0,0-2,.7,1.5,1.5,0,0,0,.6,2l.7.3h0v5.4l-6.6,3.1-4.2-2.8-.7-.5V25.5H27a1.5,1.5,0,0,0,0-3H25.5V9.7l.7-.5,4.2-2.8L37,9.5v5.4h0l-.7.3a1.5,1.5,0,0,0-.6,2,1.4,1.4,0,0,0,1.3.9l.7-.2.4-.2.4.3L42,20.9v1.6H40.5a1.5,1.5,0,0,0,0,3ZM21,25.5h1.5V38.3l-.7.5-4.2,2.8L11,38.5V33.1h0l.7-.3a1.5,1.5,0,0,0,.6-2,1.4,1.4,0,0,0-2-.7l-.4.2-.4-.3L6,27.1V25.5H7.5a1.5,1.5,0,0,0,0-3H6V20.9l3.5-2.8.4-.3.4.2.7.2a1.4,1.4,0,0,0,1.3-.9,1.5,1.5,0,0,0-.6-2L11,15h0V9.5l6.6-3.1,4.2,2.8.7.5V22.5H21a1.5,1.5,0,0,0,0,3Z"></path> <path d="M13.9,9.9a1.8,1.8,0,0,0,0,2.2l2.6,2.5v2.8l-4,4v5.2l4,4v2.8l-2.6,2.5a1.8,1.8,0,0,0,0,2.2,1.5,1.5,0,0,0,1.1.4,1.5,1.5,0,0,0,1.1-.4l3.4-3.5V29.4l-4-4V22.6l4-4V13.4L16.1,9.9A1.8,1.8,0,0,0,13.9,9.9Z"></path> <path d="M31.5,14.6l2.6-2.5a1.8,1.8,0,0,0,0-2.2,1.8,1.8,0,0,0-2.2,0l-3.4,3.5v5.2l4,4v2.8l-4,4v5.2l3.4,3.5a1.7,1.7,0,0,0,2.2,0,1.8,1.8,0,0,0,0-2.2l-2.6-2.5V30.6l4-4V21.4l-4-4Z"></path> </g> </g> </g> </g></svg>        </nuxt-link>
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
    </ul>
    <TypingWelcome v-if="$store.state.auth.isAuthenticated" />
  </nav>
</template>

<script>
import { mapState } from "vuex";
import UserDropdown from "../../components/user/UserDropdown.vue";
import TypingWelcome from "./TypingWelcome.vue";

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
        this.$toast.success("Logout successful!");
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
