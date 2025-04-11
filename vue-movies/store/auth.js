export const state = () => ({
    auth: { loggedIn: true },
    token: null,
    user: null
  })
  
  export const mutations = {
    SET_TOKEN(state, token) {
      state.token = token
    },
    SET_USER(state, user) {
      state.user = user
    }
  }
  
  export const actions = {
    async login({ commit }, credentials) {
      const response = await this.$axios.$post('/api/auth/login', credentials)
      commit('SET_TOKEN', response.token)
      localStorage.setItem('authToken', response.token)
    },
    async register(_, userData) {
      await this.$axios.$post('/api/auth/register', userData)
    }
  }