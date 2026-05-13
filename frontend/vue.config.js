const path = require('path')

function resolve(dir) {
  return path.join(__dirname, dir)
}

const os = require('os')

// 获取本机IP地址
function getNetworkIp() {
  const interfaces = os.networkInterfaces()
  for (const name of Object.keys(interfaces)) {
    for (const iface of interfaces[name]) {
      const { address, family, internal } = iface
      if (family === 'IPv4' && !internal) {
        return address
      }
    }
  }
  return 'localhost'
}

module.exports = {
  publicPath: '/',
  outputDir: 'dist',
  assetsDir: 'static',
  lintOnSave: process.env.NODE_ENV === 'development' ? 'warning' : false,
  productionSourceMap: false,
  devServer: {
    host: '0.0.0.0', // 监听所有网络接口
    port: 8081,
    open: false, // 暂时关闭自动打开，避免干扰
    allowedHosts: 'all',
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      }
    },
    // 启动后的回调
    onListening: function(devServer) {
      if (!devServer) {
        throw new Error('webpack-dev-server is not defined')
      }

      const port = devServer.server.address().port
      const localIp = getNetworkIp()

      console.log('\n')
      console.log('\x1b[36m%s\x1b[0m', '╔══════════════════════════════════════════════════════════╗')
      console.log('\x1b[36m%s\x1b[0m', '║     🚀 前端开发服务器启动成功！                         ║')
      console.log('\x1b[36m%s\x1b[0m', '╚══════════════════════════════════════════════════════════╝')
      console.log('')
      console.log('  \x1b[1m%s\x1b[0m', 'App running at:')
      console.log('  \x1b[32m%s\x1b[0m \x1b[1m%s\x1b[0m', '- Local:  ', `http://localhost:${port}/`)
      console.log('  \x1b[32m%s\x1b[0m \x1b[1m%s\x1b[0m', '- Network:', `http://${localIp}:${port}/`)
      console.log('')
      console.log('  \x1b[33m%s\x1b[0m', '📱 可在同一网络的任何设备访问网络地址')
      console.log('')
      console.log('\x1b[36m%s\x1b[0m', '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━')
      console.log('')
    }
  },
  configureWebpack: {
    resolve: {
      alias: {
        '@': resolve('src')
      }
    }
  },
  chainWebpack(config) {
    config.module
      .rule('svg')
      .exclude.add(resolve('src/assets/icons'))
      .end()

    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/assets/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
      .end()
  }
}
