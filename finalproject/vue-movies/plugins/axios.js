export default function ({ $axios, store, redirect }) {
    $axios.defaults.withCredentials = false;
    $axios.defaults.timeout = 30000;

    $axios.onRequest(config => {
        if (
            config.url.includes('/api/auth/login') ||
            config.url.includes('/api/auth/register') ||
            config.url.includes('api.themoviedb.org')
        ) {
            return config;
        }

        const token = store.state.auth.token || localStorage.getItem('authToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    });

    $axios.onError((error) => {
        if (error.response && error.response.status === 401) {
            store.commit('auth/CLEAR_AUTH');
            redirect(`/login?redirect=${encodeURIComponent(window.location.pathname + window.location.search)}`);
        }
        return Promise.reject(error);
    });
}