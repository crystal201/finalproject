module.exports = {
  devServer: {
    port: 8082,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://157.66.219.181:8081/admin',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  }
}