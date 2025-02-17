import { apiFetch } from '@/api/fetch.ts'
import type { Ticket } from '@/domain'

/**
 * Finds all tickets, as list
 */
export const findAllTickets = async (): Promise<Ticket[]> => {
    const { response, body } = await apiFetch('GET', '/tickets')

    if (response.status !== 200) return []

    if (!body) throw new Error('Response is missing!')

    return body as Ticket[]
}

/**
 * Finds a single ticket, returns null if not found.
 * @param ticketId
 */
export const findTicket = async (ticketId: string): Promise<null | Ticket> => {
    const { response, body } = await apiFetch('GET', `/tickets/${ticketId}`)

    if (response.status === 404) return null

    if (response.status !== 200) throw new Error('Ticket not found!')

    return body as Ticket
}

/**
 * Creates a ticket
 * @param ticket
 */
export const saveTicket = async (ticket: Ticket): Promise<Ticket> => {
    if (!ticket.name) throw new Error('Ticket name is missing!')

    if (!ticket.vendor) throw new Error('Ticket name is missing!')

    const { response, body } = await apiFetch('POST', '/tickets', ticket)
    if (response.status !== 200) throw new Error('Status is missing!')

    return body as Ticket
}
