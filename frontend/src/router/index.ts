import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView,
            meta: {
                authenticate: true,
            }
        },
        {
            path: '/tickets',
            name: 'tickets',
            // route level code-splitting
            // this generates a separate chunk (About.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => {
                throw new Error('no')
            },
            meta: {
                authenticated: true
            }
        },
        {
            path: '/login',
            name: 'login',
            component: LoginView,
            meta: {
                authenticated: false,
                headless: true,
            }
        }
    ],
})

export default router
