export default function ({ store }) {
    try {
      store.dispatch('auth/checkAuth')
    } catch (error) {
      console.error('Auth check failed:', error)
    }
  }