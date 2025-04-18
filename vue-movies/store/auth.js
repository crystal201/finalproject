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
    }
};

export const actions = {
    checkAuth({ commit }) {
        const token = localStorage.getItem('authToken');
        if (token) {
            commit('SET_TOKEN', token);
        }
    },

    async login({ commit }, { username, password }) {
        try {
            const response = await axios.post('/api/auth/login', { username, password }, {
                headers: { 'Content-Type': 'application/json' }
            });
            const token = response.data.token;
            if (token) {
                localStorage.setItem('authToken', token);
                commit('SET_TOKEN', token);
                this.$axios.setToken(token, 'Bearer');
                return true;
            } else {
                return false;
            }
        } catch (error) {
            console.error('Login error:', error);
            return false;
        }
    },

    logout({ commit }) {
        localStorage.removeItem('authToken');
        commit('SET_TOKEN', null);
        this.$axios.setToken(false);
    }
};