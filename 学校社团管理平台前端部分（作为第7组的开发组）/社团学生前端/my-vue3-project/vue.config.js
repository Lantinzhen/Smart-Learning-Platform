const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true
})
module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://10.34.58.66:9099',
        changeOrigin: true,
        pathRewrite: { '^/api': '' }
      }
    }
  }
};