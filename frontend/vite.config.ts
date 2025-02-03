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
            plugins: [
                tailwindcss(),
                autoprefixer()]
        }
    },
    plugins: [
        vue(),
        vueDevtools(),
        vitePWA({
            // Work offline
            workbox: {
                navigateFallbackDenylist: [
                    /^\/api/,
                    /^\/robots\.txt$/
                ]
            },

            // Generate icons
            pwaAssets: {},

            // Generate manifest
            manifest: {
                name: 'LogistiekApp',
                description: 'De MegaSuperApp voor Logistiek',
                background_color: '#020817',
                theme_color: '#0d4298',
                display: 'minimal-ui',
                orientation: 'natural'
            }
        })
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    }
})
