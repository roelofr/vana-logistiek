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
            },
        },
        {
            path: '/klaag',
            name: 'venting',
            component: () => import('@/views/VentingView.vue'),
        },
        {
            path: '/tickets',
            name: 'ticket',
            // route level code-splitting
            // this generates a separate chunk (About.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('@/views/TicketView.vue'),
            meta: {
                authenticated: true,
            },
        },
        {
            path: '/ticket/:id',
            name: 'ticket.show',
            props: true,
            component: () => import('@/views/TicketDetailView.vue'),
            meta: {
                authenticated: true,
            },
        },
        {
            path: '/ticket/nieuw',
            name: 'ticket.create',
            component: () => import('@/views/TicketCreateView.vue'),
        },
        {
            path: '/login',
            name: 'login',
            component: LoginView,
            meta: {
                authenticated: false,
                headless: true,
            },
        },
    ],
})

export default router
