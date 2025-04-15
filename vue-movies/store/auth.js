// ~/store/auth.js
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
  // Action kiểm tra đăng nhập khi khởi động app
  checkAuth({ commit }) {
    const token = localStorage.getItem('authToken'); // Thống nhất dùng authToken
    if (token) {
      commit('SET_TOKEN', token);
      this.$axios.setToken(token, 'Bearer');
    }
  },

  // Action xử lý đăng nhập
  async login({ commit }, credentials) {
    try {
      const response = await this.$axios.post('/api/auth/login', credentials);
      const token = response.data.token;
      
      localStorage.setItem('authToken', token); // Luôn dùng authToken
      commit('SET_TOKEN', token);
      this.$axios.setToken(token, 'Bearer');
      
      return true;
    } catch (error) {
      localStorage.removeItem('authToken');
      commit('SET_TOKEN', null);
      return false;
    }
  },

  // Action xử lý đăng xuất
  logout({ commit }) {
    localStorage.removeItem('authToken');
    commit('SET_TOKEN', null);
    this.$axios.setToken(false); // Xóa token khỏi Axios
  }
};