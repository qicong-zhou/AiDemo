import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  build: {
    // 构建后直接成为 Spring Boot 的静态资源
    outDir: '../src/main/resources/static',
    emptyOutDir: true,
  },
  server: {
    proxy: {
      // 所有 /memos 开头的请求，都转发给后端 8080
      '/memos': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
