import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { PiniaColada } from '@pinia/colada'

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
