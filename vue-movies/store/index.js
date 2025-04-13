export const state = () => ({
    isAuthenticated: false,
  });
  
  export const mutations = {
    setAuthenticated(state, value) {
      state.isAuthenticated = value;
    },
  };
  
  export const actions = {
    checkAuth ({ commit }) {
      const token = localStorage.getItem('authToken');
      commit('setAuthenticated', !!token);
    },
    async login ({ commit }, { username, password }) {
      try {
        const response = await this.$axios.post('/api/auth/login', { username, password });
        localStorage.setItem('authToken', response.data.token);
        commit('setAuthenticated', true);
        return true;
      } catch (error) {
        localStorage.removeItem('authToken');
        commit('setAuthenticated', false);
        return false;
      }
    },
    logout ({ commit }) {
      localStorage.removeItem('authToken');
      commit('setAuthenticated', false);
    },
  };