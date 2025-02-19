import type { Component } from 'vue'
import { slug } from '@/lib'
import { Frown, House, Logs } from 'lucide-vue-next'

declare interface NavItem {
    id: string
    label: string
    href: string
    icon: Component
}

const item = (href: string, label: string, icon: Component) => ({
    id: slug(`${href}-${label}`),
    href,
    label,
    icon,
})

export const items: NavItem[] = [
    item('/', 'Homepage', House),
    item('/tickets', 'Tickets', Logs),
    item('/klaag', 'Ik wil klagen', Frown),
]
