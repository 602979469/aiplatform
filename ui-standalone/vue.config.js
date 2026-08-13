module.exports = {
  lintOnSave: false,
  devServer: {
    port: 5173,
    // history 路由模式：直接访问 /chat、/mirror 时回退到 index.html
    historyApiFallback: true,
    proxy: {
      // 前端直连后端，/ai 前缀代理到 aiplatform（8080）
      '/ai': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}
