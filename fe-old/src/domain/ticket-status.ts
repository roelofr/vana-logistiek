import type { Component } from 'vue'
import { Heart, MailQuestion, MailWarning, UserCheck } from 'lucide-vue-next'

export enum TicketStatus {
    Created = 'created',
    Assigned = 'assigned',
    Updated = 'updated',
    Resolved = 'resolved',
}

declare interface TicketStatusDetail {
    label: string
    assignable: boolean
    icon: Component
    color: string
}

export const TicketStatusDetails: Map<TicketStatus, TicketStatusDetail> = new Map([
    [
        TicketStatus.Created,
        {
            label: 'Nieuw',
            assignable: false,
            icon: MailQuestion,
            color: 'amber',
        },
    ],
    [
        TicketStatus.Assigned,
        {
            label: 'Verdeeld',
            assignable: true,
            icon: UserCheck,
            color: 'blue',
        },
    ],
    [
        TicketStatus.Updated,
        {
            label: 'Bijgewerkt',
            assignable: false,
            icon: MailWarning,
            color: 'violet',
        },
    ],
    [
        TicketStatus.Resolved,
        {
            label: 'Afgerond',
            assignable: true,
            icon: Heart,
            color: 'green',
        },
    ],
])
