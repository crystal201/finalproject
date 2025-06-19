module.exports = {
  devServer: {
    port: 8082,
    host: '0.0.0.0',
    proxy: {
      '^/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        pathRewrite: { '^/api': '/api' },
        logLevel: 'debug'
      }
    }
  },
  outputDir: 'dist'
}