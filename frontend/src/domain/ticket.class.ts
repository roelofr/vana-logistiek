import type { User, Vendor } from '@/domain'
import { TicketStatus } from '@/domain'

export interface Ticket {
    id: number
    created_at: Date
    updated_at: Date
    status: TicketStatus
    vendor: Partial<Vendor>
    creator: Partial<User>
    description: string
}
