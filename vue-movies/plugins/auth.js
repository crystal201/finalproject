export default function ({ $axios, store }) {
    store.dispatch('checkAuth');
    $axios.onRequest(config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
    });
    $axios.onError(error => {
        if (error.response && error.response.status === 401) {
            localStorage.removeItem('token');
            store.commit('setAuthenticated', false);
            return redirect('/login');
        }
    });
}