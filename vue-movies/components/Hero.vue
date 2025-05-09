<template>
  <div>
    <div :class="$style.hero">
      <div :class="$style.backdrop">
        <div>
          <button
            v-if="trailer"
            :class="$style.play"
            type="button"
            aria-label="Play Trailer"
            @click="openModal">
            <!-- eslint-disable-next-line -->
            <svg xmlns="http://www.w3.org/2000/svg" width="55" height="55" viewBox="0 0 55 55"><circle cx="27.5" cy="27.5" r="26.75" fill="none" stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"/><path fill="none" stroke="#fff" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20.97 40.81L40.64 27.5 20.97 14.19v26.62z"/></svg>
          </button>

          <img
            v-if="backdrop"
            v-lazyload="backdrop"
            class="lazyload"
            :class="$style.image"
            :alt="name">
        </div>
      </div>

      <div :class="$style.pane">
        <transition
          appear
          name="hero">
          <div>
            <h1 :class="$style.name">
              <template v-if="isSingle">
                {{ name }}
              </template>

              <template v-else>
                <nuxt-link :to="{ name: `${type}-id`, params: { id: item.id } }">
                  {{ name }}
                </nuxt-link>
              </template>
            </h1>

            <div :class="$style.meta">
              <div
                v-if="stars || item.vote_count"
                :class="$style.rating">
                <div
                  v-if="stars"
                  :class="$style.stars">
                  <div :style="{ width: `${stars}%` }" />
                </div>

                <div v-if="item.vote_count > 0">
                  {{ item.vote_count | numberWithCommas }} Reviews
                </div>
              </div>

              <div :class="$style.info">
                <span v-if="item.number_of_seasons">Season {{ item.number_of_seasons }}</span>
                <span v-if="yearStart">{{ yearStart }}</span>
                <span v-if="item.runtime">{{ item.runtime | runtime }}</span>
                <span v-if="cert">Cert. {{ cert }}</span>
              </div>
            </div>

            <div :class="$style.desc">
              {{ item.overview | truncate(200) }}
            </div>

            <button
              v-if="trailer"
              class="button button--icon"
              :class="$style.trailer"
              type="button"
              @click="openModal">
              <!-- eslint-disable-next-line -->
              <span class="icon"><svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="#fff"><path d="M3 22v-20l18 10-18 10z"/></svg></span>
              <span class="txt">Watch Trailer</span>
            </button>
            <button class="button button--icon ticket-now-button">
    <nuxt-link :to="{ path: '/booking', query: { movieId: item.id } }" class="ticket-link">
      <span class="ticket-icon">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" height="24" width="24">
          <path d="M2 9a3 3 0 0 1 0 6v2a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-2a3 3 0 0 1 0-6V7a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2z"></path>
          <path d="M13 5v2"></path>
          <path d="M13 17v2"></path>
          <path d="M13 11v2"></path>
        </svg>
      </span>
      <span class="ticket-text">Ticket Now</span>
    </nuxt-link>
  </button>
          </div>
        </transition>
      </div>
    </div>

    <Modal
      v-if="modalVisible"
      :data="trailer"
      type="iframe"
      @close="closeModal" />
  </div>
</template>

<script>
import { name, stars, yearStart, cert, backdrop, trailer } from '~/mixins/Details';
import Modal from '~/components/Modal';

export default {
  components: {
    Modal,
  },

  mixins: [
    name,
    stars,
    yearStart,
    cert,
    backdrop,
    trailer,
  ],

  props: {
    item: {
      type: Object,
      required: true,
    },
  },

  data () {
    return {
      isSingle: this.item.id === this.$route.params.id,
      modalVisible: false,
    };
  },

  computed: {
    type () {
      return this.item.title ? 'movie' : 'tv';
    },
  },

  methods: {
    openModal () {
      this.modalVisible = true;
    },

    closeModal () {
      this.modalVisible = false;
    },
  },
};
</script>

<style lang="scss" module>
@import '~/assets/css/utilities/variables.scss';
.ticket-now-button {
  position: relative;
  overflow: hidden;
  padding: 0;
  border: none;
  background: linear-gradient(45deg, #ff4d4d, #f9cb28);
  color: white;
  font-weight: bold;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(249, 203, 40, 0.3);
  
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 20px rgba(249, 203, 40, 0.4);
    
    .ticket-link {
      background-position: 100% 0;
    }
    
    .ticket-icon {
      transform: rotate(10deg);
    }
    
    .ticket-arrow {
      transform: translateX(5px);
      opacity: 1;
    }
  }
  
  &:active {
    transform: translateY(1px);
  }
}

.ticket-link {
  display: flex;
  align-items: center;
  padding: 12px 25px;
  color: white;
  text-decoration: none;
  background: linear-gradient(45deg, #ff4d4d, #f9cb28);
  background-size: 200% 100%;
  transition: all 0.3s ease;
  border-radius: 50px;
}

.ticket-icon {
  width: 20px;
  height: 20px;
  margin-right: 10px;
  transition: transform 0.3s ease;
}

.ticket-text {
  position: relative;
  z-index: 1;
}

.ticket-arrow {
  margin-left: 8px;
  opacity: 0;
  transition: all 0.3s ease;
}

/* Animation pulse */
@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

.ticket-now-button {
  animation: pulse 2s infinite;
  
  &:hover {
    animation: none;
  }
}

/* Ripple effect */
.ticket-now-button::after {
  content: "";
  position: absolute;
  top: 50%;
  left: 50%;
  width: 5px;
  height: 5px;
  background: rgba(255, 255, 255, 0.5);
  opacity: 0;
  border-radius: 100%;
  transform: scale(1, 1) translate(-50%, -50%);
  transform-origin: 50% 50%;
}

.ticket-now-button:focus:not(:active)::after {
  animation: ripple 1s ease-out;
}

@keyframes ripple {
  0% {
    transform: scale(0, 0);
    opacity: 0.5;
  }
  100% {
    transform: scale(20, 20);
    opacity: 0;
  }
}
.hero {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 35rem;
  color: #999;
  background-color: #000;

  @media (min-width: $breakpoint-xsmall) {
    height: 50rem;
  }

  @media (min-width: $breakpoint-medium) {
    position: relative;
    display: block;
    height: 0;
    padding-bottom: 40%;
  }
}

.backdrop {
  position: relative;
  display: flex;
  flex: 1 1 auto;
  min-height: 0;

  @media (min-width: $breakpoint-medium) {
    position: absolute;
    top: 0;
    right: 0;
    display: block;
    width: 71.1%;
    height: 100%;
  }

  &::after {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    display: block;
    content: '';
    background-image: linear-gradient(to top, rgba(0, 0, 0, 1) 0%, rgba(0, 0, 0, 0.1) 50%, rgba(0, 0, 0, 0.1) 100%);

    @media (min-width: $breakpoint-medium) {
      background-image: linear-gradient(to right, #000 0, transparent 50%, transparent 100%);
    }
  }

  > div {
    width: 100%;

    @media (min-width: $breakpoint-medium) {
      display: inline;
    }
  }
}

.play {
  position: absolute;
  top: 50%;
  left: 50%;
  z-index: 1;
  padding: 0;
  margin: 0;
  background: none;
  transform: translate(-50%, -50%);

  @media (min-width: $breakpoint-medium) {
    display: none;
  }
}

.image {
  display: inline-block;
  max-width: none;
  height: 100%;

  @media (max-width: $breakpoint-medium - 1) {
    width: 100%;
    object-fit: cover;
  }
}

.pane {
  padding: 0 1.5rem 1.5rem;

  @media (min-width: $breakpoint-small) {
    padding: 0 4rem 4rem;
  }

  @media (min-width: $breakpoint-medium) {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    z-index: 1;
    display: flex;
    align-items: center;
    width: 55%;
    height: 100%;
    padding: 5rem 4rem;
  }

  @media (min-width: $breakpoint-large) {
    padding-right: 5rem;
    padding-left: 5rem;
  }

  @media (min-width: $breakpoint-xlarge) {
    width: 43%;
  }
}

.name {
  margin: 0 0 1.4rem;
  font-size: 2.8rem;
  line-height: 1.1;
  color: #fff;
  letter-spacing: $letter-spacing;

  @media (min-width: $breakpoint-small) {
    margin-bottom: 1.8rem;
  }

  @media (min-width: $breakpoint-large) {
    font-size: 2.4vw;
  }
}

.meta {
  font-size: 1.4rem;

  @media (min-width: $breakpoint-small) {
    display: flex;
  }

  @media (min-width: 1650px) {
    font-size: 0.9vw;
  }
}

.rating {
  display: flex;
  align-items: center;
  margin-bottom: 1.3rem;

  @media (min-width: $breakpoint-small) {
    margin: 0 1.2rem 0 0;
  }
}

.stars {
  width: 8.5rem;
  height: 1.4rem;
  margin-right: 1rem;
  background-image: url('~assets/images/stars.png');
  background-repeat: no-repeat;
  background-size: auto 100%;

  @media (min-width: $breakpoint-small) {
    width: 10.3rem;
    height: 1.7rem;
  }

  > div {
    height: 100%;
    background-image: url('~assets/images/stars-filled.png');
    background-repeat: no-repeat;
    background-size: auto 100%;
  }
}

.info {
  display: flex;
  align-items: center;

  span {
    margin-right: 0.9rem;
  }
}

.desc {
  display: block;
  margin-top: 2.5rem;
  font-size: 1.5rem;
  color: #fff;

  @media (max-width: $breakpoint-small - 1) {
    display: none;
  }

  @media (min-width: 1650px) {
    font-size: 0.9vw;
  }
}

.trailer {
  margin-top: 3rem;

  @media (max-width: $breakpoint-medium - 1) {
    display: none;
  }

  @media (min-width: 1650px) {
    font-size: 0.9vw;
  }
}
</style>

<style lang="scss">
.hero-enter-active,
.hero-leave-active {
  transition: transform .75s cubic-bezier(.4, .25, .3, 1), opacity .3s cubic-bezier(.4, .25, .3, 1);
}

.hero-enter,
.hero-leave-to {
  opacity: 0;
  transform: translate3d(0, 2rem, 0);
}

.hero-enter-to,
.hero-leave {
  opacity: 1;
  transform: translateZ(0);
}
</style>
