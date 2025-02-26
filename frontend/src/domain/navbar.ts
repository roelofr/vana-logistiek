import type { Component } from 'vue'
import { slug } from '@/lib'
import { Frown, House, Logs } from 'lucide-vue-next'

export interface NavItem {
    id: string
    label: string
    route: string
    icon: Component
}

const item = (route: string, label: string, icon: Component) => ({
    id: slug(`${route}-${label}`),
    route,
    label,
    icon,
})

export const items: NavItem[] = [
    item('home', 'Homepage', House),
    item('ticket', 'Tickets', Logs),
    item('vent', 'Ik wil klagen', Frown),
]
