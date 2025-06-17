export default function ({ $axios, store, redirect }) {
  $axios.defaults.withCredentials = false;
  $axios.defaults.timeout = 30000;

  $axios.onRequest(config => {
    console.log(" Calling API: ", config.url, config.method, config.headers, "BaseURL:", $axios.defaults.baseURL);
    const token = store.state.auth.token || localStorage.getItem('authToken');
    if (token && !config.url.includes('/api/auth/login') && !config.url.includes('/api/users/register')) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  $axios.onError((error) => {
    console.error('Error:', error.response ? error.response.data : error.message);
    if (error.response && error.response.status === 401) {
      store.commit('auth/CLEAR_AUTH');
      redirect(`/login?redirect=${encodeURIComponent(window.location.pathname + window.location.search)}`);
    }
    return Promise.reject(error);
  });
}
