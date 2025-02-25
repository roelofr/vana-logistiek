/// <reference types="vitest/config" />
/// <reference types="@vitest/browser/providers/playwright" />

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import autoprefixer from 'autoprefixer'
import tailwindcss from 'tailwindcss'
import vueDevtools from 'vite-plugin-vue-devtools'
import { fileURLToPath, URL } from 'node:url'
import { VitePWA as vitePWA } from 'vite-plugin-pwa'

// https://vite.dev/config/
export default defineConfig({
    css: {
        postcss: {
            plugins: [tailwindcss(), autoprefixer()],
        },
    },
    define: {
        'import.meta.env.VITE_APP_VERSION': `"${process.env.npm_package_version}"`,
    },
    plugins: [
        vue(),
        vueDevtools({
            launchEditor: 'idea',
        }),
        vitePWA({
            // Auto-update
            registerType: 'autoUpdate',
            strategies: 'injectManifest',
            srcDir: 'src/serviceworker',
            filename: 'sw.ts',

            // Configure default Workbox behaviour
            workbox: {
                navigateFallbackDenylist: [/^\/api/, /^\/robots\.txt$/],
            },

            // Generate manifest
            includeAssets: ['favicon.ico', 'apple-touch-icon.png', 'mask-icon.svg'],
            manifest: {
                name: 'LogistiekApp',
                description: 'De MegaSuperApp voor Logistiek',
                background_color: '#020817',
                theme_color: '#0d4298',
                display: 'minimal-ui',
                orientation: 'natural',
                icons: [],
            },
        }),
    ],
    test: {
        reporters: ['default', 'junit'],
        outputFile: {
            junit: './logs/junit.xml',
        },
        includeTaskLocation: true,
        browser: {
            enabled: true,
            headless: true,
            provider: 'playwright',
            instances: [{ browser: 'chromium' }],
            screenshotFailures: true,
            screenshotDirectory: './logs/screenshots',
        },
    },
    optimizeDeps: {
        include: ['vue'],
    },
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url)),
        },
    },
    server: {
        port: 8001,
        proxy: {
            '/api': {
                target: `http://localhost:8000`,
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, ''),
            },
        },
    },
})
