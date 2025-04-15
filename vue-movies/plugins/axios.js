export default function ({ $axios, store }) {
    $axios.defaults.withCredentials = false;
    $axios.defaults.timeout = 30000;

    $axios.onRequest(config => {
        if (config.url.includes('/api/auth/login') || config.url.includes('/api/auth/register')) {
            return config;
        }

        const token = store.state.auth.token || localStorage.getItem('authToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    });
}