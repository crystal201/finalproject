import axios from 'axios';

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
    },
    CLEAR_AUTH(state) {
        state.token = null;
        state.user = null;
        state.isAuthenticated = false;
    },
};

export const actions = {
    async checkAuth({ commit, dispatch }) {
        const token = localStorage.getItem('authToken');
        if (token) {
          try {
            // Xác thực token với server (tùy chọn)
            await this.$axios.get('/api/auth/validate', {
              headers: { Authorization: `Bearer ${token}` },
            });
            commit('SET_TOKEN', token);
            // Tùy chọn: Lấy thông tin user
            // const user = await dispatch('fetchUser');
            // commit('SET_USER', user);
          } catch (error) {
            console.error('Token không hợp lệ:', error);
            dispatch('logout');
          }
        } else {
          commit('CLEAR_AUTH');
        }
      },

      async login({ commit, dispatch }, { username, password }) {
        try {
          const response = await this.$axios.post('/api/auth/login', { username, password });
          const token = response.data.token;
          if (token) {
            localStorage.setItem('authToken', token);
            commit('SET_TOKEN', token);
            this.$axios.setToken(token, 'Bearer');
            await dispatch('fetchUser');
            return true;
          }
          return false;
        } catch (error) {
          console.error('Đăng nhập thất bại:', error);
          throw error; // Ném lỗi để component xử lý
        }
      },

      logout({ commit }) {
        localStorage.removeItem('authToken');
        commit('CLEAR_AUTH');
        this.$axios.setToken(false);
      },
      async fetchUser({ commit }) {
        const response = await this.$axios.get('/api/auth/user');
        commit('SET_USER', response.data);
        return response.data;
      },
};
export const namespaced = true;