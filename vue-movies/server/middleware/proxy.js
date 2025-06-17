const httpProxy = require('http-proxy');
const proxy = httpProxy.createProxyServer();

module.exports = function (req, res) {
  if (req.url.startsWith('/api/recommendations')) {
    proxy.web(req, res, {
      target: 'http://recommendation-api:5000', // Sử dụng tên service Docker
      changeOrigin: true,
      // Giữ nguyên path và query parameters
      pathRewrite: {
        '^/api/recommendations': '/recommendations' // Loại bỏ '/api' prefix
      }
    });
  } else if (req.url.startsWith('/api/')) {
    proxy.web(req, res, {
      target: 'http://cinema-system:8080', // Backend
      changeOrigin: true,
      pathRewrite: {
        '^/api': '' // Loại bỏ '/api' prefix
      }
    });
  } else {
    res.statusCode = 404;
    res.end('Not Found');
  }
};
