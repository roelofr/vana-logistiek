import { TicketStatus } from '@/domain'

export type District = {
    id: number
    name: string
    mobileName: string
    colour: string
}

export type Vendor = {
    id: number
    name: string
    number: string
    district: District
}

export type Ticket = {
    id: number
    name: string
    vendor: Vendor
    status: TicketStatus
}
