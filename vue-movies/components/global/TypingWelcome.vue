<template>
  <div class="typing-welcome" v-if="isAuthenticated && user">
    <span class="welcome-text">{{ displayedText }}</span>
    <span class="cursor" :class="{ blinking: !isTyping }">|</span>
  </div>
</template>

<script>
export default {
  data() {
    return {
      displayedText: '',
      isTyping: true,
      typingSpeed: 100, // milliseconds per character
      pauseDuration: 7000, // milliseconds before restart
    }
  },
  computed: {
    isAuthenticated() {
      return this.$store.state.auth.isAuthenticated
    },
    user() {
      return this.$store.state.auth.user
    },
    fullText() {
      return `Welcome, ${this.user?.username || 'Guest'}`
    }
  },
  mounted() {
    if (this.isAuthenticated) {
      this.typeText()
    }
  },
  methods: {
    typeText() {
      this.isTyping = true
      let i = 0
      this.displayedText = ''
      
      const typingInterval = setInterval(() => {
        if (i < this.fullText.length) {
          this.displayedText += this.fullText.charAt(i)
          i++
        } else {
          clearInterval(typingInterval)
          this.isTyping = false
          setTimeout(() => {
            this.eraseText()
          }, this.pauseDuration)
        }
      }, this.typingSpeed)
    },
    eraseText() {
      this.isTyping = true
      let i = this.fullText.length
      
      const erasingInterval = setInterval(() => {
        if (i > 0) {
          this.displayedText = this.fullText.substring(0, i - 1)
          i--
        } else {
          clearInterval(erasingInterval)
          this.isTyping = false
          setTimeout(() => {
            this.typeText()
          }, 500)
        }
      }, this.typingSpeed / 2)
    }
  }
}
</script>

<style scoped>
.typing-welcome {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 10px 20px;
  border-radius: 30px;
  font-size: 1.2rem;
  z-index: 1000;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.welcome-text {
  margin-right: 5px;
}

.cursor {
  opacity: 1;
  transition: opacity 0.3s;
}

.cursor.blinking {
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}
</style>