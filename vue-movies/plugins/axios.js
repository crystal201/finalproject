export default function ({ $axios, store }) {
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
        console.error('Unauthorized, please login again');
        // Không gọi logout
        return Promise.reject(new Error('Phiên đăng nhập hết hạn'));
      }
      return Promise.reject(error);
    });
  }