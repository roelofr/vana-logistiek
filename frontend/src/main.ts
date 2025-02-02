import { createApp } from 'vue'
import './assets/index.css'
import App from './App.vue'
import { createPinia } from 'pinia'

import router from './router'

const pinia = createPinia()

// Make
const app = createApp(App)

// Improve
app.use(pinia)
app.use(router)

// Slets go
app.mount('#app')
