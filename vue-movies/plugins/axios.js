export default function ({ $axios, store }) {
  $axios.defaults.withCredentials = true

  $axios.onRequest(config => {
    const token = store.state.auth.token || localStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  })
}
