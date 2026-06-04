import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// GitHub Pages build config — outputs to docs/main-app with correct base path
// Usage: vite build --config vite.config.github.js
export default defineConfig({
  plugins: [react()],
  base: './',   // relative paths so the build works from any GitHub Pages sub-path
  build: {
    outDir: '../docs/main-app',
    emptyOutDir: true,
  }
})
