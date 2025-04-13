export const state = () => ({
    isAuthenticated: false
});

export const mutations = {
    setAuthenticated(state, value) {
        state.isAuthenticated = value;
    }
};

export const actions = {
    checkAuth({ commit }) {
        const token = localStorage.getItem('token');
        commit('setAuthenticated', !!token);
    },
    async login({ commit }, { username, password }) {
        try {
            const response = await this.$axios.post('/api/auth/login', { username, password });
            localStorage.setItem('token', response.data.token);
            commit('setAuthenticated', true);
            return true;
        } catch (error) {
            return false;
        }
    },
    logout({ commit }) {
        localStorage.removeItem('token');
        commit('setAuthenticated', false);
    }
};