import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { PiniaColada } from '@pinia/colada'
import { registerSW } from 'virtual:pwa-register'

import App from './App.vue'
import router from './router'

import './assets/index.css'

// Make
const app = createApp(App)

// Add Pinia with Colada
app.use(createPinia())
app.use(PiniaColada)

// Routing
app.use(router)

// Slets go
app.mount('#app')

// Oh, right, that service worker
registerSW({ immediate: true })
