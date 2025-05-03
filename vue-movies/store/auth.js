import axios from 'axios';

export const state = () => ({
    user: null,
    token: typeof window !== 'undefined' ? localStorage.getItem('authToken') : null,
    isAuthenticated: typeof window !== 'undefined' ? !!localStorage.getItem('authToken') : false,
});

export const mutations = {
    SET_TOKEN(state, token) {
        state.token = token;
        state.isAuthenticated = !!token;
        if (typeof window !== 'undefined') {
            localStorage.setItem('authToken', token);
        }
    },
    SET_USER(state, user) {
        state.user = user;
    },
    SET_USER_ID(state, userId) {
        state.userId = userId;
        if (typeof window !== 'undefined') {
            localStorage.setItem('userId', userId.toString());
        }
    },
    CLEAR_AUTH(state) {
        state.token = null;
        state.user = null;
        state.userId = null;
        state.isAuthenticated = false;
        if (typeof window !== 'undefined') {
            localStorage.removeItem('authToken');
            localStorage.removeItem('userId');
        }
    },
};

export const actions = {
    async checkAuth({ commit, dispatch }) {
        if (typeof window === 'undefined') return;
        const token = localStorage.getItem('authToken');
        if (token) {
            try {
                commit('SET_TOKEN', token);
                await dispatch('fetchUser');
            } catch (error) {
                console.error('Lỗi khôi phục thông tin người dùng:', error);
                dispatch('logout');
            }
        } else {
            commit('CLEAR_AUTH');
        }
    },
    async login({ commit, dispatch }, { username, password }) {
        try {
            const response = await this.$axios.post('/api/auth/login', { username, password });
            const { token, userId } = response.data; // Lấy cả token và userId
            if (token) {
                commit('SET_TOKEN', token);
                commit('SET_USER_ID', userId); // Lưu userId vào state và localStorage
                this.$axios.setToken(token, 'Bearer');
                await dispatch('fetchUser');
                return true;
            }
            return false;
        } catch (error) {
            console.error('Đăng nhập thất bại:', error);
            throw error;
        }
    },
    logout({ commit }) {
        commit('CLEAR_AUTH');
        this.$axios.setToken(false);
    },
    async fetchUser({ commit }) {
        try {
            const response = await this.$axios.get('/api/auth/user');
            commit('SET_USER', response.data);
            commit('SET_USER_ID', response.data.id); // Cập nhật userId từ /api/auth/user
            return response.data;
        } catch (error) {
            console.error('Lỗi lấy thông tin user:', error);
            throw error;
        }
    },
};

export const namespaced = true;