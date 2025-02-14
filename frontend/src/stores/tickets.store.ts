import type { Ticket, Vendor } from '@/domain'
import { defineStore } from 'pinia'
import { TicketStatus } from '@/domain'

declare interface TicketStore {
    findById(id: string): Ticket | null;
    findAll(): Ticket[];
}

export const useTicketStore = defineStore<'tickets', TicketStore>({
    state: () => ({
        tickets: [],
    }),
    actions: {
        addTicket(vendor: Vendor, subject: string)
    }
})
