import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  define: {
    global: 'window',
  },
  build: {
    outDir: '../src/main/resources/static', // Spring Boot 정적 파일 경로
    emptyOutDir: true, // 기존 파일 삭제
  },
  server: {
    proxy: {
      // ^/(auth|rooms|messages|users) => localhost:8080 로 요청 보냄
      '^/(auth|rooms|messages|users)': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  resolve: {
    alias: {
      // '@'를 쓸 때마다 실제로는 '/src' 디렉토리를 참조하도록 설정
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
});
