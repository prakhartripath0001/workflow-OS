import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    strictPort: true,
  },
  base: './',   // Required for Electron to load assets from relative paths in production
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
  },
})
