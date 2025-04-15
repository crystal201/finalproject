export const state = () => ({
  user: null,
  token: null,
  isAuthenticated: false
});

export const mutations = {
  SET_TOKEN(state, token) {
      state.token = token;
      state.isAuthenticated = !!token;
  },
  SET_USER(state, user) {
      state.user = user;
  }
};

export const actions = {
  checkAuth({ commit }) {
      const token = localStorage.getItem('authToken');
      if (token) {
          commit('SET_TOKEN', token);
          this.$axios.setToken(token, 'Bearer');
      }
  },

  async login({ commit }, credentials) {
      try {
          const response = await this.$axios.post('/api/auth/login', credentials);
          const token = response.data.token;
          localStorage.setItem('authToken', token);
          commit('SET_TOKEN', token);
          this.$axios.setToken(token, 'Bearer');
          return true;
      } catch (error) {
          localStorage.removeItem('authToken'); // Clear stale token
          commit('SET_TOKEN', null);
          throw error;
      }
  },

  logout({ commit }) {
      localStorage.removeItem('authToken');
      commit('SET_TOKEN', null);
      this.$axios.setToken(false);
  }
};