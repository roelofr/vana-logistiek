import type { Component } from 'vue'
import { Handshake, ShoppingCart, Truck } from 'lucide-vue-next'

export enum Signature {
    Required,
    Optional,
}

export interface TicketType {
    name: string
    label: string
    description: string
    signature: false | Signature
    icon: Component
}

export const ticketTypes: TicketType[] = [
    {
        name: 'order',
        label: 'Bijbestelling',
        description: 'Materialen direct verstrekt aan standhouder',
        signature: Signature.Required,
        icon: ShoppingCart,
    },
    {
        name: 'request',
        label: 'Aanvraag',
        description: 'Materiaalaanvraag bij derden (Gator, heftruck, Keukenhof)',
        signature: Signature.Optional,
        icon: Truck,
    },
    {
        name: 'contact',
        label: 'Lokroep',
        description: 'Speed-dial de hulp van Tessa',
        signature: false,
        icon: Handshake,
    },
]

export const ticketTypesMap = new Map<string, TicketType>(
    ticketTypes.map((type) => [type.name, type]),
)
