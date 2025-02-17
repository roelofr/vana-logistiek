import { createApp } from 'vue'
import './assets/index.css'
import App from './App.vue'
import { createPinia } from 'pinia'
import { PiniaColada } from '@pinia/colada'

import router from './router'

// Make
const app = createApp(App)

// Add Pinia with Colada
app.use(createPinia())
app.use(PiniaColada)

// Routing
app.use(router)

// Slets go
app.mount('#app')
